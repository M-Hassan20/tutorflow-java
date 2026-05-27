package com.tutorflow.dryrunservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.tutorflow.dryrunservice.dto.DryRunRequest;
import com.tutorflow.dryrunservice.dto.DryRunResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DryRunService {

    @Value("${execution.timeout-seconds}")
    private int timeoutSeconds;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private DockerClient dockerClient;

//    @Value("${docker.host:npipe:////./pipe/docker_engine}")
//    private String dockerHost;


    @PostConstruct
    public void init() {
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("npipe:////./pipe/docker_engine")
                .build();
        var httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();
        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }
    public DryRunResponse dryRun(DryRunRequest request) {
        Path tempDir = null;
        String containerId = null;

        try {
            tempDir = Files.createTempDirectory("tutorflow-dryrun-");

            // Copy tracer.py from resources to temp dir
            var tracerStream = getClass().getClassLoader()
                    .getResourceAsStream("tracer.py");
            if (tracerStream == null) {
                throw new RuntimeException("tracer.py not found in resources");
            }
            Files.copy(tracerStream, tempDir.resolve("tracer.py"),
                    StandardCopyOption.REPLACE_EXISTING);

            // Encode code and stdin as base64 to avoid shell escaping issues
            String codeB64 = Base64.getEncoder()
                    .encodeToString(request.getCode().getBytes());
            String stdinB64 = request.getStdin() != null
                    ? Base64.getEncoder().encodeToString(
                    request.getStdin().getBytes())
                    : "";

            String[] cmd = {"python", "tracer.py", codeB64, stdinB64};

            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withMemory(128L * 1024 * 1024)
                    .withNetworkMode("none")
                    .withBinds(new Bind(
                            tempDir.toAbsolutePath().toString(),
                            new Volume("/app")));

            CreateContainerResponse container = dockerClient
                    .createContainerCmd("python:3.11-alpine")
                    .withCmd(cmd)
                    .withWorkingDir("/app")
                    .withHostConfig(hostConfig)
                    .withNetworkDisabled(true)
                    .exec();

            containerId = container.getId();
            dockerClient.startContainerCmd(containerId).exec();

            boolean completed = dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion(timeoutSeconds, TimeUnit.SECONDS);

            if (!completed) {
                dockerClient.killContainerCmd(containerId).exec();
                return DryRunResponse.builder()
                        .steps(List.of())
                        .totalSteps(0)
                        .timedOut(true)
                        .error("Dry run timed out after " + timeoutSeconds + " seconds")
                        .build();
            }

            // Collect stdout
            StringBuilder stdout = new StringBuilder();
            StringBuilder stderr = new StringBuilder();

            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new LogContainerResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            String line = new String(frame.getPayload());
                            if (frame.getStreamType() == StreamType.STDOUT) {
                                stdout.append(line);
                            } else {
                                stderr.append(line);
                            }
                        }
                    }).awaitCompletion();

            if (!stderr.isEmpty()) {
                log.warn("Tracer stderr: {}", stderr);
            }

            // Parse JSON result from tracer
            String output = stdout.toString().trim();
            log.info("Raw tracer output: {}", output);

// Extract JSON - find the first '{' to skip any print output
            int jsonStart = output.indexOf('{');
            if (jsonStart == -1) {
                return DryRunResponse.builder()
                        .steps(List.of())
                        .totalSteps(0)
                        .error("No JSON output from tracer: " + output)
                        .build();
            }
            output = output.substring(jsonStart);
            if (output.isEmpty()) {
                return DryRunResponse.builder()
                        .steps(List.of())
                        .totalSteps(0)
                        .error("No output from tracer: " + stderr)
                        .build();
            }

            var rootNode = objectMapper.readTree(output);

            List<Map<String, Object>> steps = objectMapper.convertValue(
                    rootNode.get("steps"),
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class,
                            objectMapper.getTypeFactory().constructMapType(
                                    Map.class, String.class, Object.class)));

            int totalSteps = rootNode.get("totalSteps").asInt();
            boolean truncated = rootNode.get("truncated").asBoolean();

            return DryRunResponse.builder()
                    .steps(steps)
                    .totalSteps(totalSteps)
                    .truncated(truncated)
                    .timedOut(false)
                    .build();

        } catch (Exception e) {
            log.error("Dry run error", e);
            return DryRunResponse.builder()
                    .steps(List.of())
                    .totalSteps(0)
                    .error("Internal error: " + e.getMessage())
                    .build();
        } finally {
            if (containerId != null) {
                try {
                    dockerClient.removeContainerCmd(containerId)
                            .withForce(true).exec();
                } catch (Exception e) {
                    log.warn("Failed to remove container {}", containerId);
                }
            }
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                            .sorted((a, b) -> -a.compareTo(b))
                            .forEach(p -> p.toFile().delete());
                } catch (Exception e) {
                    log.warn("Failed to clean temp dir");
                }
            }
        }
    }
}
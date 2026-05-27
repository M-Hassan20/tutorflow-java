package com.tutorflow.executionservice.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.tutorflow.executionservice.dto.ExecutionResponse;
import com.tutorflow.executionservice.dto.ExecutionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ExecutionService {

    @Value("${execution.timeout-seconds}")
    private int timeoutSeconds;

    @Value("${execution.memory-limit-mb}")
    private int memoryLimitMb;

    private final DockerClient dockerClient;

    public ExecutionService() {
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    public ExecutionResponse execute(ExecutionRequest request) {
        Path tempDir = null;
        String containerId = null;
        long startTime = System.currentTimeMillis();

        try {
            // Write code to a temp file
            tempDir = Files.createTempDirectory("tutorflow-exec-");
            Path codeFile = tempDir.resolve("solution.py");
            Files.writeString(codeFile, request.getCode());

            // Write stdin to a file if provided
            String[] cmd;
            if (request.getStdin() != null && !request.getStdin().isBlank()) {
                Path stdinFile = tempDir.resolve("stdin.txt");
                Files.writeString(stdinFile, request.getStdin());
                cmd = new String[]{"sh", "-c", "python solution.py < stdin.txt"};
            } else {
                cmd = new String[]{"python", "solution.py"};
            }

            // Create container
            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withMemory((long) memoryLimitMb * 1024 * 1024)
                    .withNetworkMode("none")
                    .withBinds(new Bind(tempDir.toAbsolutePath().toString(),
                            new Volume("/app")));

            CreateContainerResponse container = dockerClient.createContainerCmd("python:3.11-alpine")
                    .withCmd(cmd)
                    .withWorkingDir("/app")
                    .withHostConfig(hostConfig)
                    .withNetworkDisabled(true)
                    .exec();

            containerId = container.getId();

            // Start container
            dockerClient.startContainerCmd(containerId).exec();

            // Wait for completion with timeout
            boolean completed = dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion(timeoutSeconds, TimeUnit.SECONDS);

            long executionTime = System.currentTimeMillis() - startTime;

            if (!completed) {
                dockerClient.killContainerCmd(containerId).exec();
                return ExecutionResponse.builder()
                        .stdout("")
                        .stderr("Execution timed out after " + timeoutSeconds + " seconds")
                        .exitCode(-1)
                        .executionTimeMs(executionTime)
                        .timedOut(true)
                        .build();
            }

            // Get exit code
            int exitCode = dockerClient.inspectContainerCmd(containerId)
                    .exec()
                    .getState()
                    .getExitCodeLong()
                    .intValue();

            // Collect logs
            StringBuilder stdout = new StringBuilder();
            StringBuilder stderr = new StringBuilder();

            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new com.github.dockerjava.core.command.LogContainerResultCallback() {
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

            return ExecutionResponse.builder()
                    .stdout(stdout.toString())
                    .stderr(stderr.toString())
                    .exitCode(exitCode)
                    .executionTimeMs(executionTime)
                    .timedOut(false)
                    .build();

        } catch (Exception e) {
            log.error("Execution error", e);
            return ExecutionResponse.builder()
                    .stdout("")
                    .stderr("Internal execution error: " + e.getMessage())
                    .exitCode(-1)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .timedOut(false)
                    .build();
        } finally {
            // Always clean up
            if (containerId != null) {
                try {
                    dockerClient.removeContainerCmd(containerId).withForce(true).exec();
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
                    log.warn("Failed to clean up temp dir");
                }
            }
        }
    }
}
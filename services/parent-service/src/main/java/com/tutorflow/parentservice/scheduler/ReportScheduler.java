package com.tutorflow.parentservice.scheduler;

import com.tutorflow.parentservice.service.ProgressReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportScheduler {

    private final ProgressReportService progressReportService;

    // Every Monday at 8am
    @Scheduled(cron = "${reports.schedule.weekly}")
    public void sendWeeklyReports() {
        log.info("Sending weekly progress reports...");
        progressReportService.sendReportsForAllParents();
        log.info("Weekly reports sent.");
    }

    // 1st of every month at 8am
    @Scheduled(cron = "${reports.schedule.monthly}")
    public void sendMonthlyReports() {
        log.info("Sending monthly progress reports...");
        progressReportService.sendReportsForAllParents();
        log.info("Monthly reports sent.");
    }
}
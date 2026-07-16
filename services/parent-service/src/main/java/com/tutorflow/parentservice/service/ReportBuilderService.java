package com.tutorflow.parentservice.service;

import com.tutorflow.parentservice.dto.ProgressReportData;
import org.springframework.stereotype.Service;

@Service
public class ReportBuilderService {
    public String buildHtml(ProgressReportData data) {
        StringBuilder html = new StringBuilder();
        html.append("""
                <html>
                                <head>
                                <style>
                                    body { font-family: Arial, sans-serif; color: #333; padding: 20px; }
                                    h1 { color: #4A90D9; }
                                    h2 { color: #555; border-bottom: 1px solid #eee; padding-bottom: 6px; }
                                    table { width: 100%%; border-collapse: collapse; margin-top: 10px; }
                                    th { background: #4A90D9; color: white; padding: 8px; text-align: left; }
                                    td { padding: 8px; border-bottom: 1px solid #eee; }
                                    .passed { color: green; font-weight: bold; }
                                    .failed { color: red; }
                                    .xp { color: #FF9500; font-weight: bold; }
                                    .footer { margin-top: 30px; font-size: 12px; color: #999; }
                                </style>
                                </head>
                                <body>
                """);
        html.append("<h1>Tutorflow Progress Report</h1>");
        html.append("<p>Here is the latest progress summary " + "for student ID: <strong>").append(data.getStudentId()).append("</strong</p>");

        if(data.getClassrooms() == null || data.getClassrooms().isEmpty()) {
            html.append("<p>No classroom activity to report.</p>");
        } else {
            for(ProgressReportData.ClassroomReport cr : data.getClassrooms()) {
                html.append("<h2>").append(cr.getClassroomName()).append("</h2>");

                html.append("<table>");
                html.append("<tr><th>Metric</th><th>Value</th></tr>");

                html.append("<tr><td>Total Assignments</td><td>")
                        .append(cr.getTotalAssignments())
                        .append("</td></tr>");

                html.append("<tr><td>Submitted</td><td>")
                        .append(cr.getSubmitted())
                        .append("</td></tr>");

                html.append("<tr><td>Passed</td><td class='passed'>")
                        .append(cr.getPassed())
                        .append("</td></tr>");

                int passRate = cr.getTotalAssignments() > 0 ? (cr.getPassed() * 100 / cr.getTotalAssignments()) : 0;

                html.append("<tr><td>Pass Rate</td><td>")
                        .append(passRate)
                        .append("%</td></tr>");

                html.append("<tr><td>Total XP</td><td class='xp'>")
                        .append(cr.getTotalXP())
                        .append(" XP</td></tr>");

                html.append("<tr><td>Leaderboard Rank</td><td>#")
                        .append(cr.getRank())
                        .append("</td></tr>");

                html.append("</table>");
            }
        }
        html.append("""
                <div class='footer'>
                    This report was generated automatically by TutorFlow.
                </div>
                </body>
                </html>
                """);

        return html.toString();
    }
}

package Controller;

import Model.DBConnection;
import Model.Report;
import Model.ResumeData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class MainController {

    public static Report process(String username, String resumePath) {
        ResumeData data = ResumeParser.parse(resumePath);
        int matchCount = data.skillMatches;

        int totalKeywords = 6; // default fallback
        try {
            Map<String, Object> jobDescInfo = ResumeParser.parseJobDescription();
            List<String> jobSkills = (List<String>) jobDescInfo.get("jobSkills");
            totalKeywords = jobSkills.size();
        } catch (Exception e) {
            System.out.println("Using default totalKeywords: " + totalKeywords);
        }

        Report report = ReportGenerator.generate(data, matchCount, totalKeywords);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO reports (username, filename) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, report.filename);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return report;
    }
}
	

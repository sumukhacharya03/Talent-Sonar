package Controller;

import Model.ResumeData;
import Model.Report;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportGenerator {

    public static Report generate(ResumeData data, int matchCount, int totalKeywords) {
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("RESUME MATCHING REPORT\n");
        report.append("----------------------\n\n");
        
        // Candidate Info
        report.append("CANDIDATE DETAILS:\n");
        report.append("------------------\n");
        report.append(String.format("Name: %-20s\n", data.name));
        report.append(String.format("Email: %-20s\n", data.email));
        report.append("\n");
        
        // Job Info
        report.append("JOB DESCRIPTION:\n");
        report.append("----------------\n");
        report.append(String.format("Job Title: %-20s\n", data.jobTitle));
        report.append(String.format("Company: %-20s\n", data.company));
        report.append(String.format("Location: %-20s\n", data.location));
        report.append("\n");
        
        // Skill Matching
        report.append("SKILLS ANALYSIS\n");
        report.append("---------------\n\n");
        
        // Required Skills
        report.append("REQUIRED SKILLS:\n");
        report.append("----------------\n");
        report.append(String.format("Match Percentage: %.1f%% (%d/%d)\n", 
            data.requiredMatchPercentage, 
            data.matchedRequiredSkills.size(), 
            data.matchedRequiredSkills.size() + data.missingRequiredSkills.size()));
        
        if (!data.matchedRequiredSkills.isEmpty()) {
            report.append("Matched: " + String.join(", ", data.matchedRequiredSkills) + "\n");
        }
        if (!data.missingRequiredSkills.isEmpty()) {
            report.append("Missing: " + String.join(", ", data.missingRequiredSkills) + "\n");
        }
        
        // Preferred Skills
        report.append("\nPREFERRED SKILLS:\n");
        report.append("-------------------\n");
        report.append(String.format("Match Percentage: %.1f%% (%d/%d)\n", 
            data.preferredMatchPercentage, 
            data.matchedPreferredSkills.size(), 
            data.matchedPreferredSkills.size() + data.missingPreferredSkills.size()));
        
        if (!data.matchedPreferredSkills.isEmpty()) {
            report.append("Matched: " + String.join(", ", data.matchedPreferredSkills) + "\n");
        }
        if (!data.missingPreferredSkills.isEmpty()) {
            report.append("Missing: " + String.join(", ", data.missingPreferredSkills) + "\n");
        }
        
        // Weighted Score
        report.append("\nWEIGHTED MATCH SCORE:");
        report.append(String.format("%.1f%%\n", data.weightedScore));
        report.append("\n");
        
        // Recommendation
        String recommendation;
        if (data.weightedScore >= 80) {
            recommendation = "STRONG MATCH - RECOMMENDED";
        } else if (data.weightedScore >= 60) {
            recommendation = "GOOD MATCH - WORTH CONSIDERING";
        } else if (data.weightedScore >= 40) {
            recommendation = "MODERATE MATCH - REVIEW FURTHER";
        } else {
            recommendation = "LOW MATCH - NOT RECOMMENDED";
        }
        
        report.append("FINAL RECOMMENDATION:\n");
        report.append("---------------------\n");
        report.append(String.format("%-36s\n", recommendation));

        String filename = "data/reports/Report_" +
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".txt";

        try {
            new File("data/reports/").mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(report.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Report(report.toString(), filename);
    }
}


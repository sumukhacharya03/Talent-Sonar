package Model;

import java.util.List;

public class ResumeData {
    public String name, email, experience;
    public int skillMatches;
    public String jobTitle, company, location;
    public List<String> matchedRequiredSkills;
    public List<String> matchedPreferredSkills;
    public List<String> missingRequiredSkills;
    public List<String> missingPreferredSkills;
    public double requiredMatchPercentage;
    public double preferredMatchPercentage;
    public double weightedScore;

    public ResumeData(String name, String email, String skills, String experience, int skillMatches) {
        this.name = name;
        this.email = email;
        this.experience = experience;
        this.skillMatches = skillMatches;
    }
}

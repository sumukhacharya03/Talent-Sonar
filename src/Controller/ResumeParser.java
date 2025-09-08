package Controller;

import Model.ResumeData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class ResumeParser {
    public static ResumeData parse(String filePath) {
        String name = "Not found", email = "Not found", skills = "Not found", experience = "Not found";
        String resumeText = "";

        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDFTextStripper stripper = new PDFTextStripper();
            resumeText = stripper.getText(document);
            document.close();

            name = extractName(resumeText);
            email = extractEmail(resumeText);
            skills = extractSkills(resumeText);
            experience = extractLine(resumeText, "Experience");

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> resumeSkills = extractIndividualSkills(skills);
        Map<String, Object> jobDescInfo = parseJobDescription();
        
        // Get required and preferred skills from job description
        List<String> requiredSkills = (List<String>) jobDescInfo.get("requiredSkills");
        List<String> preferredSkills = (List<String>) jobDescInfo.get("preferredSkills");
        
        // Find matches
        List<String> matchedRequired = findSkillMatches(resumeSkills, requiredSkills, resumeText.toLowerCase());
        List<String> matchedPreferred = findSkillMatches(resumeSkills, preferredSkills, resumeText.toLowerCase());
        
        // Calculate percentages
        double requiredMatchPct = requiredSkills.isEmpty() ? 0 : 
            (double) matchedRequired.size() / requiredSkills.size() * 100;
        double preferredMatchPct = preferredSkills.isEmpty() ? 0 : 
            (double) matchedPreferred.size() / preferredSkills.size() * 100;
        
        // Calculate weighted score (75% required, 25% preferred)
        double weightedScore = (requiredMatchPct * 0.75) + (preferredMatchPct * 0.25);
        
        ResumeData data = new ResumeData(name, email, "", experience, (int) weightedScore);
        data.jobTitle = (String) jobDescInfo.get("title");
        data.company = (String) jobDescInfo.get("company");
        data.location = (String) jobDescInfo.get("location");
        data.matchedRequiredSkills = matchedRequired;
        data.matchedPreferredSkills = matchedPreferred;
        data.missingRequiredSkills = findMissingSkills(requiredSkills, matchedRequired);
        data.missingPreferredSkills = findMissingSkills(preferredSkills, matchedPreferred);
        data.requiredMatchPercentage = requiredMatchPct;
        data.preferredMatchPercentage = preferredMatchPct;
        data.weightedScore = weightedScore;
        
        return data;
    }

    private static List<String> findMissingSkills(List<String> allSkills, List<String> matchedSkills) {
        List<String> missing = new ArrayList<>();
        for (String skill : allSkills) {
            if (!matchedSkills.contains(skill)) {
                missing.add(skill);
            }
        }
        return missing;
    }

    private static List<String> findSkillMatches(List<String> resumeSkills, List<String> jobSkills, String resumeTextLower) {
        List<String> matches = new ArrayList<>();
        
        // Define skill variations
        Map<String, List<String>> skillVariations = new HashMap<>();
        skillVariations.put("python", Arrays.asList("python"));
        skillVariations.put("pandas", Arrays.asList("pandas"));
        skillVariations.put("numpy", Arrays.asList("numpy"));
        skillVariations.put("scikit-learn", Arrays.asList("scikit-learn", "sklearn"));
        skillVariations.put("tensorflow", Arrays.asList("tensorflow"));
        skillVariations.put("pytorch", Arrays.asList("pytorch"));
        skillVariations.put("sql", Arrays.asList("sql", "mysql", "postgresql"));
        skillVariations.put("spark", Arrays.asList("spark", "apache spark"));
        skillVariations.put("hadoop", Arrays.asList("hadoop", "apache hadoop"));
        skillVariations.put("aws", Arrays.asList("aws", "amazon web services"));
        skillVariations.put("gcp", Arrays.asList("gcp", "google cloud"));
        skillVariations.put("machine learning", Arrays.asList("machine learning", "ml"));
        skillVariations.put("data analysis", Arrays.asList("data analysis", "data analytics"));
        skillVariations.put("data visualization", Arrays.asList("data visualization", "visualization"));
        skillVariations.put("statistical modeling", Arrays.asList("statistical modeling", "statistical models"));
        skillVariations.put("deep learning", Arrays.asList("deep learning", "neural networks"));
        
        for (String jobSkill : jobSkills) {
            List<String> variations = skillVariations.getOrDefault(jobSkill.toLowerCase(), 
                Collections.singletonList(jobSkill.toLowerCase()));
            
            for (String variation : variations) {
                if (resumeSkills.stream().anyMatch(s -> s.toLowerCase().contains(variation)) ||
                    resumeTextLower.contains(variation)) {
                    matches.add(jobSkill);
                    break;
                }
            }
        }
        
        return matches;
    }

    private static String extractLine(String text, String keyword) {
        for (String line : text.split("\n")) {
            if (line.toLowerCase().contains(keyword.toLowerCase())) {
                return line.split(":", 2).length > 1 ? line.split(":", 2)[1].trim() : line.trim();
            }
        }
        return "Not found";
    }

    private static String extractName(String text) {
        String nameRegex = "([A-Z][a-z]+\\s[A-Z][a-z]+)";
        Matcher m = Pattern.compile(nameRegex).matcher(text);
        return m.find() ? m.group(1) : "Not found";
    }

    private static String extractEmail(String text) {
        Matcher m = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}").matcher(text);
        return m.find() ? m.group() : "Not found";
    }

    private static String extractSkills(String text) {
        StringBuilder sb = new StringBuilder();
        boolean inSection = false;
        for (String line : text.split("\n")) {
            if (line.toLowerCase().contains("skills")) inSection = true;
            else if (inSection && line.trim().isEmpty()) break;
            if (inSection) sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static List<String> extractIndividualSkills(String skillsSection) {
        List<String> list = new ArrayList<>();
        for (String line : skillsSection.split("\n")) {
            line = line.replaceAll("^[•\\-]\\s*", "")
                       .replaceAll("(?i)^.*?:", "")
                       .trim();
            String[] tokens = line.split("[,;/]\\s*|\\s+and\\s+|\\s+");
            for (String token : tokens) {
                if (!token.isEmpty()) list.add(token.trim());
            }
        }
        return list;
    }

    public static Map<String, Object> parseJobDescription() {
    Map<String, Object> map = new HashMap<>();
    List<String> requiredSkills = new ArrayList<>();
    List<String> preferredSkills = new ArrayList<>();
    String title = "", company = "", location = "";

    try (Scanner scanner = new Scanner(new File("data/resources/job_description.txt"))) {
        boolean inRequired = false;
        boolean inPreferred = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.toLowerCase().startsWith("job title:")) {
                title = line.split(":", 2)[1].trim();
            } else if (line.toLowerCase().startsWith("company:")) {
                company = line.split(":", 2)[1].trim();
            } else if (line.toLowerCase().startsWith("location:")) {
                location = line.split(":", 2)[1].trim();
            } else if (line.toLowerCase().contains("required skills:")) {
                inRequired = true;
                inPreferred = false;
            } else if (line.toLowerCase().contains("preferred skills:")) {
                inPreferred = true;
                inRequired = false;
            } else if (inRequired && line.matches("^\\d+\\.\\s+.+")) {
                // Extract required skill (remove numbering)
                String skill = line.replaceFirst("^\\d+\\.\\s+", "").trim();
                if (!skill.isEmpty()) {
                    requiredSkills.add(skill);
                }
            } else if (inPreferred && line.matches("^\\d+\\.\\s+.+")) {
                // Extract preferred skill (remove numbering)
                String skill = line.replaceFirst("^\\d+\\.\\s+", "").trim();
                if (!skill.isEmpty()) {
                    preferredSkills.add(skill);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    map.put("requiredSkills", requiredSkills);
    map.put("preferredSkills", preferredSkills);
    map.put("title", title);
    map.put("company", company);
    map.put("location", location);
    return map;
}

    private static void extractSkillsFromLine(String line, List<String> skillsList) {
        // Extract skills from bullet points
        String[] skillKeywords = {
            "python", "pandas", "numpy", "scikit-learn", "tensorflow", "pytorch",
            "sql", "spark", "hadoop", "aws", "gcp", "machine learning", "data analysis",
            "data visualization", "statistical modeling", "deep learning"
        };
        
        String lineLower = line.toLowerCase();
        for (String skill : skillKeywords) {
            if (lineLower.contains(skill)) {
                if (!skillsList.contains(skill)) {
                    skillsList.add(skill);
                }
            }
        }
    }
}

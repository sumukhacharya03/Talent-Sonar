package Controller;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class KeywordAnalyzer {

    public static String[] extractKeywords() {
        try {
            String jobDesc = Files.readString(Paths.get("data/resources/job_description.txt"));
            String[] keywords = jobDesc.toLowerCase().split("\\W+");
            return keywords;
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static int matchCount(String resumeText, String[] keywords) {
        int count = 0;
        String resumeTextLower = resumeText.toLowerCase();
        
        for (String keyword : keywords) {
            if (keyword.length() < 3) continue;
            if (resumeTextLower.contains(keyword.toLowerCase())) {
                count++;
            }
        }
        
        return count;
    }
}

package View;
import Controller.MainController;
import Model.Report;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class MainUI extends JFrame {

    private JTextArea reportArea;
    private String currentReportPath;

    public MainUI() {
        setTitle("Resume Scanner");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        // Report display area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        // Download button panel
        JPanel downloadPanel = createDownloadPanel();
        add(downloadPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JTextField userField = new JTextField(15);
        JButton selectResumeBtn = new JButton("Select Resume");
        JButton scanBtn = new JButton("Scan & Generate");

        selectResumeBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("data/resumes/");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                currentReportPath = fileChooser.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(null, "Resume selected: " + fileChooser.getSelectedFile().getName());
            }
        });

        scanBtn.addActionListener(e -> {
            if (currentReportPath == null || userField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a resume and enter username");
                return;
            }
            
            try {
                Report report = MainController.process(userField.getText(), currentReportPath);
                reportArea.setText(report.content);
                currentReportPath = report.filename; // Save the generated report path
            } catch (Exception ex) {
                reportArea.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(selectResumeBtn);
        panel.add(scanBtn);
        return panel;
    }

    private JPanel createDownloadPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton downloadBtn = new JButton("Download Report");
        
        downloadBtn.addActionListener(e -> {
            if (currentReportPath == null || reportArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No report available to download");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report As");
            fileChooser.setSelectedFile(new File("Resume_Report.txt"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                    writer.write(reportArea.getText());
                    JOptionPane.showMessageDialog(null, "Report saved to:\n" + fileToSave.getAbsolutePath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error saving file: " + ex.getMessage());
                }
            }
        });

        panel.add(new JLabel("Want to save the report? "));
        panel.add(downloadBtn);
        return panel;
    }
}

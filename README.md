# Talent-Sonar
An intelligent ATS-like tool that scans PDF resumes, analyzes skills against job descriptions, and pinpoints the best candidates from the talent pool. 

## What is the Project about?

A Java Swing desktop application that automates resume screening. It parses PDF resumes, compares them against a predefined job description to identify matching and missing skills, and generates a detailed match report with a weighted score. The application logs each scan to a MySQL database.

## Architecture

<img width="1195" height="678" alt="Screenshot from 2025-09-08 09-50-20" src="https://github.com/user-attachments/assets/70f6170b-adac-4b3b-a14e-8ae3f9034603" />

## How to Run the Project

Follow these three steps to get the application running.

### 1. Set up the Database

Run the following SQL script in your MySQL server to create the necessary database and table.

```sql
CREATE DATABASE IF NOT EXISTS resumescanner;
USE resumescanner;

CREATE TABLE IF NOT EXISTS reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    scan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Configure DB Connection

Open the file src/Model/DBConnection.java and update the USER and PASSWORD fields with your MySQL credentials.

```java
private static final String USER = "your_mysql_user";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Compile and Run

Execute the provided shell script from your project's root directory. It will compile the source code and launch the application.

```bash
chmod +x build.sh
./build.sh
```


package edu.lclark.sortinghat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.PrintWriter;

public class Output {

    private Section section;
    private SortingHat sortingHat;
    private SortingHatMain sortingHatMain;
    private HungarianAlgorithm hungarianAlgorithm;
    private Student student;
    private FileWriter csvOutput;
    ArrayList<Section> sections;
    ArrayList<Student> students;

    // Constructor creates Output
    public Output(ArrayList<Section> sections, ArrayList<Student> students) {

        this.sections = sections;
        this.students = students;
        StringBuilder sb = new StringBuilder();
        String outputFile = "eanddsorted.csv";
        String userHomeFolder = System.getProperty("user.home");

        try {
            // before we open the file check to see if it already exists
            boolean alreadyExists = new File(userHomeFolder, outputFile).exists();

            // use FileWriter constructor that specifies open for appending
            int fileEnd = 2;
            while (alreadyExists) {
                String testOutputFileName = "eanddsorted(" + Integer.toString(fileEnd) + ").csv";
                alreadyExists = new File(userHomeFolder, testOutputFileName).exists();
                fileEnd++;
                outputFile = testOutputFileName;
            }

            PrintWriter pw = new PrintWriter(new File(userHomeFolder, outputFile));
            if (!alreadyExists) {
                sb.append("student id");
                sb.append(",");
                sb.append("section");
                sb.append("\n");

                for (Student s : students) {
                    sb.append(s.getStudentNo());
                    sb.append(",");
                    sb.append(s.getAssignedSection());
                    sb.append("\n");
                }

                pw.write(sb.toString());
                pw.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

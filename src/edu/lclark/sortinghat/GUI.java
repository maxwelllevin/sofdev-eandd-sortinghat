package edu.lclark.sortinghat;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_COLOR_BURNPeer;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class GUI extends JFrame {

    // The actual algorithm
    private SortingHatMain sortingHatMain;
    private Parser parser;

    // These are the buttons that the user can click to interact with the GUI
    private JButton instructions;
    private JButton browseStudents;
    private JButton browseSections;
    private JButton runProgram;

    // These are the checkboxes that the user can select
    private JCheckBox athlete;

    // These are to hold our reports
    private JTextPane programEfficiency;
    private JTextPane overallPerformance;
    private JScrollPane scrollEfficiency;
    private JScrollPane scrollOverall;

    // This is "useless," but makes the user feel like stuff is happening
    private JProgressBar progressBar;

    private BufferedImage samHead;
    private BufferedImage jamesHead;
    private BufferedImage mackHead;
    private BufferedImage maxHead;
    private BufferedImage nickHead;
    private BufferedImage larsHead;
    private BufferedImage peterHead;


    private int flag;

    private String studentFilePath;
    private String sectionFilePath;

    private JLabel samLabel;
    private JLabel jamesLabel;
    private JLabel mackLabel;
    private JLabel maxLabel;
    private JLabel nickLabel;
    private JLabel peterLabel;
    private JLabel larsLabel;

    int easterCount;

    /**
     * JLabel larsLabel  * Initializes the GUI
     */
    public GUI() throws HeadlessException {

        // Initialize parameters
        flag = 0;

//        try {
//            samHead = ImageIO.read(new File("data/samhead.png"));
//            jamesHead = ImageIO.read(new File("data/jamesHead.png"));
//            mackHead = ImageIO.read(new File("data/mackHead.png"));
//            maxHead = ImageIO.read(new File("data/maxHead.png"));
//            nickHead = ImageIO.read(new File("data/nickHead.jpg"));
//            larsHead = ImageIO.read(new File("data/larsHead.jpg"));
//            peterHead = ImageIO.read(new File("data/peterHead.jpeg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // Initialize the buttons and checkboxes and the progress bar
        instructions = new JButton("Instructions");
        browseStudents = new JButton("Browse Students");
        browseSections = new JButton("Browse Sections");
        runProgram = new JButton("Run");
        athlete = new JCheckBox("Athletes");

        // Image on JFrame
//        setIconImage(new ImageIcon("lchat_small.jpg").getImage());

        // Initialize TextPanes and the Progress Bar
        programEfficiency = new JTextPane();
        overallPerformance = new JTextPane();
        scrollEfficiency = new JScrollPane(programEfficiency);
        scrollOverall = new JScrollPane(overallPerformance);
        scrollOverall.setBorder(BorderFactory.createEmptyBorder());
        progressBar = new JProgressBar();


//        samLabel = new JLabel(new ImageIcon(samHead));
//        jamesLabel = new JLabel(new ImageIcon(jamesHead));
//        mackLabel = new JLabel(new ImageIcon(mackHead));
//        maxLabel = new JLabel(new ImageIcon(maxHead));
//        nickLabel = new JLabel(new ImageIcon(nickHead));
//        peterLabel = new JLabel(new ImageIcon(peterHead));
//        larsLabel = new JLabel(new ImageIcon(larsHead));


        // Create and initialize grid and tooltips
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        setSize(1000, 600);
        setResizable(false);
        ToolTipManager.sharedInstance().setInitialDelay(100);

        // Create buttons, progress bar and panels for input
        runProgram.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        athlete.setSelected(true);

        // Create tooltips for buttons and checkboxes
        instructions.setToolTipText("Read the instruction");
        browseStudents.setToolTipText("Attach CSV file of students");
        browseSections.setToolTipText("Attach CSV file of sections");
        runProgram.setToolTipText("Run the program");
        athlete.setToolTipText("Select to balance athletes across all sections");

        // Initialize action listeners
        Instruction instructAction = new Instruction();
        BrowseStudents studentAction = new BrowseStudents();
        BrowseSections sectionAction = new BrowseSections();
        RunPrograms runAction = new RunPrograms();

        // Add action listener
        instructions.addActionListener(instructAction);
        browseStudents.addActionListener(studentAction);
        browseSections.addActionListener(sectionAction);
        runProgram.addActionListener(runAction);

        // Add the buttons to the Window (Adding to the upper left going down, Width and Height both 1)
        int buttonWeight_x = 20;
        int buttonWeight_y = 100;
//        instructions.setPreferredSize();
        add(instructions, new GBC(0, 0, 1, 1).setFill(GBC.BOTH).setWeight(buttonWeight_x, buttonWeight_y));
        add(browseStudents, new GBC(0, 1, 1, 1).setFill(GBC.BOTH).setWeight(buttonWeight_x, buttonWeight_y));
        add(browseSections, new GBC(0, 2, 1, 1).setFill(GBC.BOTH).setWeight(buttonWeight_x, buttonWeight_y));
        add(runProgram, new GBC(0, 3, 1, 1).setFill(GBC.BOTH).setWeight(buttonWeight_x, buttonWeight_y));
        add(athlete, new GBC(0, 4, 1, 1).setFill(GBC.BOTH).setWeight(buttonWeight_x, buttonWeight_y));

        // Add the text panels to the Window
        add(programEfficiency, new GBC(1, 0, 4, 2).setFill(GBC.BOTH).setWeight(100, 100));
        add(scrollOverall, new GBC(1, 2, 4, 3).setFill(GBC.BOTH).setWeight(100, 100));

        // Add the progress bar to the Window
        add(progressBar, new GBC(0, 6, 5, 1).setFill(GBC.BOTH).setWeight(100, 10));

        // Set sizes
        instructions.setPreferredSize(new Dimension(150, 100));
        browseStudents.setPreferredSize(new Dimension(150, 100));
        browseSections.setPreferredSize(new Dimension(150, 100));
        runProgram.setPreferredSize(new Dimension(150, 100));
        athlete.setPreferredSize(new Dimension(150, 100));
        programEfficiency.setPreferredSize(new Dimension(850, 200));
        scrollOverall.setPreferredSize(new Dimension(850, 300));

        pack();
    }


    private class Instruction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Welcome to Sorting Hat, where we sort students into E&D sections.\n" +
                    "1. Please click the \"Browse Students\" button and select a CSV file.\n" +
                    "2. Please click the \"Browse Sections\" button and select a CSV file.\n" +
                    "3. Select the \"Athlete\" checkbox to evenly spread the athletes across all sections.\n" +
                    "4. Please click the  \"Run\" button.", "Instructions", 1);
            easterCount++;
//            if (easterCount > 2) {
//                add(samLabel);
//                add(jamesLabel);
//                add(mackLabel);
//                add(maxLabel);
//                add(nickLabel);
//                add(larsLabel);
//                add(peterLabel);
//
//                try {
//                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("data/familyfeud.wav").getAbsoluteFile());
//                    Clip clip = AudioSystem.getClip();
//                    clip.open(audioInputStream);
//                    clip.start();
//                } catch (Exception ex) {
//                    System.out.println("Error with playing sound.");
//                    ex.printStackTrace();
//                }
//
//
//                revalidate();
//            }
        }

    }


    private class BrowseStudents implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            jfc.setDialogTitle("Select a CSV file");
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
            jfc.addChoosableFileFilter(filter);

            int returnValue = jfc.showOpenDialog(GUI.this);
            // int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                if (filter.accept(selectedFile)) {
                    String text = selectedFile.getAbsolutePath().toString();
                    File f = new File(text);
                    flag++;
                    studentFilePath = text;
                    String previous = programEfficiency.getText();
                    programEfficiency.setText(previous + "\n" + "Student File found at " + f.getName() + '\n');
                    // Activate Run
                    if (flag == 2) {
                        runProgram.setEnabled(true);
                    }

                    revalidate();
                }
            }
        }
    }


    private class BrowseSections implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            jfc.setDialogTitle("Select a CSV file");
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
            jfc.setFileFilter(filter);

            int returnValue = jfc.showOpenDialog(GUI.this);
            // int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                if (filter.accept(selectedFile)) {
                    String text = selectedFile.getAbsolutePath().toString();
                    File f = new File(text);
                    flag++;
                    sectionFilePath = text;
                    String previous = programEfficiency.getText();
                    programEfficiency.setText(previous + "\n" + "Section File found at " + f.getName() + '\n');
                    // Activate Run
                    if (flag == 2) {
                        runProgram.setEnabled(true);
                    }

                    revalidate();
                }
            }
        }
    }


    private class RunPrograms implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Run Sorting Hat
            boolean controlGender = true;
            boolean controlAthlete = false;
            if (athlete.isSelected()) {
                controlAthlete = true;
            }
            sortingHatMain = new SortingHatMain(sectionFilePath, studentFilePath, controlGender, controlAthlete);
//            sortingHatMain.printParse();
            parser = sortingHatMain.getSortingHat().getParser();
            runProgram.setEnabled(false);
            flag = 0;

            if (parser.isSectionsHeaderErrorBool() || parser.isStudentsHeaderErrorBool()) {
                if (parser.isSectionsHeaderErrorBool()) {
                    JOptionPane.showMessageDialog(programEfficiency, parser.getSectionsHeaderError());
                }
                if (parser.isStudentsHeaderErrorBool()) {
                    JOptionPane.showMessageDialog(programEfficiency, parser.getStudentsHeaderError());
                }
            } else {
                ProgressWorker pw = new ProgressWorker();
                pw.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        String name = evt.getPropertyName();
                        if (name.equals("progress")) {
                            int progress = (int) evt.getNewValue();
                            progressBar.setValue(progress);
                            repaint();
                        } else if (name.equals("state")) {
                            SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                            switch (state) {
                                case DONE:
                                    break;
                            }
                        }

                    }

                });
                pw.execute();
            }
        }


    }


    public class ProgressWorker extends SwingWorker<Object, Object> {
        @Override
        protected Object doInBackground() throws Exception {
            for (int i = 0; i <= 100; i++) {
                setProgress(i);
//                double progress = sortingHat.getGreedyAlgorithm().getSections().size() /
//                setProgress();
                if (i == 100) {

                    programEfficiency.setBorder(BorderFactory.createTitledBorder("Program Efficiency"));
//        programEfficiency.setEditable(false);
                    overallPerformance.setBorder(BorderFactory.createTitledBorder("Overall Performance"));
//        overallPerformance.setEditable(false);

                    printProgramEfficiency();
                    printOverallPerformance();
                    validate();
                    programEfficiency.setVisible(true);
                    overallPerformance.setVisible(true);

                }
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private void printProgramEfficiency() {
        ArrayList<Student> students = sortingHatMain.getSortingHat().getStudents(); // this includes preassigned students
        students.removeAll(sortingHatMain.getSortingHat().getAssigned()); // remove assigned students from students

//        for (Student s : sortingHatMain.getSortingHat().getAssigned()) {
//            if (students.contains(s)) {
//                System.out.println("hello");
//            }
//            System.out.println(s.getStudentNo());
//        }
//        System.out.println(students.size());
//

        Report report = new Report(sortingHatMain.getSortingHat().getSections(), students); // Note: Doesn't include preassigned students

        programEfficiency.setText("(Excludes pre-assigned students)\n\n" + report.getStatistics());
    }

    private void printOverallPerformance() {

        ArrayList<Student> allStudents = new ArrayList<>(sortingHatMain.getSortingHat().getStudents());
//
//        for (Student s: sortingHatMain.getSortingHat().getAssigned()) {
//            if (allStudents.contains(s)) {
//                System.out.println("hello");
//            }
//            System.out.println(s.getStudentNo());
//        }
//        System.out.println(allStudents.size());


        allStudents.addAll(sortingHatMain.getSortingHat().getAssigned());
        System.out.println(allStudents.size());

        Report report = new Report(sortingHatMain.getSortingHat().getSections(), allStudents);

        String stat = "";
        if (report.studentsWithIllegalPreferences().size() != 0) {
            stat = stat + "\n\nThese students listed a previous professor in their preferences:";
            for (String s : report.studentsWithIllegalPreferences()) {
                stat = stat + " " + s;
            }
            stat += "\n";
        }

        String duplicateReport = "";
        if (!idFrequencyReport().isEmpty()) {
            duplicateReport += "\nThese students were listed in the student csv file multiple times:\n";
            duplicateReport += idFrequencyReport().toString().replaceAll("[\\[\\]]", "") + "\n";
        }

        overallPerformance.setText("(Includes pre-assigned students)\n\n" + report.getStatistics() + stat +
                duplicateReport + report.worstGenderRatio() + "\n" + report.worstAthleteRatio() + "\n" +
                report.worstSpecificSport() + "\n" + "Output file created at User folder\n");
    }


    /**
     * Returns an ArrayList of students which were listed in the CSV student file more than once.
     * Accounts for duplicated instances: if they were listed 5 times, they are reported 4 times.
     */
    public ArrayList<String> idFrequencyReport() {
        ArrayList<String> badStudents = new ArrayList<>();
        Hashtable<String, Integer> table = sortingHatMain.getSortingHat().getIdFrequencyTable();

        for (String id : table.keySet()) {
            if (table.get(id) > 1) {
                for (int i = 1; i < table.get(id); i++) {
                    badStudents.add(id);
                }
            }
        }

        return badStudents;
    }

}
package edu.lclark.sortinghat;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;

public class SortingHatMain {

    private SortingHat sortingHat;



    public SortingHatMain(String sectionSelectedFile, String studentSelectedFile, boolean controlGender, boolean controlAthlete) {
        sortingHat = new SortingHat(new File(sectionSelectedFile), new File(studentSelectedFile), controlGender, controlAthlete);
        sortingHat.run();
        new Output(sortingHat.getSections(), sortingHat.getStudents());
    }

    /**
     * Returns the sortingHat
     * @return
     */
    public SortingHat getSortingHat() {
        return sortingHat;
    }

    /**
     * This launches the GUI and also contains the sorting hat rules
     *
     * @param args
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new GUI();
            frame.setTitle("Sorting Hat");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

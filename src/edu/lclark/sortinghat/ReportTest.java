package edu.lclark.sortinghat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class ReportTest {

    private SortingHat sortingHat;
    private ArrayList<Student> students;
    private ArrayList<Section> sections;
    private Student studentA;
    private Section sectionA;
    private Report report;

    @Before
    public void setUp() {
        sortingHat = new SortingHat(new File("csvparsetestSECT.csv"), new File("csvparsetestSTUD.csv"), true, true);
        sections = sortingHat.getSections();
        students = sortingHat.getStudents();
        report = new Report(sections, students);
        sortingHat.sortHungarian();
    }

    @Test
    public void testNumStudentsWithPreferences(){
        Assert.assertEquals(students.size()-21, report.numStudentsWithPreferences());
    }

}

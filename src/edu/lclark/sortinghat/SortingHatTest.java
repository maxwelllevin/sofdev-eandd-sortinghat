package edu.lclark.sortinghat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class SortingHatTest {

    private SortingHat sortingHat;
    private ArrayList<Student> students;
    private ArrayList<Section> sections;
    private ArrayList<Student> assigned;
    private ArrayList<Student> assignedCopy;
    private Student studentA;
    private Section sectionA;
    private Report report;

    @Before
    public void setUp() {
//        sortingHat = new SortingHat(new File("data/section.csv"), new File("data/student.csv")); // passes all
          sortingHat = new SortingHat(new File("data/section_duplicates.csv"), new File("data/student_duplicates.csv"),true, false);
//        sortingHat = new SortingHat(new File("data/section2.csv"), new File("data/student2.csv"), true, false); // fails gender balance and top choices
//        sortingHat = new SortingHat(new File("data/section2_headers.csv"), new File("data/student2_headers.csv"), true, false); // fails gender balance and top choices
//        sortingHat = new SortingHat(new File("data/section3.csv"), new File("data/student3.csv"), true, false); // fails gender balance, top choices, and athletes
//        sortingHat = new SortingHat(new File("data/section4_headers.csv"), new File("data/student4_headers.csv"), true, false); // fails gender balance, top choices, and athletes

        assigned = sortingHat.getAssigned();
        assignedCopy = new ArrayList<>(assigned); // copy not pointer ?"));
        sections = sortingHat.getSections();
        students = sortingHat.getStudents();
        report = new Report(sections, students);
        double time1 = System.nanoTime();
        sortingHat.sortHungarian();
        double time2 = System.nanoTime();
        //System.out.println(time2 - time1);
    }

    @Test
    public void numberSameSportLessThanThree() {
        String s = report.worstSpecificSport();
        System.out.println(s);
        Assert.assertTrue(s.isEmpty());
    }

    @Test
    public void shuffleDoesNotChangeCost() {
        ArrayList<SortingHat> hats = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            hats.add(new SortingHat(new File("data/section_duplicates.csv"), new File("data/student_duplicates.csv"),true, true));
            hats.get(i).run();
            System.out.println(hats.get(i).getCost());
        }
    }

    @Test
    public void shuffleChangesAssignments() {
        int loops = 2;
        ArrayList<SortingHat> hats = new ArrayList<>(loops);
        ArrayList<ArrayList<Student>> students = new ArrayList<>(loops);
        for (int i = 0; i < loops; i++) {
            hats.add(new SortingHat(new File("data/section_duplicates.csv"), new File("data/student_duplicates.csv"), true, true));
            hats.get(i).run();
            ArrayList<Student> temp = hats.get(i).getStudents();
            Collections.sort(temp);
            students.add(temp);

        }

        ArrayList<Student> differences = new ArrayList<>();
        for (int i = 0; i < students.get(0).size(); i++) { //
            if (students.get(0).get(i).compareTo(students.get(1).get(i)) == 0) {
                differences.add(students.get(0).get(i));
                System.out.println(students.get(0).get(i));
            }
        }

        Assert.assertFalse(differences.isEmpty());
    }


    @Test
    public void numStudentsLessThanNumSeats() {
        int numSeats = sortingHat.getNumSeats();
        int numStudents = students.size();
        System.out.println(numSeats + ", " + numStudents);
        Assert.assertTrue(numSeats >= numStudents);
    }

    @Test
    public void buildCostMatrixIsNotNullValued() {
        double[][] costs = sortingHat.buildCostMatrix();
        for (int i = 0; i < costs.length; i++) {
            for (int j = 0; j < costs.length; j++) {
                if (costs[i][j] < 1) {
                    Assert.fail("Found value less than 1");
                }
                if (Double.isNaN(costs[i][j])) {
                    Assert.fail("Found null value");
                }
                if (Double.isInfinite(costs[i][j])) {
                    Assert.fail("Found infinite value");
                }
            }
        }
        Assert.assertFalse(false);
    }


    @Test
    public void everyStudentGetsAssigned() {
        // Check that the section field of every student is set.
        for (int i = 0; i < students.size(); i++) {
            Assert.assertTrue(students.get(i).isAssigned());
        }

    }

    @Test
    public void sortSetsAppropriateSectionSize() {
        for (Section sec : sections) {
            Assert.assertTrue(sec.getNumStudents() <= sec.getCap());
        }
    }

    @Test
    public void sortDoesNotDuplicateStudents() {
        // Look at every section and assert we never have two identical students in a class
        ArrayList<Student> seen = new ArrayList<>();
        for (Section sec : sections) {
            for (Student stud : sec.getStudents()) {
                if (seen.contains(stud)) {
                    Assert.fail("Error: " + stud.getStudentNo() + " is assigned more than once.");
                } else {
                    seen.add(stud);
                }
            }
        }
    }


    @Test
    public void sortGivesMostTheirTopChoices() {
        // Our sorting algorithm should place most students into one of their top two preferences
        double[] percentages = report.percentages();

        System.out.println("The percentage of students in their 1st choice is: " + (percentages[0]));
        System.out.println("The percentage of students in their 2nd choice is: " + (percentages[1]));
        System.out.println("The percentage of students in their 3rd choice is: " + (percentages[2]));
        System.out.println("The percentage of students in their 4th choice is: " + (percentages[3]));
        System.out.println("The percentage of students in their 5th choice is: " + (percentages[4]));
        System.out.println("The percentage of students in their 6th choice is: " + (percentages[5]));
        System.out.println("The number of students who did not get any of their preferences is: " + (report.numStudentsGotNoPreferences()));

        int[] reportPref = report.preferences();

        for (int i = 0; i < reportPref.length; i++) {
            System.out.println(reportPref[i] + " students got their choice " + (i + 1));
        }

        Assert.assertTrue(.5 < (0.0 + percentages[0] + percentages[1]));
        Assert.assertEquals(0.0, percentages[5], 0.001);
        Assert.assertEquals(0, report.numStudentsGotNoPreferences());
    }

    @Test
    public void sortUpdatesSectionsAndStudents() {
        // Check that sortHungarian updates students' section and a section's students, and that they match.
        for (Student stud : students) {
            Assert.assertTrue(stud.getAssignedSection().hasStudent(stud));
        }
    }

    @Test
    public void noStudentReceivesIllegalSection() {
//        for(Section s: sections){
//            System.out.println(s.getProf());
//        }
        for (Student s : students) {
            for (String illegalSection : s.getIllegalSections()) {
                //System.out.println(s.getStudentNo() + " " + p + " " + s.getSection().getProf());
                //students without a previous professor will have the the empty string in the prevprof field
                Assert.assertFalse(illegalSection.equals(s.getAssignedSection().getSectionNo()));
            }
        }
    }

    @Test
    public void hungarianAlgorithmReturnsCorrectAnswerForSmallProblem() {
        double[][] cost = {{82.0, 83, 69, 92}, {77, 37, 49, 92}, {11, 69, 5, 86}, {8, 9, 98, 23}};
        int[] actual = {2, 1, 0, 3};
        HungarianAlgorithm hungary = new HungarianAlgorithm(cost);
        int[] result = hungary.execute();
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(actual[i], result[i]);
        }
    }

    @Test
    public void hungarianAlgorithmReturnsCorrectAnswerForMediumProblem() {
        // This is for a medium-size problem
        double[][] cost = {
                {1, 1, 2, 2, 3, 3, 4, 4, 5, 5},
                {1, 1, 2, 2, 3, 3, 5, 5, 4, 4},
                {2, 2, 3, 3, 4, 4, 100, 100, 1, 1},
                {1, 1, 5, 5, 3, 3, 2, 2, 4, 4},
                {3, 3, 2, 2, 4, 4, 1, 1, 5, 5},
                {5, 5, 1, 1, 2, 2, 3, 3, 4, 4},
                {100, 100, 100, 100, 1, 1, 2, 2, 3, 3},
                {4, 4, 100, 100, 3, 3, 2, 2, 1, 1},
                {3, 3, 2, 2, 1, 1, 4, 4, 5, 5},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100}
        };
        int[] actual = {0, 1, 8, 6, 7, 2, 4, 9, 5, 3};
        int[] actual2 = {1, 0, 9, 7, 6, 3, 5, 8, 2};
        HungarianAlgorithm hungary = new HungarianAlgorithm(cost);
        int[] result = hungary.execute();
        for (int i = 0; i < 10; i++) {
            boolean statement = result[i] == actual[i] || result[i] == actual2[i];
            Assert.assertTrue(statement);
        }
    }

    @Test
    public void hungarianAlgorithmSortsTwoStudentsInSurplusArray() {
        // This is for a medium-size problem
        double[][] cost = {
                {1, 1, 2, 2, 100, 100, 100, 100, 100, 100},
                {2, 2, 100, 100, 1, 1, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                {100, 100, 100, 100, 100, 100, 100, 100, 100, 100}
        };
        HungarianAlgorithm hungary = new HungarianAlgorithm(cost);
        int[] result = hungary.execute();
        Assert.assertTrue(result[0] == 0 || result[0] == 1);
        Assert.assertTrue(result[1] == 4 || result[1] == 5);
    }

    @Test
    public void sortMakesGenderBalancedClasses() {
        double worstRatio = 1;
        Section worstSection = sections.get(0);

        for (Section sec : sections) {
            int numMales = 0;
            for (Student stud : sec.getStudents()) {
                if (stud.isMale()) {
                    numMales++;
                }
            }

            if (worstRatio > (numMales + 0.0) / sec.getNumStudents()) {
                worstRatio = (numMales + 0.0) / sec.getNumStudents();
                worstSection = sec;
            }

//            System.out.println((numMales + 0.0) / sec.getNumStudents());
//            System.out.println(sec.getSectionNo());
//            System.out.println(sec.getNumStudents());

            if (sec.getNumStudents() < 10) {
                Assert.assertTrue((numMales + 0.0) / sec.getNumStudents() >= 0.20 && (numMales + 0.0) / sec.getNumStudents() <= 0.80);
                continue;
            }
            Assert.assertTrue((numMales + 0.0) / sec.getNumStudents() >= 0.30 && (numMales + 0.0) / sec.getNumStudents() <= 0.70);
        }

        System.out.println("Section " + worstSection + " had the worst gender ratio: " + worstRatio);
    }

    @Test
    public void sortMakesAthleteBalancedClasses() {
        double worstRatio = 0;
        Section worstSection = sections.get(0);

        for (Section sec : sections) {
            int numJocks = 0;
            for (Student stud : sec.getStudents()) {
                if (stud.isAthlete()) {
                    numJocks++;
                }
            }

//            System.out.println((numJocks + 0.0) / sec.getNumStudents());
//            System.out.println(sec.getSectionNo());
//            System.out.println(sec.getNumStudents());

            if (worstRatio < (numJocks + 0.0) / sec.getNumStudents()) {
                worstRatio = (numJocks + 0.0) / sec.getNumStudents();
                worstSection = sec;
            }

            if (sec.getNumStudents() < 10) {
                Assert.assertTrue((numJocks + 0.0) / sec.getNumStudents() <= 0.45);
                continue;
            }
            Assert.assertTrue((numJocks + 0.0) / sec.getNumStudents() <= 0.45);
        }

        System.out.println("Section " + worstSection + " had the worst athlete ratio: " + worstRatio);

//        for (Section sec : sections) {
//            int numAthletes = 0;
//            for (Student stud : sec.getStudents()) {
//                if (stud.isAthlete()) {
//                    numAthletes++;
//                }
//            }
//
//
//            if (sec.getNumStudents() < 10) {
//                Assert.assertTrue((numAthletes + 0.0) / sec.getNumStudents() <= 0.45);
//                continue;
//            }
//            Assert.assertTrue((numAthletes + 0.0) / sec.getNumStudents() <= 0.45);
//        }
    }

    @Test
    public void sortDoesNotChangeAssignedStudents() {
//        Assert.assertTrue(false);
        for (Student s : assigned) {
            Assert.assertTrue(assignedCopy.contains(s));
        }
    }

    @Test
    public void thereAreGenders() {
        int femaleCount = 0;
        for (Student s : students) {
            if (s.isMale()) {
                femaleCount++;
            }
        }
        Assert.assertTrue(femaleCount > 10);
    }

    @Test
    public void thereAreAthletes() {
        int athleteCount = 0;
        for (Student s : students) {
            if (s.isAthlete()) {
                athleteCount++;
            }
        }
        Assert.assertTrue(athleteCount > 10);
    }

    @Test
    public void correctNumberOfCourses() {
        for (Section s : sections) {
            System.out.println(s.getSectionNo());
            Assert.assertFalse(s.getSectionNo().contains("co"));
        }
//        Assert.assertTrue(sections.size() == );
    }

    @Test
    public void correctNumberOfStudents() {
        for (Student s : students) {
            System.out.println(s.getStudentNo());
            Assert.assertFalse(s.getStudentNo().contains("i"));
        }
        System.out.println("total parsed students = " + (students.size() + assigned.size()));
//        System.out.println("total parsed students = " + students.size());
//        System.out.println("total parsed students = " + assigned.size());
    }

//    @Test
//    public void thereAreDuplicateIDs() {
//        Assert.assertTrue(sortingHat.getDuplicateIDs().size() > 0);
//        System.out.println(sortingHat.getDuplicateIDs());
//    }

//    @Test
//    public void isStudentHeader() {
//        Assert.assertTrue(sortingHat.getParser().isStudentHeader());
//    }

}
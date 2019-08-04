package edu.lclark.sortinghat;

import java.util.ArrayList;
import java.util.Arrays;

public class Student implements Comparable<Student>{

    private String studentNo;

    private Section assignedSection;

    private boolean male;
    private boolean athlete;

    private ArrayList<Section> preferences;
    private ArrayList<String> illegalSections;
    private ArrayList<String> sports;

    /**
     * Constructs a Student that is NOT already assigned to a Section.
     * Student is assigned to default assignedSection: Professor DUMMY's snoop seminar
     */
    public Student(String studentNo, ArrayList<Section> preferences, boolean male, boolean athlete, ArrayList<String> sports, ArrayList<String> illegalSections) {
        this.studentNo = studentNo;
        this.male = male;
        this.athlete = athlete;
        this.sports = sports;
        this.illegalSections = illegalSections;

        this.preferences = new ArrayList<>(preferences);
        this.assignedSection = new Section("0", "DUMMY", 420); //default section
    }

    /**
     * Constructs a Student that is already assigned to a Section.
     */
    public Student(String studentNo, ArrayList<Section> preferences, boolean male, boolean athlete, ArrayList<String> sports, Section assignedSection) { // TODO: This is where the bug is for Max and Nick
        this.studentNo = studentNo;
        this.male = male;
        this.preferences = new ArrayList<>(preferences);
        this.athlete = athlete;
        this.sports = sports;
        this.illegalSections = new ArrayList<>();
//        this.prevProfs = prevProfs;
        this.assignedSection = assignedSection;
    }

    public String getStudentNo() {
        return studentNo;
    }

    /**
     * Returns an arraylist of sections the the student listed. Returns null if the student did not list preferences or
     * only listed illegal preferences.
     * @return
     */
    public ArrayList<Section> getPreferences() {
        return preferences;
    }

    public boolean isMale() {
        return male;
    }

    public boolean isAthlete() {
        return athlete;
    }

    public String toString() {
        return studentNo;
    }

    public Section getAssignedSection() {
        return assignedSection;
    }

    public boolean isAssigned() {
        return assignedSection.getIndex() != 420;
    }

    public void setAssignedSection(Section assignedSection) {
        this.assignedSection = assignedSection;
    }

    public ArrayList<String> getIllegalSections() {
        return illegalSections;
    }

    public ArrayList<String> getSports() {
        return sports;
    }

    @Override
    public int compareTo(Student o) {
        return Integer.parseInt(studentNo) - Integer.parseInt(o.studentNo);
    }


    public static void main(String[] args) {
//        Student bobby = new Student("112", new ArrayList<>(), false, false, "poop", new ArrayList<>());
//        Student brenda = new Student("300", new ArrayList<>(), false, false, "quiddich", new ArrayList<>());

//        System.out.println(bobby.compareTo(brenda));
    }

    //   public Student(String studentNo, ArrayList<Section> preferences, boolean male, boolean athlete, ArrayList<String> illegalSections) {

}

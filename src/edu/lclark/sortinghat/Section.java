package edu.lclark.sortinghat;

import java.util.*;

public class Section {

    private String sectionNo;
    private int cap;
    private String prof;
    private int index;
    private ArrayList<Student> students;
    private static final int DEFAULT_CAP = 19;

    /**
     * I don't know why we have three different Section constructors but I think this first one is the best one?
     *
     * @param sectionNo
     * @param prof
     * @param index
     */
    public Section(String sectionNo, String prof, int index) {
        this.sectionNo = sectionNo;
        this.prof = prof;
        this.index = index;
        this.cap = DEFAULT_CAP;
        students = new ArrayList<>();
    }

    public Section(String sectionNo, int index) {
        this.sectionNo = sectionNo;
        this.index = index;
        students = new ArrayList<>();
    }

    public Section(String sectionNo, String prof, int index, int cap) {
        this.sectionNo = sectionNo;
        this.prof = prof;
        this.index = index;
        this.cap = cap;
        students = new ArrayList<>();
    }

    public String getSectionNo() {
        return sectionNo;
    }

    public String getProf() {
        return prof;
    }

    public int getCap() {
        return cap;
    }

    public boolean addStudent(Student student) {
        if (students.size() >= cap) {
            // TODO: Launch an error dialog box in the GUI window
            throw new IllegalArgumentException("Section is already full. Cannot add student to full section");
        }
        students.add(student);
        return true;
    }

    public int getIndex() {
        return index;
    }

    public boolean equals(String sectionNo) {
        return this.sectionNo.equals(sectionNo);
    }

    public boolean equals(Section s) {
        return equals(s.getSectionNo());
    }

    public String toString() {
        return sectionNo;
    }

    public boolean hasStudent(String id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentNo().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasStudent(Student stud) {
        return students.contains(stud);
    }

    public int getNumStudents() {
        return students.size();
    }


    public ArrayList<Student> getStudents() {
        return students;
    }

    public int getNumAvailableSeats() {
        return cap - students.size();
    }

    public int getNumMaleStudents() {
        int n = 0;
        for (Student stud : students) {
            if (stud.isMale()) {
                n++;
            }
        }
        return n;
    }

    public int getNumFemaleStudents() {
        int n = 0;
        for (Student stud : students) {
            if (!stud.isMale()) {
                n++;
            }
        }
        return n;
    }

    public int getNumFemaleNerds(){
        int n = 0;
        for (Student stud : students) {
            if (!stud.isMale() && !stud.isAthlete()) {
                n++;
            }
        }
        return n;
    }

    public int getNumMaleNerds(){
        int n = 0;
        for (Student stud : students) {
            if (stud.isMale() && !stud.isAthlete()) {
                n++;
            }
        }
        return n;
    }

}

package edu.lclark.sortinghat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class Report {

    private ArrayList<Student> students;
    private ArrayList<Section> sections;

    /**
     * Construct a Report
     *
     * @param sections the sections you want to report on
     * @param students the students you want to report on
     */
    public Report(ArrayList<Section> sections, ArrayList<Student> students) {
        this.sections = sections;
        this.students = students;
    }

    /**
     * The number of students who receive each of their preferences
     *
     * @return int[] the ith entry is the number of students who go their (i + 1)th choice
     */
    public int[] preferences() {
        int[] preferenceResults = new int[6];
        for (Student s : students) {

            if (s.getPreferences().size() < 4) {  // check if student has at least 4 preferences
                continue;
            }

            ArrayList<Section> pref = s.getPreferences();
            for (int j = 0; j < pref.size(); j++) {
                if (pref.get(j).equals(s.getAssignedSection())) {
                    preferenceResults[j]++;
                    break;
                }
            }
        }
        return preferenceResults;
    }

    /**
     * The percent of students who got each of their preferences
     *
     * @return double[] the ith entry in the percent of students who got their ith choice
     */
    public double[] percentages() {
        int numStudents = numStudentsWithPreferences();
        int[] preferences = preferences();
        double[] percentages = new double[6];
        for (int i = 0; i < 6; i++) {
            percentages[i] = (0.0 + preferences[i]) / numStudents;
        }
        return percentages;
    }

    /**
     * @return The number of students who list at least 3 preferences
     */
    public int numStudentsWithPreferences() {
        int n = 0;
        for (Student s : students) {
            //TODO how many preferences is too few?
            if (s.getPreferences().size() >= 3) {
                n++;
            }
        }
        return n;
    }

    /**
     * Gets the number of students not assigned to any of their preferences, does not consider students who list 3 or fewer preferences
     *
     * @return the number of students who were not assigned to one of their preferences
     */
    public int numStudentsGotNoPreferences() {
        int n = 0;
        for (Student s : students) {
            if ((!s.getPreferences().contains(s.getAssignedSection())) && (s.getPreferences().size() > 3)) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return the number of male students
     */
    public int numMales() {
        int n = 0;
        for (Student s : students) {
            if (s.isMale()) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return the number of female students
     */
    public int numFemales() {
        return students.size() - numMales();
    }

    /**
     * How popular a given section is
     *
     * @param sectionNo the section number
     * @return int[] the ith entry corresponds to the nubmer of students that listed the section as their ith preference
     */
    public int[] sectionPopularity(String sectionNo) {
        int[] num = new int[6];
        for (Student s : students) {
            for (int i = 0; i < s.getPreferences().size(); i++) {
                if (s.getPreferences().get(i).equals(sectionNo)) {
                    num[i]++;
                }
            }
        }
        return num;
    }

    /**
     * Gets all the students who have illegal preferences
     *
     * @return An arrayList of Strings of the studentNo of the students with illegal preferences
     */
    public ArrayList<String> studentsWithIllegalPreferences() {
        ArrayList<String> badStudents = new ArrayList<>();
        for (Student student : students) {
            for (Section preference : student.getPreferences()) {
                if (student.getIllegalSections().contains(preference.getSectionNo())) {
                    badStudents.add(student.getStudentNo());
                }
            }
        }
        return badStudents;
    }

    /**
     * Gets the sections with more than 2 athletes in a specific sport
     * @return
     */
    public String worstSpecificSport() {
        ArrayList<Student> tooManyAthletes = new ArrayList<>();

        String result = "";
        int worstCase = 0;

        for (Section section : sections) {
            Hashtable<String, Integer> map = new Hashtable<>();
            for (Student student : section.getStudents()) {
                if (student.isAthlete()) {
                    for (String sport : student.getSports()) {
                        if (map.containsKey(sport)) {
                            map.replace(sport, map.get(sport) + 1);
                        } else {
                            map.put(sport, 1);
                        }
                    }
                }
            }

            // don't check for too many specific athletes if there aren't any
            if (map.values().isEmpty()) {
                continue;
            }

            // find and report sections with too many specific sport playaz
            ArrayList<String> manySports = new ArrayList<>();
            for (String sport : map.keySet()) {
                if (map.get(sport) > 2) {
                    if (!manySports.contains(sport)) {
                        manySports.add(sport);
                    }
                }
            }

            if (Collections.max(map.values()) > 2) {
                result += "Section " + section + " has too many athletes in the following sports: \n";
                for (String sport : manySports) {
                    result += "\t" + sport + ": ";
                    for (Student student : section.getStudents()) {
                        if (student.isAthlete() && student.getSports().contains(sport)) {
                            result += student + " ";
                        }
                    }
                    result += "\n\n";
                }
            }

            worstCase = Math.max(worstCase, Collections.max(map.values()));
        }

        if (result.isEmpty()) {
            result += "There were no more than 2 athletes of a specific sport in any section\n";
        }

        return result;
    }

    public String worstGenderRatio() {
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
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return "Section " + worstSection + " had the worst gender ratio: " + df.format(100*worstRatio) + "%";
    }

    /***
     *
     * @return
     */
    public String worstAthleteRatio() {
        double worstRatio = 0;
        Section worstSection = sections.get(0);

        for (Section sec : sections) {
            int numJocks = 0;
            for (Student stud : sec.getStudents()) {
                if (stud.isAthlete()) {
                    numJocks++;
                }
            }

            if (worstRatio < (numJocks + 0.0) / sec.getNumStudents()) {
                worstRatio = (numJocks + 0.0) / sec.getNumStudents();
                worstSection = sec;
            }
        }

        DecimalFormat df = new DecimalFormat("#.##");
        return "Section " + worstSection + " had the worst athlete ratio: " + df.format(100*worstRatio) + "%";
    }

    public String getStatistics() {
        String stat = "";

        DecimalFormat df = new DecimalFormat("#.##");

        for (int i = 0; i < 6; i++) {
            stat = stat + "The percentage of students in their number " + (i + 1) + " choice is: " + df.format((((100 * percentages()[i])))) + "%" + "\n";
        }

        stat = stat + "The number of students who did not get any of their preferences is: " + numStudentsGotNoPreferences();

        return stat;
    }

}

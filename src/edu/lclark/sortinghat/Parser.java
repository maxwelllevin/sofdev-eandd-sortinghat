package edu.lclark.sortinghat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Parser {

    private ArrayList<Section> sections;
    private ArrayList<Student> students;
    private ArrayList<Student> unassigned;
    private ArrayList<Student> assigned;

    private Hashtable<String, Integer> idFrequencyTable;

    private boolean sectionHeader;
    private boolean studentHeader;

    private String sectionsHeaderError;
    private String studentsHeaderError;

    private boolean sectionsHeaderErrorBool = false;
    private boolean studentsHeaderErrorBool = false;

    public Parser(File sectionsFile, File studentsFile) {
        idFrequencyTable = new Hashtable<>();

        students = new ArrayList<>();
        unassigned = new ArrayList<>();
        assigned = new ArrayList<>();

        sections = parseSectionCSV(sectionsFile);
        parseStudentCSV(studentsFile);
    }


    /**
     * Puts section information from CSV file into section objects
     *
     * @param file
     */
    public ArrayList<Section> parseSectionCSV(File file) {
        ArrayList<Section> sections = new ArrayList<>();

        int index = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int sectionNoIndex = 0;
        int professorIndex = 1;
        int capIndex = 2;

        List<String> possibleCapNames = new ArrayList<>();
        possibleCapNames.add("cap");
        possibleCapNames.add("size");
        possibleCapNames.add("number students");
        possibleCapNames.add("student cap");

        List<String> row = CSVReader.parseLine(scanner.nextLine());

        if ((int) row.get(0).charAt(0) == 65279) { // check for mysterious non-space character
            row.set(0, row.get(0).substring(1, row.get(0).length()));
        }

        row.replaceAll(String::toLowerCase);

        boolean header = row.get(0).contains("co") || row.get(0).startsWith("prof")
                || possibleCapNames.contains(row.get(0));

        sectionHeader = header;

        // if there is a header
        if (header) {
            if (row.size() > 2) {
                for (int i = 0; i < 3; i++) {
                    if (row.get(i).toLowerCase().startsWith("co")) {
                        sectionNoIndex = i;
                    } else if (row.get(i).startsWith("prof")) {
                        professorIndex = i;
                    } else if (possibleCapNames.contains(row.get(i))) {
                        capIndex = i;
                    } else {
                        sectionsHeaderError = "Could not interpret header: " + row.get(i) + ". Please fix header!";
                        sectionsHeaderErrorBool = true;
                    }
                }
            }
        }

        // look for "Course Section #", or "Course Number"
        if (header) {
            addSections(sections, index, scanner, sectionNoIndex, professorIndex, capIndex);
        } else {
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            addSections(sections, index, scanner, sectionNoIndex, professorIndex, capIndex);
        }

        scanner.close();

        return sections;
    }

    /***
     *
     *  Populates sections<> with a bunch of Sections
     */
    public void addSections(ArrayList<Section> sections, int index, Scanner scanner, int sectionNoIndex, int professorIndex, int capIndex) {
        List<String> row;
        while (scanner.hasNext()) {

            row = CSVReader.parseLine(scanner.nextLine());
            if ((int) row.get(0).charAt(0) == 65279) { // check for mysterious non-space character
                row.set(0, row.get(0).substring(1, row.get(0).length()));
            }
            row.replaceAll(String::toLowerCase);

            String sectionNo = row.get(sectionNoIndex);
            String prof = row.get(professorIndex);

            if (prof.charAt(0) == '\"') {
                prof = prof.substring(1, prof.length());
            }
            if (prof.charAt(prof.length() - 1) == ' ') {
                prof = prof.substring(0, prof.length() - 1);
            }

            if (row.size() <= 2) {
                sections.add(new Section(sectionNo, prof, ++index));
            } else {
                sections.add(new Section(sectionNo, prof, ++index, Integer.parseInt(row.get(capIndex))));
            }
        }
    }

    /**
     * Puts student information from CSV file into student objects
     *
     * @param file
     */
    public void parseStudentCSV(File file) {

        String[] headersArray = { "placement", "identifying number", "m / f", "athlete", "sport", "sss", "aes", "transfer",
                "continuing", "prev. e&d instructor", "section #'s can't be placed into (due to prev professor)",
                "choice 1", "choice 2", "choice 3", "choice 4", "choice 5", "choice 6" };

        ArrayList<String> headers = new ArrayList<>();
        for (String s : headersArray) {
            headers.add(s);
        }

        HashMap<String, Integer> headerMap = new HashMap<>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<String> row = CSVReader.parseLine(scanner.nextLine());

        if ((int) row.get(0).charAt(0) == 65279) { // check for mysterious non-space character
            row.set(0, row.get(0).substring(1, row.get(0).length()));
        }

        row.replaceAll(String::toLowerCase);

        boolean header = headers.contains(row.get(0)); // why untrue//?

        studentHeader = header;

        if (header) {
            // create map of custom header orderings
            for (int i = 0; i < row.size(); i++) {
                headerMap.put(row.get(i).trim(), i);
            }
            addStudents(scanner, headerMap);
        } else {
            // assume default header orderings
            for (int i = 0; i < headersArray.length; i++) {
                headerMap.put(headersArray[i], i);
            }

            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            addStudents(scanner, headerMap);
        }

        scanner.close();

    }


    /***
     *
     * Adds Students to the students<> ArrayList
     */
    private void addStudents(Scanner scanner, HashMap<String, Integer> headerMap) {
        List<String> row;

        while (scanner.hasNext()) {
            row = CSVReader.parseLine(scanner.nextLine());

            if (!row.get(0).isEmpty() && (int) row.get(0).charAt(0) == 65279) { // check for mysterious non-space character
                row.set(0, row.get(0).substring(1, row.get(0).length()));
            }

            row.replaceAll(String::toLowerCase);

            // ID
            String id = "";
            if (headerMap.containsKey("identifying number")) {
                id = row.get(headerMap.get("identifying number"));
            } else if (headerMap.containsKey("id")) {
                id = row.get(headerMap.get("id"));
            } else {
                studentsHeaderError = "Could not find student ID header";
                studentsHeaderErrorBool = true;
                return;
            }

            // search for duplicate student IDs
            if (idFrequencyTable.containsKey(id)) {
                idFrequencyTable.replace(id, idFrequencyTable.get(id) + 1);
            } else {
                idFrequencyTable.put(id, 1);
            }

            // Gender
            boolean male = false;
            if (headerMap.containsKey("gender")) {
                male = row.get(headerMap.get("gender")).equals("m") ||row.get(headerMap.get("gender")).equals("m");
            } else if (headerMap.containsKey("m / f")) {
                male = row.get(headerMap.get("m / f")).equals("m") || row.get(headerMap.get("m / f")).equals("m");
            }

            // Are they and athlete?
            boolean athlete = row.get(headerMap.get("athlete")).equals("y") || row.get(headerMap.get("athlete")).equals("y");

            // Specific sport
            ArrayList<String> sports = new ArrayList<>();
            sports.add("");
            if (athlete) {
                if (row.get(headerMap.get("sport")) != null) {
                    sports = parseIllegalSections(row.get(headerMap.get("sport"))); // rename "Parse illegeal Sections"
                }
            }

            // Adding preference
            ArrayList<Section> preferences = new ArrayList<>(); // TODO: reference already parsed sections arraylist

            for (int i = 0; i < 6; i++) {
                boolean b = false;
                for (Section s : sections) {
                    if (row.get(headerMap.get("choice 1") + i).isEmpty()) {
                        b = true;
                        break;
                    }
                    if (s.getSectionNo().equals(row.get(headerMap.get("choice 1") + i))) {
                        preferences.add(sections.get(s.getIndex() - 1)); // Goes out of bounds (removed a -1 and it works!)
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    studentsHeaderError = "Student " + id + " listed a section that does not exist ("
                            + row.get(headerMap.get("choice 1") + i) + ")" + "\n Are you sure these data files are compatible?";
                    studentsHeaderErrorBool = true;
                    return;
                }
            }

            // Adds pre-assigned students to assigned<>
            if (!row.get(headerMap.get("placement")).isEmpty()) { //TODO: MAke sure this works
                for (Section s : sections) {
                    if (s.getSectionNo().equals(row.get(headerMap.get("placement")))) {
                        Student peter = new Student(id, preferences, male, athlete, sports, s); // peter is an preassigned student
                        if (!s.addStudent(peter)) {
                            System.out.println("ERROR! Could not pre-assign student to section.");
                        }
                        assigned.add(peter);
                        break;
                    }
                }
                continue;
            }

            // illegal sections
            String illegal = "";
            if (headerMap.containsKey("section #'s can't be placed into (due to prev professor)")) {
                illegal = row.get(headerMap.get("section #'s can't be placed into (due to prev professor)"));
            } else if (headerMap.containsKey("illegal sections")) {
                illegal = row.get(headerMap.get("illegal sections"));
            }

            //parse illegal sections
            ArrayList<String> illegalSections = parseIllegalSections(illegal);

            students.add(new Student(id, preferences, male, athlete, sports, illegalSections));
        }
    }

    /**
     *
     * Parses illegal sections... duh
     */
    public ArrayList<String> parseIllegalSections(String illegal) {
        ArrayList<String> illegalSections = new ArrayList<>();
        if (illegal.contains(",")) {
            String[] illegalArray = illegal.split(",");
            for (String s : illegalArray) {
                s = s.replaceAll("\\s+", "");
                if (s.charAt(0) == '\"') {
                    s = s.substring(1, s.length());
                }
                if (s.charAt(s.length() - 1) == '\"') {
                    s = s.substring(0, s.length() - 1);
                }
                illegalSections.add(s);
            }
        } else {
            illegalSections.add(illegal);
        }
        return illegalSections;
    }


    public ArrayList<Section> getSections() {
        return sections;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Student> getUnassigned() {
        return unassigned;
    }

    public ArrayList<Student> getAssigned() {
        return assigned;
    }

    public Hashtable<String, Integer> getIdFrequencyTable() {
        return idFrequencyTable;
    }

    public boolean isSectionHeader() {
        return sectionHeader;
    }

    public boolean isStudentHeader() {
        return studentHeader;
    }

    public String getSectionsHeaderError() {
        return sectionsHeaderError;
    }

    public String getStudentsHeaderError() {
        return studentsHeaderError;
    }

    public boolean isSectionsHeaderErrorBool() {
        return sectionsHeaderErrorBool;
    }

    public boolean isStudentsHeaderErrorBool() {
        return studentsHeaderErrorBool;
    }

    public static void main(String[] args) {
        //        sortingHat = new SortingHat(new File("data/section.csv"), new File("data/student.csv")); // passes all
        Parser p = new Parser(new File("data/section2.csv"), new File("data/student2.csv")); // fails gender balance and top choices
//        sortingHat = new SortingHat(new File("data/section2_headers.csv"), new File("data/student2_headers.csv")); // fails gender balance and top choices
//        sortingHat = new SortingHat(new File("data/section3.csv"), new File("data/student3.csv")); // fails gender balance, top choices, and athletes
//        sortingHat = new SortingHat(new File("data/section4_headers.csv"), new File("data/student4_headers.csv")); // fails gender balance, top choices, and athletes

        String peter = " Peter ";
//        System.out.println("[" + peter.trim() + "]");

    }

}

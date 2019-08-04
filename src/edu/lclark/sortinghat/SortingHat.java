package edu.lclark.sortinghat;

import java.io.File;
import java.util.*;

public class SortingHat {


    // used to store our students and sections
    private ArrayList<Section> sections;
    private ArrayList<Student> students;
    private ArrayList<Student> originalStudents; // order maintained, never used
    private ArrayList<Student> unassigned;
    private ArrayList<Student> assigned;    // the preassigned students

    private Hashtable<String, Integer> idFrequencyTable;

    private int numSeats;
    private SeatIndexMap seatMap;

    private double[][] costMatrix;

    private int[] result;

    private double cost;

    private Parser parser;

    boolean controlAthlete;

    private boolean fixSpecificAthlete;

    /**
     * Calls parsing method on students and sections csv files. Can optionally account for gender and athlete ratios in
     * the hungarian algorithm part of the code.
     * @param sectionsFile
     * @param studentsFile
     * @param controlGender
     * @param controlAthlete
     */
    public SortingHat(File sectionsFile, File studentsFile, boolean controlGender, boolean controlAthlete) {
        this.controlAthlete = controlAthlete;
        parser = new Parser(sectionsFile, studentsFile);
        sections = parser.getSections();
        students = parser.getStudents();
        originalStudents = new ArrayList<>(students); // make a copy of Students<>, NOT simply point tp Students<>
        Collections.shuffle(students);
        unassigned = parser.getUnassigned();
        assigned = parser.getAssigned();

        idFrequencyTable = parser.getIdFrequencyTable();

        numSeats = 0;
        for (Section s : this.sections) {
            numSeats += s.getNumAvailableSeats();
        }
        seatMap = new SeatIndexMap(sections, numSeats);
    }


    /**
     * Call this method to run the actual assignment algorithm. Calls sortHungarian which assigns non-preassigned students
     * to sections and populates appropriate fields for both students and sections. Also adds students that have been
     * preassigned to specific sections to the students arrayList.
     */
    public void run() {
        sortHungarian();
        students.addAll(assigned);
    }

    /**
     * Called by run method. Calls buildCostMatrix and performs the hungarian algorithm on the cost matrix. This algorithm
     * finds the minimum cost path to assign all students to seats. (See buildCostMatrix documentation for seats/students).
     * Populates student field for section and section field for students.
     */
    public void sortHungarian() {

        // Convert ArrayLists to arrays as prep for hungarian algorithm
        Section[] sectionsArray = new Section[sections.size()];
        Student[] studentArray = new Student[students.size()];
        sectionsArray = sections.toArray(sectionsArray);
        studentArray = students.toArray(studentArray);

        // Build the cost matrix and execute the hungarian algorithm
        buildCostMatrix();
        HungarianAlgorithm hungary = new HungarianAlgorithm(costMatrix);
        result = hungary.execute();


        for (int i = 0; i < studentArray.length; i++) {
            // Handle cases where people[i] is -1 (unassigned student)
            if (result[i] < 0) {
                unassigned.add(studentArray[i]);
                continue;
            }
            // Assign students to sections, and update section field of students
            Section currentSection = seatMap.getSection(result[i]); // people[i] might be negative
            currentSection.addStudent(studentArray[i]);
            studentArray[i].setAssignedSection(currentSection);
        }
    }

    /**
     * Builds a the cost matrix for use in the Hungarian algorithm. Takes into account preferences, gender, and athlete statuses.
     * @rows students, including dummy students. Actual students come first, dummy students are created to make the matrix square.
     * @cols seats. Each seat belongs to a section, sections contain seats, which are grouped together in a row in the cost matrix. There are often more seats than students.
     * @entries The cost of assigning a student to a seat. Seats are mostly gendered by default in addition to being mostly reserved for non-athletes.
     * @return 2D array of costs (doubles)
     */
    public double[][] buildCostMatrix() { // sectionsArray will be used later to hard encode male/female ratios, etc

            double alpha = 3.5;
            double defaultWeight = Math.pow(alpha, 7);
            double illegalWeight = Math.pow(alpha, 9);
            double minGenderRatio = 0.3;  //minimum ratio for a gender
            double minNerdRatio = 0; //minimum ratio for nerds
            if(controlAthlete) {
                minNerdRatio = .15;
            }
            int numFemaleNerds;
            int numMaleNerds;
            int numFemales;
            int numMales;
            int numNoGenderNerds;

            Section currentSection;

            Section[] sectionsArray = new Section[sections.size()];
            Student[] studentsArray = new Student[students.size()];
            sectionsArray = sections.toArray(sectionsArray); // Not used
            studentsArray = students.toArray(studentsArray);

            // Initialize costMatrix array
            costMatrix = new double[numSeats][numSeats];

            // Sets everything to nullWeight
            for (int i = 0; i < numSeats; i++) {
                for (int j = 0; j < numSeats; j++) {
                    costMatrix[i][j] = defaultWeight;
                }
            }

            // Loop through students, grab their preferences, and update the cost matrix for each preference
            // Currently accounts for gender by setting the first 'n' seats to female, next 'n' to male.
            for (int i = 0; i < studentsArray.length; i++) {
                // Loop through the preferences
                ArrayList<Section> preferences = studentsArray[i].getPreferences();
                for (int j = 0; j < preferences.size(); j++) {
                    currentSection = preferences.get(j);
                    // Get the indices in the cost matrix corresponding to the section of current preference
                    int[] indices = seatMap.getIndices(currentSection);
                    //set numMaleNerds and numFemaleNerds
                    numFemales = (int) (currentSection.getCap() * minGenderRatio + .5) - currentSection.getNumFemaleStudents();
                    numMales = (int) (currentSection.getCap() * minGenderRatio + .5) - currentSection.getNumMaleStudents();
                    numFemaleNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumFemaleNerds();
                    numMaleNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumMaleNerds();
                    numNoGenderNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumFemaleNerds() - currentSection.getNumMaleNerds();
                    if (numFemaleNerds < 0) {numFemaleNerds = 0;}
                    if (numMaleNerds < 0) {numMaleNerds = 0;}
                    if(numNoGenderNerds < 0) {numNoGenderNerds = 0;}
                    for (int k = 0; k < indices.length; k++) {
                        // Set the first seats to be female nerds
                        if (k < numFemaleNerds) {
                            if (!studentsArray[i].isMale() && !studentsArray[i].isAthlete()) {
                                costMatrix[i][indices[k]] = weightFunction(alpha, j);
                            }
                        }
                        // Set the next seats to be females
                        else if (k < numFemales) {
                            if (!studentsArray[i].isMale()) {
                                costMatrix[i][indices[k]] = weightFunction(alpha, j);
                            }
                        }
                        // Set the next seats to be male nerds
                        else if (k < numMaleNerds + numFemales) {
                            if (studentsArray[i].isMale() && !studentsArray[i].isAthlete()) { //never enter
                                costMatrix[i][indices[k]] = weightFunction(alpha, j);
                            }
                        }
                        // Set the next seats to be males
                        else if (k < numFemales + numMales) {
                            if (studentsArray[i].isMale()) {
                                costMatrix[i][indices[k]] = weightFunction(alpha, j);
                            }
                        }
                        //Set the next seat to be a gendered Nerd
                        else if (k < numFemales + numMales + numNoGenderNerds){
                            if(!studentsArray[i].isAthlete()){
                                costMatrix[i][indices[k]] = weightFunction(alpha, j);
                            }
                        }
                        // The remaining seats should be biased only by the preference level
                        else {
                            if(!studentsArray[i].isAthlete()) {
                                costMatrix[i][indices[k]] = weightFunction(alpha, j);
                            }
                        }
                    }
                }

                // Loop through remaining non-preferred sections and account for gender balance
                for (int j = 0; j < sections.size(); j++) {
                    currentSection = sections.get(j);
                    if (preferences.contains(currentSection)) {
                        continue;
                    } // skip if we've already done it in preferences

                    // Get the indices in the cost matrix corresponding to the section of current preference
                    int[] indices = seatMap.getIndices(currentSection);

                    //set numMaleNerds and numFemaleNerds
                    numFemales = (int) (currentSection.getCap() * minGenderRatio + .5) - currentSection.getNumFemaleStudents();
                    numMales = (int) (currentSection.getCap() * minGenderRatio + .5) - currentSection.getNumMaleStudents();
                    numFemaleNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumFemaleNerds();
                    numMaleNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumMaleNerds();
                    numNoGenderNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumFemaleNerds() - currentSection.getNumMaleNerds();
                    if (numFemaleNerds < 0) {numFemaleNerds = 0;}
                    if (numMaleNerds < 0) {numMaleNerds = 0;}
                    if(numNoGenderNerds < 0) {numNoGenderNerds = 0;}
                        for (int k = 0; k < indices.length; k++) {
                        // Set the first seats to be biased against female athletes and all males
                        if (k < numFemaleNerds) {
                            if (studentsArray[i].isMale() || studentsArray[i].isAthlete()) {
                                costMatrix[i][indices[k]] = illegalWeight;

                            }
                        }
                        // Set the next seats to be biased against all males
                        else if (k < numFemales){
                            if (studentsArray[i].isMale()) {
                                costMatrix[i][indices[k]] = illegalWeight;

                            }
                        }
                        // Set the next seats to be biased against male athletes and all females
                        else if (k < numMaleNerds + numFemales) {
                            if (!studentsArray[i].isMale() || studentsArray[i].isAthlete()) {
                                costMatrix[i][indices[k]] = illegalWeight;

                            }
                        }
                        //Set the next seats to be biased against all females
                        else if (k < numMales + numFemales) {
                            if (!studentsArray[i].isMale()) {
                                costMatrix[i][indices[k]] = illegalWeight;

                            }
                        }
                        //Set the next seats to be biased againts athletes
                        else if (k < numFemales + numMales + numNoGenderNerds){
                            if(studentsArray[i].isAthlete()){
                                costMatrix[i][indices[k]] = illegalWeight;
                            }
                        }
                        else {
                            if(studentsArray[i].isAthlete()){
                                costMatrix[i][indices[k]] = illegalWeight;
                            }
                        }
                    }
                }

            }

            // make illegal sections have illegalWeight
            for (int i = 0; i < studentsArray.length; i++) {
                ArrayList<String> illegalSections = studentsArray[i].getIllegalSections();
                for (int j = 0; j < illegalSections.size(); j++) {
                    if (!illegalSections.get(j).equals("")) { // max is suspicious
                        int[] indices = seatMap.getIndices(illegalSections.get(j));
                        for (int k = 0; k < indices.length; k++) {
                            costMatrix[i][indices[k]] = illegalWeight;
                        }
                    }
                }
            }

            // Go through the dummy students and set the gendered seats to illegalWeight
            for (int i = studentsArray.length; i < numSeats; i++) {
                for (int j = 0; j < sections.size(); j++) {
                    currentSection = sections.get(j);
                    // Get the indices in the cost matrix corresponding to the section of current preference
                    int[] indices = seatMap.getIndices(currentSection);
                    //set numMaleNerds and numFemaleNerds
                    numFemales = (int) (currentSection.getCap() * minGenderRatio + .5) - currentSection.getNumFemaleStudents();
                    numMales = (int) (currentSection.getCap() * minGenderRatio + .5) - currentSection.getNumMaleStudents();
                    numFemaleNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumFemaleNerds();
                    numMaleNerds = (int) (currentSection.getCap() * minNerdRatio + .5) - currentSection.getNumMaleNerds();
                    for (int k = 0; k < indices.length; k++) {
                        if (k < numMales + numFemales) {
                            costMatrix[i][indices[k]] = illegalWeight;
                        }
                    }
                }
            }

            return costMatrix;

    }

    /**
     * Weight function takes alpha constant and j variable. Likely will have to change later in order to optimize
     * preferences, gender, and other factors.
     * @param alpha
     * @param j
     * @return
     */
    private double weightFunction(double alpha, int j) {
        return Math.pow(alpha, j + 1);
    }

    /**
     * Filters the students by gender and athlete. Both params required.
     * @param isMale
     * @param isAthlete
     * @return
     */
    private ArrayList<Student> filter(boolean isMale, boolean isAthlete) {
        ArrayList<Student> filtered = new ArrayList<>();
        for (Student s : students) {
            if (s.isMale() == isMale && s.isAthlete() == isAthlete) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    /**
     * Returns a string used for printing a 2D double array. Useful for debugging purposes when examining the cost matrix.
     * @param mat
     * @return
     */
    public String prettifyMatrix(double[][] mat) {
        String s = "";
        for (double[] row : mat) {
            for (double element : row) {
                s += (int) element + "\t\t";
            }
            s += "\n";
        }
        return s;
    }


    /**
     * Returns a string for printing a 1D integer array. Useful for debugging when examining the result matrix from the
     * hungarian algorithm.
     * @param mat
     * @return
     */
    public String prettifyMatrix(int[] mat) {
        String s = "";
        for (double element : mat) {
            s += element + "\t\t";
        }
        s += "\n";
        return s;
    }

    /**
     * Returns the cost matrix
     * @return costMatrix
     */
    public double[][] getCostMatrix() {
        return costMatrix;
    }

    /**
     * Returns the cost. Cost is the sum of the entries in the cost matrix corresponding to student assignments.
     * @return
     */
    public double getCost() {
        return cost;
    }

    /**
     * Returns an arrayList of sections.
     * @return
     */
    public ArrayList<Section> getSections() {
        return sections;
    }

    /**
     * Returns an arrayList of students. Contains pre-assigned students after run() is called.
     * @return
     */
    public ArrayList<Student> getStudents() {
        return students;
    }

    /**
     * Returns an arrayList of preassigned students.
     * @return
     */
    public ArrayList<Student> getAssigned() {
        return assigned;
    }

    /**
     * Returns the number of total seats (?)
     * Not useful for practical use.
     * @return
     */
    public int getNumSeats() {
        return numSeats;
    }

    /**
     * Returns a hashtable that maps student IDs to the number of times they are present in our data.
     * Useful for determining duplicate students in the data set, which is almost always due to a
     * duplicate student in the input student csv file.
     * @return
     */
    public Hashtable<String, Integer> getIdFrequencyTable() {
        return idFrequencyTable;
    }

    /**
     * Returns parser.
     * @return
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * Main static boy
     *
     * @param args
     */
    public static void main(String[] args) {
        SortingHat sortingHat = new SortingHat(new File("csvparsetestSECT.csv"), new File("csvparsetestSTUD.csv"), true, true);
        System.out.println("The Cost Array is:");
        System.out.println(sortingHat.prettifyMatrix(sortingHat.buildCostMatrix()));
        System.out.println("The assignment array is:");
        System.out.println("The Cost Array is:");
    }

}

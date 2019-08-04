package edu.lclark.sortinghat;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * maps cost matrix columns to class sections.
 */
public class SeatIndexMap {
    //TODO add support for mapping seats to specific genders and athletes

    private int numSeats;
    private ArrayList<Seat> seats;
    private ArrayList<Section> sections;
    private Hashtable<Section, Integer> sectionTable;

    public SeatIndexMap(ArrayList<Section> sections, int n) {
        this.sections = sections;
        numSeats = n;
        //build all the seats
        seats = new ArrayList<>();
//        for (int i = 0; i < s.size(); i++) {
//            for (int j = 0; j < (s.get(i).getSize() - s.get(i).getNumStudents()); j++) {
//                seats.add(new Seat(s.get(i)));
//            }
//        }
        for (Section s : sections) {
            for (int i = 0; i < s.getNumAvailableSeats(); i++) {
                seats.add(new Seat(s));
            }
        }
        // Build our lookup tables
        int start = 0;
        sectionTable = new Hashtable<>();
        for (Section sec : sections) {
            sectionTable.put(sec, start);
            start += sec.getNumAvailableSeats();
        }
    }

    /**
     * Gets all the seats corresponding to the given section
     *
     * @param s section
     * @return array of indices
     */
    public int[] getIndices(Section s) {
        int start = sectionTable.get(s);
        int size = s.getNumAvailableSeats();
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = start + i;
        }
        return indices;
    }

    public int[] getIndices(String s) {
        for (Section sec : sections) {
            if (sec.getSectionNo().equals(s)) {
                return getIndices((sec));
            }
        }
        System.out.println(s);
        throw new java.lang.Error("Not a section number");
    }

//    public int[] getIndices(String section) {
//        ArrayList<Integer> indices = new ArrayList<Integer>();
//        //find all seats for thegiven section
//        for (int i = 0; i < seats.length; i++) {
//            if (seats[i].section.equals(section)) {
//                indices.add(i);
//            }
//        }
//        //make the array list into an array
//        int[] indicesArray = new int[indices.size()];
//        for (int i = 0; i < indices.size(); i++) {
//            indicesArray[i] = indices.get(i);
//        }
//        return indicesArray;
//    }

    /**
     * Returns the section of a given seat number
     */
    public Section getSection(int s) {
        return seats.get(s).section;
    }

}
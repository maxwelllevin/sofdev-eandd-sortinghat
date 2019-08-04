package edu.lclark.sortinghat;

public class Seat {

    protected Section section;
    protected boolean male;
    protected boolean athlete;

    public Seat(Section section) {
        this.section = section;
    }

    public Seat(Section section, boolean male) { //for gender functionality
        this.section = section;
        this.male = male;
    }

    public Seat(Section section, boolean male, boolean athlete) {
        this.section = section;
        this.male = male;
        this.athlete = athlete;
    }

}
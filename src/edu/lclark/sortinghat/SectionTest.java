package edu.lclark.sortinghat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class SectionTest {

    private Section section;

    @Before
    public void setUp() {
        section = new Section("107-11", "Drake", 0);
    }

    @Test
    public void sectionCreated() {
        Assert.assertEquals(section.getSectionNo(), "107-11");
        Assert.assertEquals(section.getProf(), "Drake");
    }

}
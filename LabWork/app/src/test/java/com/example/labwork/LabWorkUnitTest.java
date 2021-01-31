package com.example.labwork;

import Publication.PublicationClass;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LabWorkUnitTest {
    @Test
    public void publicationConstructor_isCorrect() {
        PublicationClass publication = new PublicationClass("www");
        Assert.assertEquals(publication.getUrl(), "");
        Assert.assertEquals(publication.getLink(), "www");
        Assert.assertEquals(publication.getDescription(), "");
    }
}

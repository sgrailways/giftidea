package com.sgrailways.giftidea.core;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HashTagLocatorTest {

    HashTagLocator hashTagLocator;

    @Before public void setUp() {
        hashTagLocator = new HashTagLocator();
    }

    @Test public void shouldReturnEmptySetWhenNone() {
        // arrange
        Set<String> expected = new HashSet<>();

        // act
        Set<String> hashTags = hashTagLocator.findAllIn("no tags here");

        // assert
        assertThat(hashTags, is(expected));
    }

    @Test public void shouldReturnSingleHashTag() {
        // arrange
        Set<String> expected = new HashSet<>(asList("#one"));

        // act
        Set<String> hashTags = hashTagLocator.findAllIn("just the #one tag");

        // assert
        assertThat(hashTags, is(expected));
    }

    @Test public void shouldReturnHashTagsInInsertionOrder() {
        // arrange
        Set<String> hashTags = hashTagLocator.findAllIn("#more tags #than7 you'd expect #");

        // act
        Iterator<String> iterator = hashTags.iterator();

        // assert
        assertThat(hashTags.size(), is(2));
        assertThat(iterator.next(), is("#more"));
        assertThat(iterator.next(), is("#than7"));
    }

    @Test public void shouldReturnNotDuplicateHashTags() {
        // arrange
        Set<String> expected = new HashSet<>(asList("#more", "#than7"));

        // act
        Set<String> hashTags = hashTagLocator.findAllIn("#more tags #than7 you'd #MORE expect #");

        // assert
        assertThat(hashTags, is(expected));
    }

    @Test public void shouldRemoveHashTags() {
        // arrange
        String expected = "tags you'd expect";

        // act
        String cleaned = hashTagLocator.removeAllFrom("#more tags #than7 you'd #MORE expect #");

        // assert
        assertThat(cleaned, is(expected));
    }
}

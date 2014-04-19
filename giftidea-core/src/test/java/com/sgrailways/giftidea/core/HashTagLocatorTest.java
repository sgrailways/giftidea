package com.sgrailways.giftidea.core;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HashTagLocatorTest {

    HashTagLocator hashTagLocator;

    @Before public void setUp() {
        hashTagLocator = new HashTagLocator();
    }

    @Test public void shouldReturnEmptySetWhenNone() {
        Set<String> hashTags = hashTagLocator.findAllIn("no tags here");
        assertThat(hashTags.size(), is(0));
    }

    @Test public void shouldReturnSingleHashTag() {
        // arrange
        Set<String> expected = new HashSet<>();
        expected.add("#one");

        // act
        Set<String> hashTags = hashTagLocator.findAllIn("just the #one tag");

        // assert
        assertThat(hashTags, is(expected));
    }

    @Test public void shouldReturnMultipleHashTagsInInsertionOrder() {
        Set<String> hashTags = hashTagLocator.findAllIn("#more tags #than7 you'd expect #");
        assertThat(hashTags.size(), is(2));
        Iterator<String> iterator = hashTags.iterator();
        assertThat(iterator.next(), is("#more"));
        assertThat(iterator.next(), is("#than7"));
    }

    @Test public void shouldReturnNotDuplicateHashTags() {
        Set<String> hashTags = hashTagLocator.findAllIn("#more tags #than7 you'd #MORE expect #");
        assertThat(hashTags.size(), is(2));
        Iterator<String> iterator = hashTags.iterator();
        assertThat(iterator.next(), is("#more"));
        assertThat(iterator.next(), is("#than7"));
    }

    @Test public void shouldRemoveHashTags() {
        String cleaned = hashTagLocator.removeAllFrom("#more tags #than7 you'd #MORE expect #");
        assertThat(cleaned, is("tags you'd expect"));
    }
}

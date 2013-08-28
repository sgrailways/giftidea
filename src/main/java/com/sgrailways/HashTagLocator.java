package com.sgrailways;

import com.google.inject.Singleton;

import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class HashTagLocator {

    private final Pattern pattern = Pattern.compile("(#\\w+)");

    public LinkedHashSet<String> findAllIn(String s) {
        LinkedHashSet<String> hashTags = new LinkedHashSet<String>();
        Matcher matcher = pattern.matcher(s);
        while(matcher.find()) {
            hashTags.add(matcher.group().toLowerCase());
        }
        return hashTags;
    }
}

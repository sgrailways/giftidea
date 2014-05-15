package com.sgrailways.giftidea.core;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class HashTagLocator {
    private static final Pattern HASHTAG = Pattern.compile("(#\\w+)");
    private static final Pattern STRAY_HASH = Pattern.compile("#");
    private static final Pattern[] REMOVAL_PATTERNS = new Pattern[] {HASHTAG, STRAY_HASH};

    @Inject
    public HashTagLocator() {
    }

    public LinkedHashSet<String> findAllIn(String s) {
        LinkedHashSet<String> hashTags = new LinkedHashSet<String>();
        Matcher matcher = HASHTAG.matcher(s);
        while(matcher.find()) {
            hashTags.add(matcher.group().toLowerCase(Locale.getDefault()));
        }
        return hashTags;
    }

    public String removeAllFrom(String s) {
        String result = s;
        for(Pattern p : REMOVAL_PATTERNS) {
            Matcher matcher = p.matcher(s);
            while(matcher.find()) {
                result = result.replaceAll(matcher.group(), "");
            }
        }
        return result.replaceAll("\\s+", " ").trim();
    }
}

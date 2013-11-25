package com.sgrailways.giftidea;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class HashTagLocator {

    private final Pattern pattern = Pattern.compile("(#\\w+)");

    public LinkedHashSet<String> findAllIn(String s) {
        LinkedHashSet<String> hashTags = new LinkedHashSet<String>();
        Matcher matcher = pattern.matcher(s);
        while(matcher.find()) {
            hashTags.add(matcher.group().toLowerCase(Locale.getDefault()));
        }
        return hashTags;
    }

    public String removeAllFrom(String s) {
        Matcher matcher = pattern.matcher(s);
        String hashTagFree = s;
        while (matcher.find()) {
            hashTagFree = StringUtils.remove(hashTagFree, matcher.group());
        }
        hashTagFree = StringUtils.remove(hashTagFree, "#");
        return StringUtils.normalizeSpace(hashTagFree);
    }
}

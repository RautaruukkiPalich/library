package com.app.utils;

import org.springframework.web.util.HtmlUtils;

public class NormalizeSanitizer{
    public static String sanitize(String str){
        return str == null ?
                null :
                HtmlUtils.htmlEscape(
                        str.trim().replaceAll("\\s+", " "));
    }

    public static String normalize(String str){
        return str == null ?
                null :
                str.toLowerCase().trim();
    }
}

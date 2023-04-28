package com.arcgis.project.app.utils;

public class Utils {

    public static String textWithoutWhiteSpaces(String textWithTrailingWhiteSpaces) {
        return textWithTrailingWhiteSpaces.replaceAll("\\s", "");
    }
}

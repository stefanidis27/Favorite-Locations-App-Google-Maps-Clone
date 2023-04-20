package com.arcgis.project.app.utils;

public class Utils {

    public static String textWithoutTrailingWhiteSpaces(String textWithTrailingWhiteSpaces) {
        return textWithTrailingWhiteSpaces.replaceAll("\\s", "");
    }
}

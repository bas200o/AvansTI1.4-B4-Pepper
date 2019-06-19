package com.b4.pepper.model.speech;

import java.util.HashMap;
import java.util.Map;

public class ConceptLibrary {
    public final static String greetings =
            "((ja|ok|nee|nope|graag|nah|yup|(als..blieft)).*)";
    public static String greetingsNegative =
            "((nee|nope|nah).*)";
    public static String greetingsPositive =
            "((ja|ok|graag|yup|(als..blieft)).*)";
    public static String MetHoeveelMensen =
            "\\d+.*";
}

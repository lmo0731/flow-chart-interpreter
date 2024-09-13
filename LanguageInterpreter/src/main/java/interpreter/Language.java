/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author LMO
 */
public class Language {
    //                                               0       1     2       3        4       5       6      7      8       9       10

    private static String[] keyword = new String[]{"loop", "if", "else", "print", "read", "array", "and", "or", "true", "false", "break", "while", "not"};
    public static HashMap<String, Integer> OPERATORS = new HashMap<String, Integer>() {

        {
            put(keyword[7], 1);
            put(keyword[8], 1);
            put("||", 1);
            put("or", 1);
            put("&&", 1);
            put("and", 1);
            put("==", 2);
            put("=", 2);
            put("!=", 2);
            put("!", 2);
            put("not", 2);
            put("<>", 2);
            put("<=", 2);
            put(">=", 2);
            put("<", 2);
            put(">", 2);
            put("+", 3);
            put("-", 3);
            put("*", 4);
            put("/", 4);
            put("%", 4);
            //put("^", 5);
        }
    };
    public static final Vector<String> KEYWORDS = new Vector<String>(Arrays.asList(keyword));

    public static boolean isInt(String word) {
        return word.matches("^[-]?[0-9]+$");
    }

    public static boolean isVar(String word) {
        return (word.matches("^([a-zA-Z]+[a-zA-Z0-9_]*|[_][a-zA-Z0-9_]*[a-zA-Z0-9][a-zA-Z0-9_]*)$") && !KEYWORDS.contains(word.toLowerCase()));
    }

    public static boolean isNum(String word) {
        return word.matches("^[-]?[0-9]+([.][0-9]+)?$");
    }

    public static boolean isStr(String word) {
        return word.length() > 0 && word.charAt(0) == word.charAt(word.length() - 1) && (word.charAt(0) == '\'' || word.charAt(0) == '\"');
    }

    public static boolean isBool(String word) {
        return (Language.trueKeys().contains(word.toLowerCase()) || Language.falseKeys().contains(word.toLowerCase()));
    }

    public static ArrayList<String> beginKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"{", "begin"}));
    }

    public static ArrayList<String> endKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"}", "end"}));
    }

    public static ArrayList<String> ifKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"if"}));
    }

    public static ArrayList<String> elseKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"else"}));
    }

    public static ArrayList<String> whileKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"loop", "while"}));
    }

    public static ArrayList<String> breakKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"break"}));
    }

    public static ArrayList<String> printKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"print"}));
    }

    public static ArrayList<String> readKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"read"}));
    }

    public static ArrayList<String> arrayKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"array"}));
    }

    public static ArrayList<String> seperator() {
        return new ArrayList<String>(Arrays.asList(new String[]{";"}));
    }

    public static ArrayList<String> trueKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"true"}));
    }

    public static ArrayList<String> falseKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"false"}));
    }

    public static ArrayList<String> functionKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"function"}));
    }

    public static ArrayList<String> orKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"or", "||"}));
    }

    public static ArrayList<String> andKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"and", "&&"}));
    }

    public static ArrayList<String> notKeys() {
        return new ArrayList<String>(Arrays.asList(new String[]{"!", "not"}));
    }
}

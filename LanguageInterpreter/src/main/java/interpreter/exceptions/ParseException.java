/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.exceptions;

/**
 *
 * @author LMO
 */
public class ParseException extends BuildException {

    public static int ERROR_INVALID_FUNCTION_START = 1;
    public static int ERROR_MISSING_BEGIN_KEYWORD = 2;
    public static int ERROR_MISSING_END_KEYWORD = 3;
    public static int ERROR_MISSING_SEPARATOR = 4;
    public static int ERROR_MISSING_SET_OPERATOR = 5;
    public static int ERROR_INVALID_COMMAND = 6;
    public static int ERROR_INVALID_DECLARATION = 7;
    public static int ERROR_MISSING_LEFT_SQUARE_BRACKET = 8;
    public static int ERROR_MISSING_RIGHT_SQUARE_BRACKET = 9;
    public static int ERROR_INVALID_VARIABLE_NAME = 10;
    public static int ERROR_MISSING_OPERATOR = 11;
    public static int ERROR_ILLEGAL_EXPRESSION = 12;
    public static int ERROR_MISSING_LEFT_BRACKET = 8;
    public static int ERROR_MISSING_RIGHT_BRACKET = 9;
    public static int ERROR_NUMBER_TOO_LARGE = 10;
    public static int ERROR_INVALID_VALUE = 11;
    public static int ERROR_INVALID_PARAMETER_TYPE = 12;
    public static int ERROR_DEVELOPER_ON_POLISH = 100;

    public ParseException(String name, int line, String message, int id) {
        super(name, line, ParseException.getErrorMessage(id) + " near: \"" + message + "\"");
    }

    private static String getErrorMessage(int id) {
        if (id == ERROR_INVALID_FUNCTION_START) {
            return "Invalid function start";
        } else if (id == ERROR_MISSING_BEGIN_KEYWORD) {
            return "Missing begin keyword";
        } else if (id == ERROR_MISSING_END_KEYWORD) {
            return "Missing end keyword";
        } else if (id == ERROR_MISSING_SEPARATOR) {
            return "Missin seperator";
        } else if (id == ERROR_MISSING_SET_OPERATOR) {
            return "Missing set operator";
        } else if (id == ERROR_INVALID_COMMAND) {
            return "Invalid command keyword";
        } else if (id == ERROR_INVALID_DECLARATION) {
            return "Invalid declaration";
        } else if (id == ERROR_MISSING_LEFT_SQUARE_BRACKET) {
            return "Missing '[' ";
        } else if (id == ERROR_MISSING_RIGHT_SQUARE_BRACKET) {
            return "Missing ']'";
        } else if (id == ERROR_INVALID_VARIABLE_NAME) {
            return "Invalid vaiable name";
        } else if (id == ERROR_MISSING_OPERATOR) {
            return "Missing operator";
        } else if (id == ERROR_ILLEGAL_EXPRESSION) {
            return "Use seperator! Illegal expression";
        } else if (id == ERROR_MISSING_LEFT_BRACKET) {
            return "Missin '('";
        } else if (id == ERROR_MISSING_RIGHT_BRACKET) {
            return "Missing ')'";
        } else if (id == ERROR_NUMBER_TOO_LARGE) {
            return "Number is too large";
        } else if (id == ERROR_INVALID_VALUE) {
            return "Invalid value";
        } else if (id == ERROR_INVALID_PARAMETER_TYPE) {
            return "Invalid parameter type";
        } else if (id == ERROR_DEVELOPER_ON_POLISH) {
            return "Developer error on polish";
        }
        return "Unknown Error";
    }
}

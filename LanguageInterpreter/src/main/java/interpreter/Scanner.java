/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author LMO
 */
public class Scanner {

    static final Vector<String> SPACES = new Vector<String>(Arrays.asList(
            new String[]{"\n", " ", "\r", "\t"}));
    static final Vector<String> OP1 = new Vector<String>(Arrays.asList(
            new String[]{"!", "%", "^", "&", "*", "(", ")", "-", "=", "_", "+", "{", "}", "[", "]", "\\", "|", ";", ":", "\'", "\"", "<", ">", ",", ".", "/"}));
    static final Vector<String> OP2 = new Vector<String>(Arrays.asList(
            new String[]{"!=", "<=", "<>", ">=", "==", "&&", "||"}));
    static final HashMap<String, String> BACKSLASHED = new HashMap<String, String>() {

        {
            put("n", "\n");
            put("r", "\r");
            put("b", "\b");
            put("\\", "\\");
            put("\'", "\'");
            put("\"", "\"");
        }
    };
    String path;
    Vector<Vector<String>> answer;
    Vector<String> line;
    String nextWord;
    String name;

    public Scanner() {
    }

    private void addLine() {
        this.answer.add(this.line);
        this.line = new Vector<String>();
    }

    private void addWord() {
        if (!this.nextWord.isEmpty()) {
            this.line.add(this.nextWord);
            this.nextWord = "";
        }
    }

    private void addLetter(String letter) {
        this.nextWord += letter;
    }

    public Vector<Vector<String>> scan(String name, String code) {
        this.answer = new Vector<Vector<String>>();
        this.line = new Vector<String>();
        this.nextWord = "";
        this.path = "";
        this.name = name;
        String quote = "";
        boolean backslash = false;
        boolean dot = false;
        for (int i = 0; i < code.length(); i++) {
            String letter = "" + code.charAt(i);
            if (!quote.isEmpty()) {
                if (backslash) {
                    backslash = false;
                    if (Scanner.BACKSLASHED.containsKey(letter)) {
                        this.addLetter(Scanner.BACKSLASHED.get(letter));
                    }
                } else if (letter.equals(quote)) {
                    quote = "";
                    this.addLetter(letter);
                    this.addWord();
                } else if (letter.equals("\\")) {
                    backslash = true;
                } else {
                    this.addLetter(letter);
                }
            } else if (Scanner.SPACES.contains(letter)) {
                this.addWord();
                if (letter.equals("\n")) {
                    this.addLine();
                }
            } else if (Scanner.OP1.contains(letter)) {
                if (letter.equals("\'") || letter.equals("\"")) {
                    this.addWord();
                    quote = letter;
                    this.addLetter(letter);
                } else if (Scanner.OP2.contains(nextWord + letter)) {
                    this.addLetter(letter);
                    this.addWord();
                } else if (letter.equals(".") && Language.isInt(nextWord)) {
                    dot = true;
                } else {
                    this.addWord();
                    this.addLetter(letter);
                }
            } else {
                if (dot) {
                    dot = false;
                    if (Language.isInt(this.nextWord) && letter.matches("^[0-9]*$")) {
                        this.addLetter(".");
                    } else {
                        dot = false;
                        this.addWord();
                        this.addLetter(".");
                        this.addWord();
                    }
                    this.addLetter(letter);
                } else if (Scanner.OP1.contains(this.nextWord)) {
                    this.addWord();
                    this.addLetter(letter);
                } else {
                    this.addLetter(letter);
                }
            }
        }
        this.addWord();
        this.addLine();
        return this.answer;
    }
}

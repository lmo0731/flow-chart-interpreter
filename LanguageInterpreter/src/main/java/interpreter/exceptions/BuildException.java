/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.exceptions;

/**
 *
 * @author LMO
 */
public class BuildException extends Exception {

    String name;
    int line;

    public BuildException(String name, int line, String message) {
        super(message);
        this.name = name;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }
}

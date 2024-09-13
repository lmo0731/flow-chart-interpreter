/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.exceptions;

/**
 *
 * @author LMO
 */
public class InterpretException extends BuildException {

    public InterpretException(String name, int line, String message) {
        super(name, line, message);
    }
}

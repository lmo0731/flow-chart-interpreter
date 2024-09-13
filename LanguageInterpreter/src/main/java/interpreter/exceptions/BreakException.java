/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.exceptions;

/**
 *
 * @author munkhochir
 */
public class BreakException extends InterpretException {

    public BreakException(String name, int line, String message) {
        super(name, line, message);
    }
}

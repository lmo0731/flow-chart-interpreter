/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.memory;

/**
 *
 * @author LMO
 */
public class NotDeclaredMemoryException extends MemoryException {

    public NotDeclaredMemoryException(String message) {
        super(message);
    }
}

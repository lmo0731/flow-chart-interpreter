/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.memory;

/**
 *
 * @author LMO
 */
public class Location {

    Pointer pointer;
    Object key;

    public Location(Pointer pointer, Object key) {
        this.pointer = pointer;
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    public Pointer getPointer() {
        return pointer;
    }

    @Override
    public String toString() {
        return key.toString() + pointer.toString();
    }
}

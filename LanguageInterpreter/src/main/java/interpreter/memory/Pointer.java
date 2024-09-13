/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.memory;

/**
 *
 * @author LMO
 */
public class Pointer {

    int address;

    public Pointer(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "@" + address;
    }
}

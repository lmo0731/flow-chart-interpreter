/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class VTreeV extends VTree {

    Object value;

    public VTreeV(Object value, String name, Integer line) {
        super(name, line);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value.getClass().equals(String.class)) {
            return "'" + value + "'";
        }
        return value.toString();
    }
}

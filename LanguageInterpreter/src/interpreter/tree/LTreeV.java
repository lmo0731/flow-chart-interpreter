/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class LTreeV extends LTree {

    String var;

    public LTreeV(String var, String name, Integer line) {
        super(name, line);
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    @Override
    public String toString() {
        return "(var,"
                + var + ")";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

import interpreter.Language;
import java.util.Vector;

/**
 *
 * @author LMO
 */
public class LTreeI extends LTree {

    String var;
    Vector<VTree> itree;

    public LTreeI(String var, Vector<VTree> itree, String name, Integer line) {
        super(name, line);
        this.var = var;
        this.itree = itree;
    }

    public String getVar() {
        return var;
    }

    public Vector<VTree> getItree() {
        return itree;
    }

    @Override
    public String toString() {
        return "(var,"
                + var + ","
                + itree.toString() + ")";
    }
}

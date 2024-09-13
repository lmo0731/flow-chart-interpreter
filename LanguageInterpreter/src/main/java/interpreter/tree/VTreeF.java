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
public class VTreeF extends VTree {

    String function;
    Vector<VTree> args;

    public VTreeF(String function, Vector<VTree> args, String name, Integer line) {
        super(name, line);
        this.function = function;
        this.args = args;
    }

    public Vector<VTree> getArgs() {
        return args;
    }

    public String getFunction() {
        return function;
    }

    @Override
    public String toString() {
        return "(func,"
                + function + ","
                + args.toString() + ")";
    }
}

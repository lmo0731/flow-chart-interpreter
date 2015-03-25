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
public class BTree extends CTree {

    Vector<CTree> ctrees;

    public BTree(Vector<CTree> ctrees, String name, Integer line) {
        super(name, line);
        this.ctrees = ctrees;
    }

    public Vector<CTree> getCtrees() {
        return ctrees;
    }

    @Override
    public String toString() {
        return ctrees.toString();
    }
}

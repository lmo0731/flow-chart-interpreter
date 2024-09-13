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
public class FTree extends Tree {

    Vector<LTreeV> ptree;
    BTree btree;
    VTree vtree;

    public FTree(Vector<LTreeV> ptree, BTree btree, VTree vtree, String name, Integer line) {
        super(name, line);
        this.ptree = ptree;
        this.btree = btree;
        this.vtree = vtree;
    }

    public BTree getBtree() {
        return btree;
    }

    public Vector<LTreeV> getPtree() {
        return ptree;
    }

    public VTree getVtree() {
        return vtree;
    }

    @Override
    public String toString() {
        return "("
                + ptree.toString() + ","
                + btree.toString() + ","
                + vtree.toString() + ")";
    }
}

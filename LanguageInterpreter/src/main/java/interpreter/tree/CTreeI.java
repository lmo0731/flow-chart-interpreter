/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class CTreeI extends CTree {

    VTree vtree;
    BTree b1tree;
    BTree b2tree;

    public CTreeI(VTree vtree, BTree b1tree, BTree b2tree, String name, Integer line) {
        super(name, line);
        this.vtree = vtree;
        this.b1tree = b1tree;
        this.b2tree = b2tree;
    }

    public VTree getVtree() {
        return vtree;
    }

    public BTree getB1tree() {
        return b1tree;
    }

    public BTree getB2tree() {
        return b2tree;
    }

    @Override
    public String toString() {
        return "(if,"
                + vtree.toString() + ","
                + b1tree.toString() + ","
                + b2tree.toString() + ")";
    }
}

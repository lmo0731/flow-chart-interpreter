/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class CTreeW extends CTree {

    VTree vtree;
    BTree btree;

    public CTreeW(VTree vtree, BTree btree, String name, Integer line) {
        super(name, line);
        this.vtree = vtree;
        this.btree = btree;
    }

    public BTree getBtree() {
        return btree;
    }

    public VTree getVtree() {
        return vtree;
    }

    @Override
    public String toString() {
        return "(while," + btree.toString() + ")";
    }
}

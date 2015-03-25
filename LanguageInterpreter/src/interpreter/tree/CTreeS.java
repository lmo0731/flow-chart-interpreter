/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class CTreeS extends CTree {

    LTree ltree;
    VTree vtree;

    public CTreeS(LTree ltree, VTree vtree, String name, Integer line) {
        super(name, line);
        this.ltree = ltree;
        this.vtree = vtree;
    }

    public LTree getLtree() {
        return ltree;
    }

    public VTree getVtree() {
        return vtree;
    }

    @Override
    public String toString() {
        return "(=,"
                + ltree.toString() + ","
                + vtree.toString() + ")";
    }
}

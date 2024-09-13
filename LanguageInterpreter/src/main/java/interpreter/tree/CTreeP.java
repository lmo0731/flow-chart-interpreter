/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class CTreeP extends CTree {

    VTree vtree;

    public CTreeP(VTree vtree, String name, Integer line) {
        super(name, line);
        this.vtree = vtree;
    }

    public VTree getVtree() {
        return vtree;
    }

    @Override
    public String toString() {
        return "(print,"
                + vtree.toString() + ")";
    }
}

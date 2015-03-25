/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class CTreeD extends CTree {

    LTree ltree;
    DTree dtree;

    public CTreeD(LTree ltree, DTree dtree, String name, Integer line) {
        super(name, line);
        this.ltree = ltree;
        this.dtree = dtree;
    }

    public DTree getDtree() {
        return dtree;
    }

    public LTree getLtree() {
        return ltree;
    }

    @Override
    public String toString() {
        return "(array," + ltree.toString() + "," + dtree.toString() + ")";
    }
}

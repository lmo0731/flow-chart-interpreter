/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class CTreeR extends CTree {

    LTree ltree;

    public CTreeR(LTree ltree, String name, Integer line) {
        super(name, line);
        this.ltree = ltree;
    }

    public LTree getLtree() {
        return ltree;
    }

    @Override
    public String toString() {
        return "(read,"
                + ltree.toString() + ")";
    }
}

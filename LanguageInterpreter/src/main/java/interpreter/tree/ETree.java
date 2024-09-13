/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class ETree extends VTree {

    String operator;
    VTree v1tree;
    VTree v2tree;

    public ETree(String operator, VTree v1tree, VTree v2tree, String name, Integer line) {
        super(name, line);
        this.operator = operator;
        this.v1tree = v1tree;
        this.v2tree = v2tree;
    }

    public String getOperator() {
        return operator;
    }

    public VTree getV1tree() {
        return v1tree;
    }

    public VTree getV2tree() {
        return v2tree;
    }

    @Override
    public String toString() {
        return "("
                + operator + ","
                + v1tree.toString() + ","
                + v2tree.toString() + ")";
    }
}

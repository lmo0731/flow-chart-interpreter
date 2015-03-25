/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tree;

/**
 *
 * @author LMO
 */
public class Tree {

    String name;
    Integer line;

    public Tree(String name, Integer line) {
        this.name = name;
        this.line = line;
    }

    public Integer getLine() {
        return line;
    }

    public String getName() {
        return name;
    }
}

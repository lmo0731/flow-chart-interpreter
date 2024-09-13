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
public class DTreeA extends DTree {
    
    Vector<VTree> dimension;
    
    public DTreeA(Vector<VTree> dimension, String name, Integer line) {
        super(name, line);
        this.dimension = dimension;
    }
    
    public Vector<VTree> getDimension() {
        return dimension;
    }
    
    @Override
    public String toString() {
        return dimension.toString();
    }
}

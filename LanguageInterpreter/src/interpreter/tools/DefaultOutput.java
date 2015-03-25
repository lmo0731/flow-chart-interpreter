/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tools;

/**
 *
 * @author LMO
 */
public class DefaultOutput implements Output {

    public DefaultOutput() {
    }

    @Override
    public void write(String out) {
        System.out.print(out);
    }
}

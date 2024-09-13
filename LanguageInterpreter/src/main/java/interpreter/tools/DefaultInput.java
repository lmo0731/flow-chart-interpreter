/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tools;

import java.io.BufferedInputStream;
import java.util.Scanner;

/**
 *
 * @author LMO
 */
public class DefaultInput implements Input {

    public DefaultInput() {
    }

    @Override
    public Object read() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextBoolean()) {
            return scanner.nextBoolean();
        } else if (scanner.hasNextLong()) {
            return scanner.nextLong();
        } else if (scanner.hasNextDouble()) {
            return scanner.nextDouble();
        } else if (scanner.hasNext()) {
            return scanner.next();
        }
        return new Long(0);
    }
}

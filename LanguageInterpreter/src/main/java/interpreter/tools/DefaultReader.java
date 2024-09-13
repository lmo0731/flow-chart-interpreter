/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tools;

import interpreter.exceptions.ParseException;
import interpreter.Parser;
import interpreter.tree.FTree;
import interpreter.tree.FTree;
import java.io.*;
import java.util.HashMap;

/**
 *
 * @author LMO
 */
public class DefaultReader implements Reader {

    public DefaultReader() {
    }

    @Override
    public String readCode(String name) throws Exception {
        String filePath = name + ".txt";
        String ret = "";
        BufferedReader in = null;
        File fileDir = new File(filePath);
        System.out.println(fileDir.getAbsolutePath());
        in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileDir), "UTF-8"));
        String str;
        while ((str = in.readLine()) != null) {
            ret += str + "\n";
        }
        System.out.println(ret);
        in.close();
        return ret;
    }
}

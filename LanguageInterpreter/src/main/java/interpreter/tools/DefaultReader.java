/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author LMO
 */
public class DefaultReader implements Reader {

    public File home = new File(System.getProperty("user.dir"));

    public File getHome() {
        return home;
    }

    public void setHome(File home) {
        this.home = home;
    }

    public DefaultReader() {
    }

    @Override
    public String readCode(String name) throws Exception {
        String filePath = home.getAbsolutePath() + File.separatorChar + name + ".txt";
        String ret = "";
        InputStreamReader in = null;
        File fileDir = new File(filePath);
        System.out.println(fileDir.getAbsolutePath());
        try {
            in = new InputStreamReader(new FileInputStream(fileDir), "UTF-8");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (true) {
                int c = in.read();
                if (c == -1) {
                    break;
                }
                baos.write(c);
            }
            ret = baos.toString("UTF-8");
            System.out.println(ret);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return ret;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import config.Config;
import drawer.entity.block.BeginBlock;
import java.io.File;
import org.ho.yaml.Yaml;

/**
 *
 * @author LMO
 */
public class FlowchartReader implements interpreter.tools.Reader {

    private String path;
    private String name;
    private String code;
    private boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getDebug() {
        return debug;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String readCode(String name) throws Exception {
        if (this.name.equals(name)) {
            return code;
        }
        BeginBlock root = (BeginBlock) Yaml.load(new File(path + File.separatorChar + name + "." + Config.getFileFormat()));
        String ret = root.convert(debug);
        System.out.println(ret);
        return ret;
    }
}

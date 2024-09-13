/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import config.Config;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author munkhochir
 */
public class FlowchartFileFilter extends FileFilter {

    String[] formats = new String[]{Config.getFileFormat()};

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        if (formats.length == 0) {
            return true;
        }
        int k = file.getName().lastIndexOf('.');
        if (k < 1) {
            return false;
        }
        for (String format : formats) {
            if (file.getName().toLowerCase().endsWith("." + format.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        if (formats.length == 0) {
            return "All types";
        }
        String desc = "*." + formats[0];
        for (int i = 1; i < formats.length; i++) {
            desc += ", *." + formats[i];
        }
        return desc;
    }
}

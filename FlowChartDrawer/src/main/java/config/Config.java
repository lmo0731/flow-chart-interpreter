/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.awt.Color;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author munkhochir
 */
public class Config {

    public static String SETTINGPATH = System.getenv("userprofile") + File.separatorChar + ".flowchardrawer" + File.separatorChar + "setting.dat";
    private static String fontName = "Arial";
    private static String fileFormat = "yml";
    private static String filePath = System.getenv("userprofile") + File.separatorChar + "Desktop";
    private static int fontSize = 12;
    private static int blockHeight = 40;
    private static int blockWidth = 100;
    private static int blockVerticalPadding = 10;
    private static int blockHorizontalPadding = 10;
    private static int lineSpacing = 5;
    private static Color blockBackgroundColor = new Color(0xee, 0xee, 0xee);
    private static Color blockBorderColor = new Color(0x77, 0x77, 0x77);
    private static Color blockSelectionColor = Color.blue;
    private static Color blockHoverColor = Color.black;
    private static Color blockErrorColor = Color.red;
    private static Color blockCurrentColor = Color.GREEN;
    private static int arrowHead = 3;
    private static int arrowHeight = 15;
    private static int arrowWidth = 20;
    private static int arrowArea = 10;
    private static int zoomLevel = 100;
    private static int iconSize = 10;
    @PropertiesHolder(file = "config.properties", autoLoad = true)
    private static Properties prop;

    static {
        /*Properties fallback = new Properties();
        fallback.put("key", "default");
        prop = new Properties(fallback);
        try {
        //InputStream stream = 
        //...;
        try {
        prop.load(stream);
        } finally {
        stream.close();
        }
        } catch (IOException ex) {
        
        }*/
        Config.zoomLevel = 100;
    }

    public static void setBlockCurrentColor(Color blockCurrentColor) {
        Config.blockCurrentColor = blockCurrentColor;
    }

    public static Color getBlockCurrentColor() {
        return blockCurrentColor;
    }

    public static String getFileFormat() {
        return fileFormat;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static int getArrowHeight() {
        return (int) (arrowHeight * 0.01 * zoomLevel);
    }

    public static int getArrowArea() {
        return (int) (arrowArea * 0.01 * zoomLevel);
    }

    public static int getArrowHead() {
        return (int) (arrowHead * 0.01 * zoomLevel);
    }

    public static int getArrowWidth() {
        return (int) (arrowWidth * 0.01 * zoomLevel);
    }

    public static Color getBlockBackgroundColor() {
        return blockBackgroundColor;
    }

    public static Color getBlockBorderColor() {
        return blockBorderColor;
    }

    public static Color getBlockSelectionColor() {
        return blockSelectionColor;
    }

    public static Color getBlockErrorColor() {
        return blockErrorColor;
    }

    public static Color getBlockHoverColor() {
        return blockHoverColor;
    }

    public static int getBlockHeight() {
        return (int) (blockHeight * 0.01 * zoomLevel);
    }

    public static int getBlockHorizontalPadding() {
        return (int) (blockHorizontalPadding * 0.01 * zoomLevel);
    }

    public static int getBlockVerticalPadding() {
        return (int) (blockVerticalPadding * 0.01 * zoomLevel);
    }

    public static int getBlockWidth() {
        return (int) (blockWidth * 0.01 * zoomLevel);
    }

    public static String getFontName() {
        return fontName;
    }

    public static int getFontSize() {
        return (int) (fontSize * 0.01 * zoomLevel);
    }

    public static int getLineSpacing() {
        return (int) (lineSpacing * 0.01 * zoomLevel);
    }

    public static int getZoomLevel() {
        return zoomLevel;
    }

    public static int getIconSize() {
        return (int) (iconSize * 0.01 * zoomLevel);
    }

    public static void setArrowHeight(int arrowHeight) {
        Config.arrowHeight = arrowHeight;
    }

    public static void setArrowArea(int arrowArea) {
        Config.arrowArea = arrowArea;
    }

    public static void setArrowHead(int arrowHead) {
        Config.arrowHead = arrowHead;
    }

    public static void setArrowWidth(int arrowWidth) {
        Config.arrowWidth = arrowWidth;
    }

    public static void setBlockSelectionColor(Color blockSelectionColor) {
        Config.blockSelectionColor = blockSelectionColor;
    }

    public static void setBlockBackgroundColor(Color blockBackgroundColor) {
        Config.blockBackgroundColor = blockBackgroundColor;
    }

    public static void setBlockBorderColor(Color blockBorderColor) {
        Config.blockBorderColor = blockBorderColor;
    }

    public static void setBlockErrorColor(Color blockErrorColor) {
        Config.blockErrorColor = blockErrorColor;
    }

    public static void setBlockHoverColor(Color blockHoverColor) {
        Config.blockHoverColor = blockHoverColor;
    }

    public static void setBlockHeight(int blockHeight) {
        Config.blockHeight = blockHeight;
    }

    public static void setBlockHorizontalPadding(int blockHorizontalPadding) {
        Config.blockHorizontalPadding = blockHorizontalPadding;
    }

    public static void setBlockVerticalPadding(int blockVerticalPadding) {
        Config.blockVerticalPadding = blockVerticalPadding;
    }

    public static void setBlockWidth(int blockWidth) {
        Config.blockWidth = blockWidth;
    }

    public static void setFontName(String fontName) {
        Config.fontName = fontName;
    }

    public static void setFontSize(int fontSize) {
        Config.fontSize = fontSize;
    }

    public static void setLineSpacing(int lineSpacing) {
        Config.lineSpacing = lineSpacing;
    }

    public static void setZoomLevel(int zoomLevel) {
        Config.zoomLevel = zoomLevel;
    }

    public static void setIconSize(int iconSize) {
        Config.iconSize = iconSize;
    }
}

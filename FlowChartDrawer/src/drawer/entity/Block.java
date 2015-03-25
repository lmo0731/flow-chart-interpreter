package drawer.entity;

import config.Config;
import interpreter.Language;
import interpreter.Parser;
import interpreter.exceptions.ParseException;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.swing.ImageIcon;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author munkhochir
 */
public abstract class Block {

    private String text;
    private String displayText;
    private Stack<Color> foregroundColor;
    protected int height, width, x, y;
    protected int beginLine, endLine, totalLine;//inclusive, exclusive, totalLine,
    private boolean breakpoint = false, selected, error, hovered;

    public Block() {
        this.displayText = "";
        this.setText("");
        foregroundColor = new Stack<Color>();
        foregroundColor.push(Config.getBlockBorderColor());
    }

    protected abstract void initDraw();

    protected abstract void drawBlockBorder(Graphics2D g, int x, int y);

    protected abstract void drawBlockBackground(Graphics2D g, int x, int y);

    final public void setText(String text) {
        this.text = text.trim();
        checkError();
    }

    final protected void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    protected String getDisplayedText() {
        return this.displayText;
    }

    final public void setBreakpoint(boolean bp) {
        this.breakpoint = bp;
    }

    final public boolean is_Breakpoint() {
        return breakpoint;
    }

    final public String getText() {
        return text + " ";
    }

    public static void drawText(Graphics2D g, String text, int x, int y) {
        FontRenderContext frc = g.getFontRenderContext();
        Font font = new Font(Config.getFontName(), Font.PLAIN, Config.getFontSize());
        String[] lines = text.split("\n");
        y -= Block.getTextBounds(text).getHeight() / 2;
        x -= (int) Block.getTextBounds(text).getWidth() / 2;
        if (text.length() != 0) {
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    continue;
                }
                TextLayout layout = new TextLayout(lines[i].trim(), font, frc);
                y += layout.getAscent();
                layout.draw(g, x, y);
                y += Config.getLineSpacing();
            }
        }
    }

    public static Rectangle2D getTextBounds(String text) {
        Graphics2D g = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        FontRenderContext frc = g.getFontRenderContext();
        Font font = new Font(Config.getFontName(), Font.PLAIN, Config.getFontSize());
        String[] lines = text.split("\n");
        int tw = 0, th = 0;
        if (text.length() != 0) {
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    continue;
                }
                TextLayout layout = new TextLayout(lines[i], font, frc);
                int w = (int) layout.getBounds().getWidth();
                int h = (int) layout.getAscent();
                th += h + Config.getLineSpacing();
                tw = Math.max(tw, w);
            }
        }
        return new Rectangle(tw, th);
    }

    public void pushColor(Color color) {
        this.foregroundColor.push(color);
    }

    final public Color peekColor() {
        return foregroundColor.peek();
    }

    public void popColor() {
        if (foregroundColor.size() > 1) {
            this.foregroundColor.pop();
        }
    }

    public void removeTotalColor() {
        this.popColor();
    }

    public void setTotalColor(Color color) {
        this.pushColor(color);
    }

    public int getHeight() {
        return height;
    }

    public int getTotalHeight() {
        return this.getHeight();
    }

    public int getRightWidth() {
        return width / 2;
    }

    public int getLeftWidth() {
        return width / 2;
    }

    public int getTotalRightWidth() {
        return this.getRightWidth();
    }

    public int getTotalLeftWidth() {
        return this.getLeftWidth();
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    final protected void setColors() {
        if (this.isHovered()) {
            this.pushColor(Config.getBlockHoverColor());
        }
        if (this.isSelected()) {
            this.pushColor(Config.getBlockSelectionColor());
        }
        if (this.isError()) {
            this.pushColor(Config.getBlockErrorColor());
        }

    }

    final protected void removeColors() {
        if (this.isError()) {
            this.popColor();
        }
        if (this.isSelected()) {
            this.popColor();
        }
        if (this.isHovered()) {
            this.popColor();
        }
    }

    public int draw(Graphics2D g, int x, int y, int remain) {
        initDraw();
        this.x = x;
        this.y = y;
        Color c = g.getColor();
        this.setColors();
        g.setColor(foregroundColor.peek());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.drawBlock(g, x, y + getBlockOffset());
        this.drawOther(g, x, y);

        this.pushColor(Color.BLACK);
        g.setColor(this.peekColor());
        Block.drawText(g, this.getDisplayedText(), x, y + getBlockOffset() + height / 2);
        this.popColor();
        if (this.isError()) {
            Image img = new ImageIcon(this.getClass().getResource("/resources/error.png")).getImage();
            g.drawImage(img, x - img.getWidth(null) / 2, y + getBlockOffset() + height / 2 - img.getHeight(null) / 2, null);
        }
        removeColors();
        remain -= this.getHeight();
        g.setColor(c);
        return remain;
    }

    protected int getBlockOffset() {
        return 0;
    }

    private void drawBlock(Graphics2D g, int x, int y) {
        g.setColor(Config.getBlockBackgroundColor());
        this.drawBlockBackground(g, x, y);
        g.setColor(foregroundColor.peek());
        this.drawBlockBorder(g, x, y);
    }

    public int getTotalLine() {
        return totalLine;
    }

    protected void drawOther(Graphics2D g, int x, int y) {
        if (this.is_Breakpoint()) {
            Image img = new ImageIcon(this.getClass().getResource("/resources/breakpoint.png")).getImage();
            g.drawImage(
                    img,
                    x - this.getLeftWidth() - img.getWidth(null),
                    y + this.getBlockOffset() + height / 2 - img.getHeight(null) / 2,
                    null);
        }
    }

    final public boolean isSelected(int x, int y) {//is only block selected
        if (this.x - width / 2 < x
                && x < this.x + width / 2
                && this.y + this.getBlockOffset() < y
                && y < this.y + this.getBlockOffset() + height) {
            return true;
        }
        return false;
    }

    public Arrow getBlock(int x, int y) {
        if (this.isSelected(x, y)) {
            return new Arrow(this);
        }
        return null;
    }

    public Arrow getArrow(int x, int y) {
        return null;
    }

    final public String convert(boolean debug) {
        return convert("", 1, debug);
    }

    final public void checkError() {
        try {
            checkTextError();
        } catch (ParseException ex) {
        }
    }

    final public void checkTextError() throws ParseException {
        try {
            error = false;
            Parser parser = new Parser();
            parser.parse("", checker());
            this.setDisplayText(this.text);
        } catch (ParseException ex) {
            error = true;
            throw ex;
        }
    }

    protected String checker() {
        return text.replaceAll("[\\n]", "; ");
    }

    public String convert(String code, int line, boolean debug) {
        beginLine = line;
        if (this.is_Breakpoint() || debug) {
            code += Language.beginKeys().get(0) + " " + Language.endKeys().get(0) + "\n";
            line++;
        }
        totalLine = line;
        return code;
    }

    public Block getBlockByLine(int line) {
        if (this.beginLine <= line && line < this.endLine) {
            return this;
        }
        return null;
    }

    final protected boolean isSelected() {
        return selected;
    }

    final protected boolean isError() {
        return error;
    }

    final protected boolean isHovered() {
        return hovered;
    }

    final public void select() {
        this.selected = true;
    }

    final public void unselect() {
        this.selected = false;
    }

    final public void hover() {
        this.hovered = true;
    }

    final public void unhover() {
        this.hovered = false;
    }

    final public void error() {
        this.error = true;
    }

    final public void unerror() {
        this.error = false;
    }
}

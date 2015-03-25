package drawer.entity;

import config.Config;
import java.awt.Graphics2D;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author munkhochir
 */
public class Arrow {

    private Block nextBlock;
    int height, x, y;

    public Arrow() {
        this.nextBlock = null;
    }

    public Arrow(Block nextBlock) {
        this.nextBlock = nextBlock;
    }

    public void setNextBlock(Block nextBlock) {
        this.nextBlock = nextBlock;
    }

    public Block getNextBlock() {
        return nextBlock;
    }

    public int draw(Graphics2D g, int x, int y, int h) {
        this.x = x;
        this.y = y;
        this.height = h;
        g.drawLine(x, y, x, y = y + h - 1);
        g.drawLine(x, y, x - Config.getArrowHead(), y - Config.getArrowHead());
        g.drawLine(x, y, x + Config.getArrowHead(), y - Config.getArrowHead());
        return h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public boolean isSelected(int x, int y) {//is only arrow selected
        if (this.x - Config.getArrowArea() / 2 < x
                && x < this.x + Config.getArrowArea() / 2
                && this.y < y
                && y < this.y + height) {
            return true;
        }
        return false;
    }
}

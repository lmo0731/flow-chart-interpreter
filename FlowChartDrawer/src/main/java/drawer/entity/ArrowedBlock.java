/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawer.entity;

import config.Config;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author munkhochir
 */
public abstract class ArrowedBlock extends Block {

    protected Arrow nextArrow;

    public ArrowedBlock() {
        super();
        this.nextArrow = new Arrow();
    }

    final public void setNextBlock(Block b) {
        this.nextArrow.setNextBlock(b);
    }

    final public Arrow getNextArrow() {
        return nextArrow;
    }

    final public Block getNextBlock() {
        return nextArrow.getNextBlock();
    }

    final public Arrow getLastArrow() {
        Arrow ret = nextArrow;
        if (ret.getNextBlock() != null
                && ret.getNextBlock().getClass().getSuperclass().equals(ArrowedBlock.class)) {
            ret = ((ArrowedBlock) ret.getNextBlock()).getLastArrow();
        }
        return ret;
    }

    @Override
    final public void setTotalColor(Color color) {
        this.pushColor(color);
        if (this.nextArrow.getNextBlock() != null) {
            this.nextArrow.getNextBlock().setTotalColor(color);
        }
    }

    @Override
    public void removeTotalColor() {
        super.popColor();
        if (this.nextArrow.getNextBlock() != null) {
            this.nextArrow.getNextBlock().removeTotalColor();
        }
    }

    @Override
    public int getHeight() {
        return this.height + Config.getArrowHeight();
    }

    @Override
    final public int getTotalHeight() {
        int ret = this.getHeight();
        if (nextArrow.getNextBlock() != null) {
            ret += nextArrow.getNextBlock().getTotalHeight();
        }
        return ret;
    }

    @Override
    final public int getTotalLeftWidth() {
        int ret = this.getLeftWidth();
        if (nextArrow.getNextBlock() != null) {
            ret = Math.max(ret, nextArrow.getNextBlock().getTotalLeftWidth());
        }
        return ret;
    }

    @Override
    final public int getTotalRightWidth() {
        int ret = this.getRightWidth();
        if (nextArrow.getNextBlock() != null) {
            ret = Math.max(ret, nextArrow.getNextBlock().getTotalRightWidth());
        }
        return ret;
    }

    @Override
    final public int draw(Graphics2D g, int x, int y, int remain) {
        Color c = g.getColor();
        remain = super.draw(g, x, y, remain + Config.getArrowHeight());
        this.setColors();
        g.setColor(this.peekColor());
        if (this.nextArrow.getNextBlock() != null) {
            remain -= this.nextArrow.draw(g, x, y + this.getHeight() - Config.getArrowHeight(), Config.getArrowHeight());
            remain -= this.nextArrow.getNextBlock().draw(g, x, y + this.getHeight(), remain);
        } else {
            remain -= this.nextArrow.draw(g, x, y + this.getHeight() - Config.getArrowHeight(), remain);
        }
        this.removeColors();
        g.setColor(c);
        return remain;
    }

    @Override
    public Arrow getBlock(int x, int y) {
        Arrow ret = super.getBlock(x, y);
        if (ret == null && nextArrow.getNextBlock() != null && nextArrow.getNextBlock().isSelected(x, y)) {
            ret = nextArrow;
        }
        if (ret == null && this.nextArrow.getNextBlock() != null) {
            ret = this.nextArrow.getNextBlock().getBlock(x, y);
        }
        return ret;
    }

    @Override
    public Arrow getArrow(int x, int y) {
        Arrow ret = super.getArrow(x, y);
        if (nextArrow != null && this.nextArrow.isSelected(x, y)) {
            ret = nextArrow;
        }
        if (ret == null && this.nextArrow.getNextBlock() != null) {
            ret = this.nextArrow.getNextBlock().getArrow(x, y);
        }
        return ret;
    }

    @Override
    public Block getBlockByLine(int line) {
        Block ret = super.getBlockByLine(line);
        if (ret == null && this.nextArrow.getNextBlock() != null) {
            ret = this.nextArrow.getNextBlock().getBlockByLine(line);
        }
        return ret;
    }
}

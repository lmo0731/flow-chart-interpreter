/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawer.entity.block;

import config.Config;
import drawer.entity.Arrow;
import drawer.entity.ArrowedBlock;
import drawer.entity.Block;
import interpreter.Language;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author munkhochir
 */
public class IfBlock extends ArrowedBlock {

    private Arrow leftArrow;
    private Arrow rightArrow;

    public IfBlock() {
        super();
        this.leftArrow = new Arrow();
        this.rightArrow = new Arrow();
    }

    final public Arrow getLeftArrow() {
        return leftArrow;
    }

    final public Arrow getRightArrow() {
        return rightArrow;
    }

    final public Block getLeftBlock() {
        return leftArrow.getNextBlock();
    }

    final public Block getRightBlock() {
        return rightArrow.getNextBlock();
    }

    final public void setLeftBlock(Block b) {
        leftArrow.setNextBlock(b);
    }

    final public void setRightBlock(Block b) {
        rightArrow.setNextBlock(b);
    }

    @Override
    final protected void initDraw() {
        height = Math.max(Config.getBlockHeight(), (int) ArrowedBlock.getTextBounds(this.getDisplayedText()).getHeight() * 2);
        width = Math.max(Config.getBlockWidth(), (int) ArrowedBlock.getTextBounds(this.getDisplayedText()).getWidth() * 2);
    }

    @Override
    final public void pushColor(Color color) {
        super.pushColor(color);
        if (leftArrow.getNextBlock() != null) {
            leftArrow.getNextBlock().setTotalColor(color);
        }
        if (rightArrow.getNextBlock() != null) {
            rightArrow.getNextBlock().setTotalColor(color);
        }
    }

    @Override
    final public void popColor() {
        super.popColor();
        if (leftArrow.getNextBlock() != null) {
            leftArrow.getNextBlock().removeTotalColor();
        }
        if (rightArrow.getNextBlock() != null) {
            rightArrow.getNextBlock().removeTotalColor();
        }
    }

    @Override
    final public int getHeight() {
        int right = height + Config.getArrowHeight();
        if (rightArrow.getNextBlock() != null) {
            right = Math.max(right, height / 2 + height / 4 + Config.getArrowHeight() + rightArrow.getNextBlock().getTotalHeight());
        }
        int left = height + Config.getArrowHeight();
        if (leftArrow.getNextBlock() != null) {
            left = Math.max(left, height / 2 + height / 4 + Config.getArrowHeight() + leftArrow.getNextBlock().getTotalHeight());
        }
        return Math.max(right, left) + Config.getArrowHeight();
    }

    @Override
    final public int getLeftWidth() {
        int ret = width / 2 + Config.getArrowWidth();
        if (leftArrow.getNextBlock() != null) {
            int k = Math.max(leftArrow.getNextBlock().getRightWidth(), Config.getArrowWidth() + (int) ((0.5) * width / 2)) + (int) ((0.5) * width / 2);
            k = Math.max(k, leftArrow.getNextBlock().getTotalRightWidth() + Config.getArrowWidth() / 2);
            ret = Math.max(ret, k + leftArrow.getNextBlock().getTotalLeftWidth());
        }
        return ret;
    }

    @Override
    final public int getRightWidth() {
        int ret = width / 2 + Config.getArrowWidth();
        if (rightArrow.getNextBlock() != null) {
            int k = Math.max(rightArrow.getNextBlock().getLeftWidth(), Config.getArrowWidth() + (int) ((0.5) * width / 2)) + (int) ((0.5) * width / 2);
            k = Math.max(k, rightArrow.getNextBlock().getTotalLeftWidth() + Config.getArrowWidth() / 2);
            ret = Math.max(ret, k + rightArrow.getNextBlock().getTotalRightWidth());
        }
        return ret;
    }

    @Override
    final public void drawOther(Graphics2D g, int x, int y) {
        super.drawOther(g, x, y);
        int xr = x + this.getRightWidth();
        int hr = this.getHeight() - height / 2 - Config.getArrowHeight();
        if (this.rightArrow.getNextBlock() != null) {
            xr -= rightArrow.getNextBlock().getTotalRightWidth();
            int ha = Config.getArrowHeight() + height / 4;
            hr -= this.rightArrow.draw(g, xr, y + height / 2, ha);
            this.rightArrow.getNextBlock().draw(g, xr, y + height / 2 + ha, hr);
        } else {
            hr -= this.rightArrow.draw(g, xr, y + height / 2, hr);
        }
        Block.drawText(g, "-", (x + width / 2) + Config.getArrowWidth() / 2, y + height / 4);
        g.drawLine(x + width / 2, y + height / 2, xr, y + height / 2);
        int xl = x - this.getLeftWidth();
        int hl = this.getHeight() - height / 2 - Config.getArrowHeight();
        if (this.leftArrow.getNextBlock() != null) {
            xl += leftArrow.getNextBlock().getTotalLeftWidth();
            int ha = Config.getArrowHeight() + height / 4;
            hl -= this.leftArrow.draw(g, xl, y + height / 2, ha);
            this.leftArrow.getNextBlock().draw(g, xl, y + height / 2 + ha, hl);
        } else {
            hl -= this.leftArrow.draw(g, xl, y + height / 2, hl);
        }
        Block.drawText(g, "+", (x - width / 2) - Config.getArrowWidth() / 2, y + height / 4);
        g.drawLine(x - width / 2, y + height / 2, xl, y + height / 2);
        int h = this.getHeight() - Config.getArrowHeight();
        g.drawLine(xl, y + h, xr, y + h);
    }

    @Override
    final public Arrow getBlock(int x, int y) {//get selected block's parent arrow
        Arrow ret = super.getBlock(x, y);
        if (ret == null && leftArrow.getNextBlock() != null && leftArrow.getNextBlock().isSelected(x, y)) {
            ret = leftArrow;
        }
        if (ret == null && rightArrow.getNextBlock() != null && rightArrow.getNextBlock().isSelected(x, y)) {
            ret = rightArrow;
        }
        if (ret == null && this.leftArrow.getNextBlock() != null) {
            ret = this.leftArrow.getNextBlock().getBlock(x, y);
        }
        if (ret == null && this.rightArrow.getNextBlock() != null) {
            ret = this.rightArrow.getNextBlock().getBlock(x, y);
        }
        return ret;
    }

    @Override
    final public Arrow getArrow(int x, int y) {
        Arrow ret = super.getArrow(x, y);
        if (leftArrow != null && leftArrow.isSelected(x, y)) {
            ret = leftArrow;
        }
        if (rightArrow != null && rightArrow.isSelected(x, y)) {
            ret = rightArrow;
        }
        if (ret == null && this.leftArrow.getNextBlock() != null) {
            ret = this.leftArrow.getNextBlock().getArrow(x, y);
        }
        if (ret == null && this.rightArrow.getNextBlock() != null) {
            ret = this.rightArrow.getNextBlock().getArrow(x, y);
        }
        return ret;
    }

    @Override
    final public String convert(String code, int line, boolean debug) {
        code = super.convert(code, line, debug);
        line = this.totalLine;
        code += Language.ifKeys().get(0) + " ( " + this.getText().replaceAll("[\\n]", " ") + " ) " + Language.beginKeys().get(0) + " \n";
        line++;
        this.endLine = line;
        if (leftArrow.getNextBlock() != null) {
            code = leftArrow.getNextBlock().convert(code, line, debug);
            line = leftArrow.getNextBlock().getTotalLine();
        }
        code += Language.endKeys().get(0) + " " + Language.elseKeys().get(0) + " " + Language.beginKeys().get(0) + "\n";
        line++;
        if (rightArrow.getNextBlock() != null) {
            code = rightArrow.getNextBlock().convert(code, line, debug);
            line = rightArrow.getNextBlock().getTotalLine();
        }
        code += Language.endKeys().get(0) + "\n";
        line++;
        if (this.nextArrow.getNextBlock() != null) {
            code = this.nextArrow.getNextBlock().convert(code, line, debug);
            line = this.getNextBlock().getTotalLine();
        }
        this.totalLine = line;
        return code;
    }

    @Override
    final public String checker() {
        String code = Language.functionKeys().get(0) + " () " + Language.beginKeys().get(0) + "\n";
        code += Language.ifKeys().get(0) + " ( " + this.getText().replaceAll("[\\n]", " ") + " ) " + Language.beginKeys().get(0) + "\n";
        code += Language.endKeys().get(0) + "\n";
        code += Language.endKeys().get(0);
        return code;
    }

    @Override
    final public Block getBlockByLine(int line) {
        Block ret = null;
        if (ret == null && this.leftArrow.getNextBlock() != null) {
            ret = this.leftArrow.getNextBlock().getBlockByLine(line);
        }
        if (ret == null && this.rightArrow.getNextBlock() != null) {
            ret = this.rightArrow.getNextBlock().getBlockByLine(line);
        }
        if (ret == null) {
            ret = super.getBlockByLine(line);
        }
        return ret;
    }

    @Override
    final public void removeTotalColor() {
        super.removeTotalColor();
        if (this.leftArrow.getNextBlock() != null) {
            this.leftArrow.getNextBlock().removeTotalColor();
        }
        if (this.rightArrow.getNextBlock() != null) {
            this.rightArrow.getNextBlock().removeTotalColor();
        }
    }

    @Override
    protected void drawBlockBorder(Graphics2D g, int x, int y) {
        g.drawPolygon(
                new int[]{x, x + width / 2, x, x - width / 2},
                new int[]{y, y + height / 2, y + height - 1, y + height / 2},
                4);
    }

    @Override
    protected void drawBlockBackground(Graphics2D g, int x, int y) {
        g.fillPolygon(
                new int[]{x, x + width / 2, x, x - width / 2},
                new int[]{y, y + height / 2, y + height - 1, y + height / 2},
                4);
    }
}

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
public class LoopBlock extends ArrowedBlock {

    Arrow initArrow;
    Arrow backArrow;

    public LoopBlock() {
        super();
        this.backArrow = new Arrow();
        this.initArrow = new Arrow();
    }

    final public Arrow getBackArrow() {
        return backArrow;
    }

    final public Arrow getInitArrow() {
        return initArrow;
    }

    final public Block getBackBlock() {
        return backArrow.getNextBlock();
    }

    final public Block getInitBlock() {
        return initArrow.getNextBlock();
    }

    final public void setInitBlock(Block initBlock) {
        this.initArrow.setNextBlock(initBlock);
    }

    final public void setBackBlock(Block backBlock) {
        this.backArrow.setNextBlock(backBlock);
    }

    @Override
    protected void initDraw() {
        height = Math.max(Config.getBlockHeight(), (int) ArrowedBlock.getTextBounds(this.getDisplayedText()).getHeight() * 2);
        width = Math.max(Config.getBlockWidth(), (int) ArrowedBlock.getTextBounds(this.getDisplayedText()).getWidth() * 2);
    }

    @Override
    final public void pushColor(Color color) {
        super.pushColor(color);
        if (initArrow.getNextBlock() != null) {
            initArrow.getNextBlock().setTotalColor(color);
        }
        if (backArrow.getNextBlock() != null) {
            backArrow.getNextBlock().setTotalColor(color);
        }
    }

    @Override
    final public void popColor() {
        super.popColor();
        if (initArrow.getNextBlock() != null) {
            initArrow.getNextBlock().removeTotalColor();
        }
        if (backArrow.getNextBlock() != null) {
            backArrow.getNextBlock().removeTotalColor();
        }
    }

    @Override
    final public int getHeight() {
        int ret = 4 * Config.getArrowHeight() + height;
        if (initArrow.getNextBlock() != null) {
            ret += initArrow.getNextBlock().getTotalHeight();
        }
        if (backArrow.getNextBlock() != null) {
            ret += backArrow.getNextBlock().getTotalHeight();
        }
        return ret;
    }

    @Override
    public int getRightWidth() {
        int ret = Config.getArrowWidth() + width / 2;
        if (backArrow.getNextBlock() != null) {
            ret = Math.max(ret, Config.getArrowWidth() + backArrow.getNextBlock().getTotalRightWidth());
        }
        if (initArrow.getNextBlock() != null) {
            ret = Math.max(ret, Config.getArrowWidth() + initArrow.getNextBlock().getTotalRightWidth());
        }
        return ret;
    }

    @Override
    public int getLeftWidth() {
        int ret = Config.getArrowWidth() + width / 2;
        if (backArrow.getNextBlock() != null) {
            ret = Math.max(ret, Config.getArrowWidth() + backArrow.getNextBlock().getTotalLeftWidth());
        }
        if (initArrow.getNextBlock() != null) {
            ret = Math.max(ret, initArrow.getNextBlock().getTotalLeftWidth());
        }
        return ret;
    }

    @Override
    public int getBlockOffset() {
        int ret = Config.getArrowHeight();
        if (initArrow.getNextBlock() != null) {
            ret += initArrow.getNextBlock().getTotalHeight();
        }
        return ret;
    }

    @Override
    public void drawOther(Graphics2D g, int x, int y) {
        super.drawOther(g, x, y);
        int y1 = y + initArrow.draw(g, x, y + 1, Config.getArrowHeight());
        int right = width / 2 + Config.getArrowWidth(), left = width / 2 + Config.getArrowWidth();
        if (initArrow.getNextBlock() != null) {
            this.initArrow.getNextBlock().draw(g, x, y1, this.initArrow.getNextBlock().getTotalHeight());
            right = Math.max(right, initArrow.getNextBlock().getTotalRightWidth() + Config.getArrowWidth());
            y1 += initArrow.getNextBlock().getTotalHeight();
        }
        Block.drawText(g, "+", x - width / 2 - Config.getArrowWidth() / 2, y1 + height / 4);
        int y2 = y1 + height;
        Block.drawText(g, "-", x + Config.getArrowWidth() / 2, y2 + Config.getArrowHeight() / 2);
        y2 += backArrow.draw(g, x, y2, Config.getArrowHeight());
        if (backArrow.getNextBlock() != null) {
            this.backArrow.getNextBlock().draw(g, x, y2, this.backArrow.getNextBlock().getTotalHeight());
            right = Math.max(right, backArrow.getNextBlock().getTotalRightWidth() + Config.getArrowWidth());
            left = Math.max(left, backArrow.getNextBlock().getTotalLeftWidth() + Config.getArrowWidth());
            y2 += backArrow.getNextBlock().getTotalHeight();
        }
        g.drawLine(x, y2, x + right, y2);
        g.drawLine(x + right, y + 1, x + right, y2);
        g.drawLine(x + right, y + 1, x, y + 1);

        g.drawLine(x - width / 2, y1 + height / 2, x - left, y1 + height / 2);
        g.drawLine(x - left, y2 + Config.getArrowHeight(), x - left, y1 + height / 2);
        g.drawLine(x - left, y2 + Config.getArrowHeight(), x, y2 + Config.getArrowHeight());
    }

    @Override
    public Arrow getBlock(int x, int y) {
        Arrow ret = super.getBlock(x, y);
        if (ret == null && initArrow.getNextBlock() != null && initArrow.getNextBlock().isSelected(x, y)) {
            ret = initArrow;
        }
        if (ret == null && backArrow.getNextBlock() != null && backArrow.getNextBlock().isSelected(x, y)) {
            ret = backArrow;
        }
        if (ret == null && this.initArrow.getNextBlock() != null) {
            ret = this.initArrow.getNextBlock().getBlock(x, y);
        }
        if (ret == null && this.backArrow.getNextBlock() != null) {
            ret = this.backArrow.getNextBlock().getBlock(x, y);
        }
        return ret;
    }

    @Override
    public Arrow getArrow(int x, int y) {
        Arrow ret = super.getArrow(x, y);
        if (initArrow != null && initArrow.isSelected(x, y)) {
            ret = initArrow;
        }
        if (backArrow != null && backArrow.isSelected(x, y)) {
            ret = backArrow;
        }
        if (ret == null && this.initArrow.getNextBlock() != null) {
            ret = this.initArrow.getNextBlock().getArrow(x, y);
        }
        if (ret == null && this.backArrow.getNextBlock() != null) {
            ret = this.backArrow.getNextBlock().getArrow(x, y);
        }
        return ret;
    }

    @Override
    final public String convert(String code, int line, boolean debug) {
        code += Language.whileKeys().get(0) + " ( " + Language.trueKeys().get(0) + " ) " + Language.beginKeys().get(0) + " \n";
        line++;
        if (initArrow.getNextBlock() != null) {
            code = initArrow.getNextBlock().convert(code, line, debug);
            line = initArrow.getNextBlock().getTotalLine();
        }
        code = super.convert(code, line, debug);
        line = this.totalLine;
        code += Language.ifKeys().get(0) + " ( " + this.getText().replaceAll("[\\n]", " ") + " ) "
                + Language.beginKeys().get(0) + " " + Language.breakKeys().get(0) + Language.seperator().get(0) + " " + Language.endKeys().get(0) + "\n";
        line++;
        this.endLine = line;
        if (backArrow.getNextBlock() != null) {
            code = backArrow.getNextBlock().convert(code, line, debug);
            line = backArrow.getNextBlock().getTotalLine();
        }
        code += Language.endKeys().get(0) + "\n";
        line++;
        if (this.getNextBlock() != null) {
            code = this.getNextBlock().convert(code, line, debug);
            line = this.getNextBlock().getTotalLine();
        }
        this.totalLine = line;
        return code;
    }

    @Override
    public String checker() {
        String code = Language.functionKeys().get(0) + " () " + Language.beginKeys().get(0) + "\n";
        code += Language.ifKeys().get(0) + " ( " + this.getText().replaceAll("[\\n]", " ") + " ) " + Language.beginKeys().get(0) + "\n";
        code += Language.endKeys().get(0) + "\n";
        code += Language.endKeys().get(0);
        return code;
    }

    @Override
    public Block getBlockByLine(int line) {
        Block ret = null;
        if (ret == null && this.initArrow.getNextBlock() != null) {
            ret = this.initArrow.getNextBlock().getBlockByLine(line);
        }
        if (ret == null && this.backArrow.getNextBlock() != null) {
            ret = this.backArrow.getNextBlock().getBlockByLine(line);
        }
        if (ret == null) {
            ret = super.getBlockByLine(line);
        }
        return ret;
    }

    @Override
    public void removeTotalColor() {
        super.removeTotalColor();
        if (this.initArrow.getNextBlock() != null) {
            this.initArrow.getNextBlock().removeTotalColor();
        }
        if (this.backArrow.getNextBlock() != null) {
            this.backArrow.getNextBlock().removeTotalColor();
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

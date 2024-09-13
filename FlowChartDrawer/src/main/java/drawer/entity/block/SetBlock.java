/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawer.entity.block;

import config.Config;
import drawer.entity.ArrowedBlock;
import drawer.entity.Block;
import interpreter.Language;
import java.awt.Graphics2D;

/**
 *
 * @author munkhochir
 */
public class SetBlock extends ArrowedBlock {

    @Override
    public String convert(String code, int line, boolean debug) {
        code = super.convert(code, line, debug);
        line = this.totalLine;
        code += this.getText().replaceAll("[\\n]", Language.seperator().get(0) + " ") + Language.seperator().get(0) + "\n";
        line++;
        this.endLine = line;
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
        code += this.getText().replaceAll("[\\n]", Language.seperator().get(0) + " ") + "\n";
        code += Language.endKeys().get(0);
        return code;
    }

    @Override
    protected void initDraw() {
        this.height = (int) Math.max(Config.getBlockHeight(), Block.getTextBounds(this.getDisplayedText()).getHeight() + 2 * Config.getBlockVerticalPadding());
        this.width = (int) Math.max(Config.getBlockWidth(), Block.getTextBounds(this.getDisplayedText()).getWidth() + 2 * Config.getBlockHorizontalPadding());
    }

    @Override
    protected void drawBlockBorder(Graphics2D g, int x, int y) {
        g.drawRect(x - width / 2, y, width - 1, height - 1);
    }

    @Override
    protected void drawBlockBackground(Graphics2D g, int x, int y) {
        g.fillRect(x - width / 2, y, width - 1, height - 1);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawer.entity.block;

import config.Config;
import drawer.entity.Block;
import interpreter.Language;
import java.awt.Graphics2D;

/**
 *
 * @author munkhochir
 */
public class EndBlock extends Block {

    @Override
    final public String convert(String code, int line, boolean debug) {
        this.beginLine = line;
        code += Language.endKeys().get(0) + " " + this.getText().replaceAll("[\\n]", Language.seperator().get(0) + " ") + "\n";
        line++;
        this.endLine = line;
        this.totalLine = line;
        return code;
    }

    @Override
    final public String checker() {
        String code = Language.functionKeys().get(0) + " ( ) " + Language.beginKeys().get(0) + " " + Language.endKeys().get(0) + " " + this.getText().replaceAll("[\\n]", "; ") + "\n";
        return code;
    }

    @Override
    final public String getDisplayedText() {
        return super.getDisplayedText() + "\n" + "Tөгсгөл";
    }

    @Override
    final protected void drawBlockBorder(Graphics2D g, int x, int y) {
        g.drawRoundRect(x - width / 2, y, width - 1, height - 1, height, height);
    }

    @Override
    protected void drawBlockBackground(Graphics2D g, int x, int y) {
        g.fillRoundRect(x - width / 2, y, width - 1, height - 1, height, height);
    }

    @Override
    protected void initDraw() {
        this.height = (int) Math.max(Config.getBlockHeight(), Block.getTextBounds(this.getDisplayedText()).getHeight() + 2 * Config.getBlockVerticalPadding());
        this.width = (int) Math.max(Config.getBlockWidth(), Block.getTextBounds(this.getDisplayedText()).getWidth() + 2 * Config.getBlockHorizontalPadding());
    }
}

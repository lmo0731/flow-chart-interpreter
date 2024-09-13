/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawer.entity.block;

import config.Config;
import drawer.entity.ArrowedBlock;
import interpreter.Language;
import java.awt.Graphics2D;

/**
 *
 * @author munkhochir
 */
public class ReadBlock extends ArrowedBlock {

    public static double DEG = 20;

    @Override
    protected void initDraw() {
        height = Math.max(Config.getBlockHeight(), (int) PrintBlock.getTextBounds(this.getText()).getHeight() + 2 * Config.getBlockVerticalPadding());
        int TILT = (int) (height * Math.sin(Math.PI * DEG / 180));
        width = Math.max(Config.getBlockWidth(), (int) PrintBlock.getTextBounds(this.getText()).getWidth() + 2 * TILT);
    }

    @Override
    public String convert(String code, int line, boolean debug) {
        code = super.convert(code, line, debug);
        line = this.totalLine;
        code += Language.readKeys().get(0) + " " + this.getText().replaceAll("[\\n]", Language.seperator().get(0) + " ") + Language.seperator().get(0) + "\n";
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
        code += Language.readKeys().get(0) + " " + this.getText().replaceAll("[\\n]", Language.seperator().get(0) + " ") + "\n";
        code += Language.endKeys().get(0);
        return code;
    }

    @Override
    protected void drawBlockBorder(Graphics2D g, int x, int y) {
        int TILT = (int) (height * Math.sin(Math.PI * DEG / 180));
        g.drawPolygon(
                new int[]{x - width / 2 + TILT, x + width / 2 - 1, x + width / 2 - TILT, x - width / 2},
                new int[]{y, y, y + height - 1, y + height - 1},
                4);
    }

    @Override
    protected void drawBlockBackground(Graphics2D g, int x, int y) {
        int TILT = (int) (height * Math.sin(Math.PI * DEG / 180));
        g.fillPolygon(
                new int[]{x - width / 2 + TILT, x + width / 2 - 1, x + width / 2 - TILT, x - width / 2},
                new int[]{y, y, y + height - 1, y + height - 1},
                4);
    }

    @Override
    protected String getDisplayedText() {
        return "унших\n" + super.getDisplayedText();
    }
}

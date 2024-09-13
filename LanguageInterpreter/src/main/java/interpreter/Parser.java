/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

import interpreter.exceptions.ParseException;
import interpreter.tree.*;
import java.util.Vector;
import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author LMO
 */
public class Parser {

    public static Vector<String> BRACKETS = new Vector<String>(Arrays.asList(new String[]{"(", ")"}));
    public static Vector<String> UNARY_OPERATORS = new Vector<String>(Arrays.asList(new String[]{"-", Language.notKeys().get(0)}));
    Vector<Vector<String>> lineList = new Vector<Vector<String>>();
    Vector<String> wordList = new Vector<String>();
    String prevWord = "";
    String nextWord = "";
    String name = "";
    int line = 0;

    public Parser() {
    }

    private void error(int id) throws ParseException {
        String message = this.prevWord + " " + this.nextWord;
        throw new ParseException(name, line, message, id);
    }

    private void getNextWord() {
        this.prevWord = this.nextWord;
        while (this.wordList.isEmpty()) {
            this.line += 1;
            if (this.lineList.isEmpty()) {
                this.nextWord = "";
                return;
            } else {
                this.wordList = this.lineList.get(0);
                this.lineList.remove(0);
            }
        }
        this.nextWord = this.wordList.get(0);
        this.wordList.remove(0);
    }

    public FTree parse(String name, String code) throws ParseException {
        this.lineList = new Vector<Vector<String>>();
        this.wordList = new Vector<String>();
        this.prevWord = "";
        this.nextWord = "";
        this.line = 0;
        this.name = name;
        Vector<Vector<String>> lines = new Scanner().scan(this.name, code);
        lines.add(new Vector<String>());
        this.lineList = lines;
        this.getNextWord();
        FTree ftree = this.parseF();
        return ftree;
    }

    private FTree parseF() throws ParseException {
        if (!Language.functionKeys().contains(this.nextWord.toLowerCase())) {
            this.error(ParseException.ERROR_INVALID_FUNCTION_START);
        }
        this.getNextWord();
        Integer start = line;
        Vector<LTreeV> ptree = this.parseP();
        BTree btree = this.parseB();
        VTree ret = new VTreeV(new Long(0), name, start);
        if (!this.nextWord.equals("")) {
            ret = this.parseE(false);
        }
        return new FTree(ptree, btree, ret, name, start);
    }

    private BTree parseB() throws ParseException {
        Integer start = line;
        if (Language.beginKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
        } else {
            this.error(ParseException.ERROR_MISSING_BEGIN_KEYWORD);
        }
        Vector<CTree> ctrees = new Vector<CTree>();
        while (!Language.endKeys().contains(this.nextWord.toLowerCase())) {
            CTree ctree = this.parseC();
            if (ctree != null) {
                ctrees.add(ctree);
            }
            if (Language.seperator().contains(this.nextWord.toLowerCase())) {
                this.getNextWord();
            } else {
                //if (!Language.endKeys().contains(this.prevWord.toLowerCase())) {
                //    this.error(ParseException.ERROR_MISSING_SEPARATOR);
                //}
            }
        }
        if (Language.endKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
        } else {
            this.error(ParseException.ERROR_MISSING_END_KEYWORD);
        }
        return new BTree(ctrees, name, start);
    }

    private CTree parseC() throws ParseException {
        Integer start = line;
        if (Language.ifKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
            VTree etree = this.parseE(false);
            BTree b1tree = this.parseB();
            BTree b2tree = new BTree(new Vector<CTree>(), name, start);
            if (Language.elseKeys().contains(this.nextWord.toLowerCase())) {
                this.getNextWord();
                b2tree = this.parseB();
            }
            return new CTreeI(etree, b1tree, b2tree, name, start);
        } else if (Language.breakKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
            return new CTreeB(name, start);
        } else if (Language.whileKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
            VTree vtree = this.parseE(false);
            BTree btree = this.parseB();
            return new CTreeW(vtree, btree, name, start);
        } else if (Language.printKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
            VTree etree = this.parseE(false);
            return new CTreeP(etree, name, start);
        } else if (Language.readKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
            LTree ltree = this.parseL();
            return new CTreeR(ltree, name, start);
        } else if (Language.beginKeys().contains(this.nextWord.toLowerCase())) {
            return this.parseB();
        } else if (Language.isVar(this.nextWord)) {
            LTree ltree = this.parseL();
            if (this.nextWord.equals("=")) {
                this.getNextWord();
                if (Language.arrayKeys().contains(nextWord.toLowerCase())) {
                    return new CTreeD(ltree, this.parseD(), name, start);
                } else {
                    return new CTreeS(ltree, this.parseE(false), name, start);
                }
            } else if (this.nextWord.equals("(")) {
                return new CTreeF(this.prevWord, this.parseA(), name, start);
            } else {
                this.error(ParseException.ERROR_MISSING_SET_OPERATOR);
            }
        } else if (Language.seperator().contains(this.nextWord.toLowerCase())) {
            return null;
        } else if (Language.isStr(this.nextWord)) {
            this.parseV();
        } else {
            this.error(ParseException.ERROR_INVALID_COMMAND);
        }
        return null;
    }

    private DTree parseD() throws ParseException {
        Integer start = line;
        if (Language.arrayKeys().contains(this.nextWord.toLowerCase())) {
            this.getNextWord();
            Vector<VTree> itree = this.parseI();
            return new DTreeA(itree, name, start);
        } else {
            this.error(ParseException.ERROR_INVALID_DECLARATION);
        }
        return null;
    }

    private Vector<VTree> parseI() throws ParseException {
        Vector<VTree> itree = new Vector<VTree>();
        while (this.nextWord.equals("[")) {
            if (this.nextWord.equals("[")) {
                this.getNextWord();
            } else {
                this.error(ParseException.ERROR_MISSING_LEFT_SQUARE_BRACKET);
            }
            VTree etree = this.parseE(false);
            if (this.nextWord.equals("]")) {
                this.getNextWord();
            } else {
                this.error(ParseException.ERROR_MISSING_RIGHT_SQUARE_BRACKET);
            }
            itree.add(etree);
        }
        return itree;
    }

    private LTree parseL() throws ParseException {
        Integer start = line;
        String var = "";
        if (Language.isVar(this.nextWord)) {
            var = this.nextWord;
            this.getNextWord();
        } else {
            this.error(ParseException.ERROR_INVALID_VARIABLE_NAME);
        }
        if (this.nextWord.equals("[")) {
            return new LTreeI(var, this.parseI(), name, start);
        }
        return new LTreeV(var, name, start);
    }

    private VTree parseE(boolean bracket) throws ParseException {
        Integer start = line;
        Vector<Object> polish = new Vector<Object>();
        {
            Stack<String> stack = new Stack<String>();
            while (false
                    || Language.isBool(this.nextWord)
                    || Language.isStr(this.nextWord)
                    || Language.isVar(this.nextWord)
                    || Language.isNum(this.nextWord)
                    || Language.OPERATORS.containsKey(this.nextWord)
                    || this.nextWord.equals("(")) {
                if (false
                        || Language.isBool(this.nextWord)
                        || Language.isStr(this.nextWord)
                        || Language.isVar(this.nextWord)
                        || Language.isNum(this.nextWord)) {
                    if (Language.isVar(this.prevWord)) {
                        this.error(ParseException.ERROR_MISSING_OPERATOR);
                    }
                    polish.add(this.parseV());
                } else if (Language.OPERATORS.containsKey(this.nextWord)) {
                    if (UNARY_OPERATORS.contains(this.nextWord) && Language.OPERATORS.containsKey(this.prevWord)) {
                        polish.add(new VTreeV(new Double(0.0), name, line));
                        stack.add(this.nextWord);
                        this.getNextWord();
                    } else {
                        while (!stack.isEmpty()
                                && Language.OPERATORS.containsKey(stack.lastElement())
                                && Language.OPERATORS.get(stack.lastElement()) >= Language.OPERATORS.get(this.nextWord)) {
                            polish.add(stack.pop());
                        }
                        stack.add(this.nextWord);
                        this.getNextWord();
                    }
                } else if (this.nextWord.equals("(")) {
                    this.getNextWord();
                    polish.add(this.parseE(true));
                } else {
                    this.error(ParseException.ERROR_ILLEGAL_EXPRESSION);
                }
            }
            while (!stack.isEmpty()) {
                polish.add(stack.pop());
            }
            if (bracket) {
                if (this.nextWord.equals(")")) {
                    this.getNextWord();
                } else {
                    this.error(ParseException.ERROR_MISSING_RIGHT_BRACKET);
                }
            }
        }
        Stack<VTree> stack = new Stack<VTree>();
        for (Object e : polish) {
            if (e.getClass().equals(String.class) && Language.OPERATORS.containsKey((String) e)) {
                if (this.UNARY_OPERATORS.contains(e) && stack.size() < 2) {
                    stack.add(0, new VTreeV(new Long(0), name, start));
                }
                if (stack.size() < 2) {
                    this.error(ParseException.ERROR_ILLEGAL_EXPRESSION);
                }
                VTree v2tree = stack.pop();
                VTree v1tree = stack.pop();
                stack.add(new ETree((String) e, v1tree, v2tree, name, start));
            } else if (e.getClass().getSuperclass().equals(VTree.class)) {
                stack.add((VTree) e);
            } else {
                this.error(ParseException.ERROR_DEVELOPER_ON_POLISH);
            }
        }
        if (stack.size() != 1) {
            this.error(ParseException.ERROR_ILLEGAL_EXPRESSION);
        }
        return stack.firstElement();
    }

    private VTree parseV() throws ParseException {
        Integer start = line;
        this.getNextWord();
        if (Language.isNum(this.prevWord)) {
            try {
                if (Language.isInt(this.prevWord)) {
                    return new VTreeV(new Long(this.prevWord), name, start);
                } else {
                    return new VTreeV(new Double(this.prevWord), name, start);
                }
            } catch (NumberFormatException ex) {
                this.error(ParseException.ERROR_NUMBER_TOO_LARGE);
            }
        } else if (Language.isStr(this.prevWord)) {
            return new VTreeV(this.prevWord.substring(1, this.prevWord.length() - 1), name, start);
        } else if (Language.isBool(this.prevWord)) {
            if (Language.trueKeys().contains(this.prevWord.toLowerCase())) {
                return new VTreeV(true, name, start);
            } else {
                return new VTreeV(false, name, start);
            }
        } else if (Language.isVar(this.prevWord)) {
            if (this.nextWord.equals("(")) {
                return new VTreeF(this.prevWord, this.parseA(), name, start);
            } else if (this.nextWord.equals("[")) {
                Integer start1 = line;
                return new VTreeL(new LTreeI(this.prevWord, this.parseI(), name, start1), name, start);
            } else {
                return new VTreeL(new LTreeV(this.prevWord, name, start), name, start);
            }
        } else {
            this.error(ParseException.ERROR_INVALID_VALUE);
        }
        return null;
    }

    private Vector<VTree> parseA() throws ParseException {
        Vector<VTree> args = new Vector<VTree>();
        if (!this.nextWord.equals("(")) {
            this.error(ParseException.ERROR_MISSING_LEFT_BRACKET);
        }
        String nextChar = "(";
        while (this.nextWord.equals(nextChar)) {
            nextChar = ",";
            this.getNextWord();
            if (!this.nextWord.equals(")")) {
                args.add(this.parseE(false));
            }
        }
        if (!this.nextWord.equals(")")) {
            this.error(ParseException.ERROR_MISSING_RIGHT_BRACKET);
        }
        this.getNextWord();
        return args;
    }

    private Vector<LTreeV> parseP() throws ParseException {
        int start = line;
        Vector<LTreeV> parms = new Vector<LTreeV>();
        if (!this.nextWord.equals("(")) {
            this.error(ParseException.ERROR_MISSING_LEFT_BRACKET);
        }
        String nextChar = "(";
        while (this.nextWord.equals(nextChar)) {
            nextChar = ",";
            this.getNextWord();
            if (!this.nextWord.equals(")")) {
                LTree ltree = this.parseL();
                if (ltree.getClass().equals(LTreeV.class)) {
                    parms.add((LTreeV) ltree);
                } else {
                    this.error(ParseException.ERROR_INVALID_PARAMETER_TYPE);
                }
            }
        }
        if (!this.nextWord.equals(")")) {
            this.error(ParseException.ERROR_MISSING_RIGHT_BRACKET);
        }
        this.getNextWord();
        return parms;
    }
}

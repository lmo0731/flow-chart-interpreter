/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.InterpretException;
import interpreter.memory.Location;
import interpreter.memory.Memory;
import interpreter.memory.Pointer;
import interpreter.tools.Input;
import interpreter.tools.Output;
import interpreter.tools.Reader;
import interpreter.tree.BTree;
import interpreter.tree.CTree;
import interpreter.tree.CTreeB;
import interpreter.tree.CTreeD;
import interpreter.tree.CTreeF;
import interpreter.tree.CTreeI;
import interpreter.tree.CTreeP;
import interpreter.tree.CTreeR;
import interpreter.tree.CTreeS;
import interpreter.tree.CTreeW;
import interpreter.tree.DTree;
import interpreter.tree.DTreeA;
import interpreter.tree.ETree;
import interpreter.tree.FTree;
import interpreter.tree.LTree;
import interpreter.tree.LTreeI;
import interpreter.tree.LTreeV;
import interpreter.tree.VTree;
import interpreter.tree.VTreeF;
import interpreter.tree.VTreeL;
import interpreter.tree.VTreeV;

/**
 *
 * @author LMO
 */
public class Interpreter implements Runnable {

    public static long MAX_LEVEL = 3000;
    private int level = 0;
    private Thread thread;
    private final Object lock = new Object();
    private boolean debug = false;
    private Input input;
    private Output output;
    private Reader reader;
    private String name;
    private int line;
    private Vector<Object> args;
    private Memory memory;
    private boolean finished = false;
    private Object returnValue = null;
    private boolean subInterpret = false;
    private Interpreter subInterpreter;
    private boolean error = false;
    private Exception exception;
    private HashMap<String, FTree> funcs;

    public void checkError() throws Exception {
        synchronized (lock) {
            if (error) {
                throw this.exception;
            }
        }
    }

    public void error(String message) throws InterpretException {
        throw new InterpretException(name, line, message);
    }

    public Interpreter(Reader reader, Input input, Output output) {
        this(reader, input, output, new HashMap<String, FTree>(), 0);
    }

    public Interpreter(Reader reader, Input input, Output output, HashMap<String, FTree> funcs, int level) {
        this.level = level;
        this.reader = reader;
        this.input = input;
        this.output = output;
        this.funcs = funcs;
        thread = new Thread(this, "interpreter");
    }

    public int getLevel() {
        if (this.subInterpret) {
            return this.subInterpreter.getLevel();
        }
        return this.level;
    }

    public String getInfo() {
        return this.getName() + "[" + this.getLevel() + "]" + "@" + this.getLine();
    }

    public int getLine() {
        synchronized (lock) {
            if (this.subInterpret) {
                return this.subInterpreter.getLine();
            }
            return line;
        }
    }

    public String getName() {
        synchronized (lock) {
            if (this.subInterpret) {
                return this.subInterpreter.getName();
            }
            return name;
        }
    }

    public Memory getMemory() {
        synchronized (lock) {
            if (this.subInterpret) {
                return this.subInterpreter.getMemory();
            }
            return this.memory;
        }
    }

    public void next() {
        synchronized (lock) {
            if (this.subInterpret) {
                this.subInterpreter.next();
            } else {
                lock.notify();
            }
        }
    }

    public void stop() {
        synchronized (lock) {
            if (this.subInterpret) {
                this.subInterpreter.stop();
            } else {
                thread.stop();
            }
        }
    }

    public boolean isFinished() {
        synchronized (lock) {
            return finished;
        }
    }

    public Object getReturnValue() {
        return returnValue;
    }

    @Override
    public void run() {
        try {
            FTree ftree = null;
            if (this.funcs.containsKey(name)) {
                ftree = this.funcs.get(name);
            } else {
                ftree = new Parser().parse(name, reader.readCode(name));
                this.funcs.put(name, ftree);
            }
            interpretF(ftree, args);
            finished = true;
        } catch (Exception ex) {
            this.error = true;
            this.exception = ex;
        }
    }

    private void debug() {
        if (debug) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void interpret(String name, Vector<Object> args) {
        this.interpret(name, args, false);
    }

    public void interpret(String name, Vector<Object> args, boolean debug) {
        this.finished = false;
        this.error = false;
        this.name = name;
        this.args = args;
        this.debug = debug;
        this.memory = new Memory();
        this.thread.start();

    }

    private void interpretF(FTree ftree, Vector<Object> args) throws Exception {
        try {
            this.interpretP(ftree.getPtree(), args);
            this.interpretB(ftree.getBtree(), false);
            returnValue = this.interpretV(ftree.getVtree());
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void interpretB(BTree btree, boolean block) throws Exception {
        if (block) {
            debug();
        }
        for (CTree ctree : btree.getCtrees()) {
            this.interpretC(ctree);
        }
    }

    private void interpretC(CTree ctree) throws Exception {
        this.line = ctree.getLine();
        if (ctree.getClass().equals(CTreeD.class)) {
            CTreeD c = (CTreeD) ctree;
            this.memory.setData(this.interpretL(c.getLtree()), this.interpretD(c.getDtree()));
        } else if (ctree.getClass().equals(CTreeS.class)) {
            CTreeS c = (CTreeS) ctree;
            Object v = this.interpretV(c.getVtree());
            if (v.getClass().equals(Pointer.class)) {
                this.error("cant set array");
            }
            this.memory.setData(this.interpretL(c.getLtree()), v);
        } else if (ctree.getClass().equals(CTreeW.class)) {
            CTreeW c = (CTreeW) ctree;
            while (toBoolean(this.interpretV(c.getVtree()))) {
                try {
                    this.interpretB(c.getBtree(), false);
                } catch (BreakException ex) {
                    break;
                }
                Thread.sleep(0, 1);
            }
        } else if (ctree.getClass().equals(CTreeB.class)) {
            throw new BreakException(name, line, "break must be in loop");
        } else if (ctree.getClass().equals(CTreeI.class)) {
            CTreeI c = (CTreeI) ctree;
            if (toBoolean(this.interpretV(c.getVtree()))) {
                this.interpretB(c.getB1tree(), false);
            } else {
                this.interpretB(c.getB2tree(), false);
            }
        } else if (ctree.getClass().equals(BTree.class)) {
            BTree c = (BTree) ctree;
            this.interpretB(c, true);
        } else if (ctree.getClass().equals(CTreeP.class)) {
            CTreeP c = (CTreeP) ctree;
            Object value = this.interpretV(c.getVtree());
            this.output.write(value.toString());
        } else if (ctree.getClass().equals(CTreeR.class)) {
            CTreeR c = (CTreeR) ctree;
            Object in = input.read();
            this.memory.setData(this.interpretL(c.getLtree()), in);
        } else if (ctree.getClass().equals(CTreeF.class)) {
            CTreeF c = (CTreeF) ctree;
            this.interpretV(new VTreeF(c.getFunction(), c.getArgs(), c.getName(), c.getLine()));
        } else {
            this.error("unknown command");
        }
    }

    private Pointer interpretD(DTree dtree) throws Exception {

        this.line = dtree.getLine();
        if (dtree.getClass().equals(DTreeA.class)) {
            DTreeA d = (DTreeA) dtree;
            return memory.declareArray(this.interpretS(d.getDimension()));
        } else {
            this.error("unknown declaration");
        }
        return null;
    }

    private Vector<Object> interpretS(Vector<VTree> stree) throws Exception {
        long s = 1;
        Vector<Long> dim = new Vector<Long>();
        for (VTree d : stree) {
            Object e = this.interpretV(d);
            if (e.getClass().getSuperclass().equals(Number.class)) {
                long b = ((Number) e).longValue();
                if (b < 0) {
                    this.error("array dimension must be not negative integer: " + b);
                }
                dim.add(b);
                s = s * b;
                if (s > Memory.ARRAY_SIZE_LIMIT) {
                    this.error("array size limit exceeded");
                }
            } else {
                this.error("array dimension must be integer: " + e.getClass().getSimpleName());
            }
        }

        Vector<Object> array = new Vector<Object>();
        try {
            Long[] arr = new Long[new Long(s).intValue()];
            Arrays.fill(arr, 0L);
            array = new Vector<Object>(Arrays.asList(arr));
            array.add(dim);
        } catch (OutOfMemoryError ex) {
            this.error("memory limit exceeded");
        }
        return array;
    }

    private int interpretI(Pointer pointer, Vector<VTree> itree) throws Exception {
        long s = 1;
        int ind = 0;
        Vector<Long> stree = memory.length(pointer);
        if (stree.size() != itree.size()) {
            this.error("array dimension not matching");
        }
        for (Long i : stree) {
            s *= i;
        }
        for (int i = 0; i < itree.size(); i++) {
            Object e = this.interpretV(itree.get(i));
            if (e.getClass().getSuperclass().equals(Number.class)) {
                long b = ((Number) e).longValue();
                if (b < 0) {
                    this.error("array indices must be not negative: " + b);
                }
                s /= stree.get(i);
                if (b >= stree.get(i)) {
                    this.error("array index out of array size");
                }
                ind += b * s;
            } else {
                this.error("array indices must be integer: " + e.getClass().getSimpleName());
            }
        }
        return ind;
    }

    private Location interpretL(LTree ltree) throws Exception {

        this.line = ltree.getLine();
        if (ltree.getClass().equals(LTreeV.class)) {
            LTreeV l = (LTreeV) ltree;
            return new Location(new Pointer(0), l.getVar().toLowerCase());
        } else if (ltree.getClass().equals(LTreeI.class)) {
            LTreeI l = (LTreeI) ltree;
            Object o = memory.getData(new Location(new Pointer(0), l.getVar().toLowerCase()));
            if (o.getClass().equals(Pointer.class)) {
                Pointer p = (Pointer) o;
                return new Location(p, this.interpretI(p, l.getItree()));
            } else {
                this.error("variable is not array: " + l.getVar());
            }
        } else {
            this.error("invalid lefthand type: " + ltree.getClass());
        }
        return null;
    }

    private Object interpretE(ETree etree) throws Exception {

        this.line = etree.getLine();
        Object ret = null;
        String op = etree.getOperator();
        Object v1 = this.interpretV(etree.getV1tree());
        Object v2 = this.interpretV(etree.getV2tree());
        if (Language.notKeys().contains(op.toLowerCase())) {
            ret = !this.toBoolean(v2);
        } else if (Language.orKeys().contains(op.toLowerCase())) {
            ret = this.toBoolean(v1) || this.toBoolean(v2);
        } else if (Language.andKeys().contains(op.toLowerCase())) {
            ret = this.toBoolean(v1) && this.toBoolean(v2);
        } else if ((op.equals("==") || op.equals("="))
                && (v1.getClass().equals(String.class) || v2.getClass().equals(String.class))) {
            ret = v1.toString().equals(v2.toString());
        } else if ((op.equals("!=") || op.equals("<>"))
                && (v1.getClass().equals(String.class) || v2.getClass().equals(String.class))) {
            ret = v1.toString().equals(v2.toString());
        } else if (op.equals("+") && (v1.getClass().equals(String.class) || v2.getClass().equals(String.class))) {
            ret = v1.toString() + v2.toString();
        } else if (v1.getClass().getSuperclass().equals(Number.class)
                && v2.getClass().getSuperclass().equals(Number.class)) {
            double b1, b2;
            b1 = ((Number) v1).doubleValue();
            b2 = ((Number) v2).doubleValue();
            if (op.equals("+")) {
                ret = b1 + b2;
            } else if (op.equals("-")) {
                ret = b1 - b2;
            } else if (op.equals("*")) {
                ret = b1 * b2;
            } else if (op.equals("/")) {
                ret = b1 / b2;
            } else if (op.equals("%")) {
                ret = b1 % b2;
            } else if (op.equals(">")) {
                ret = b1 > b2;
            } else if (op.equals(">=")) {
                ret = b1 >= b2;
            } else if (op.equals("<")) {
                ret = b1 < b2;
            } else if (op.equals("<=")) {
                ret = b1 <= b2;
            } else if (op.equals("==") || op.equals("=")) {
                ret = b1 == b2;
            } else if (op.equals("!=") || op.equals("<>")) {
                ret = b1 != b2;
            } else {
                this.error("unknown operator: " + op);
            }
            if (ret.getClass().equals(Double.class)
                    && (Double.isInfinite(((Double) ret)) || Double.isNaN(((Double) ret)))) {
                error("illegal arithmetic expression");
            }
            if (v1.getClass().equals(Long.class)) {
                if (ret.getClass().equals(Double.class)) {
                    ret = ((Double) ret).longValue();
                }
            }
        } else if (v1.getClass().equals(Pointer.class) || v2.getClass().equals(Pointer.class)) {
            this.error(etree + "\ninvalid operator '" + op + "' for array");
        } else {
            this.error(
                    etree + "\ninvalid operator '" + op + "' between " + v1.toString() + ", " + v2.toString() + "; ");
        }
        return ret;
    }

    private Object interpretV(VTree vtree) throws Exception {

        this.line = vtree.getLine();
        if (vtree.getClass().equals(VTreeL.class)) {
            return memory.getData(this.interpretL(((VTreeL) vtree).getLtree()));
        } else if (vtree.getClass().equals(VTreeF.class)) {
            VTreeF v = (VTreeF) vtree;
            Vector<String> func = new Vector<String>(Arrays.asList(new String[] { "acos", "asin", "atan", "ceil", "cos",
                    "exp", "fabs", "floor", "log", "log10", "sin", "sqrt", "tan", "min", "max", "pow", "abs" }));
            if (func.contains(v.getFunction().toLowerCase())) {
                if (v.getArgs().size() == 1) {
                    Object a1 = this.interpretV(v.getArgs().get(0));
                    if (a1.getClass().getSuperclass().equals(Number.class)) {
                        try {
                            Object ret = Math.class.getMethod(v.getFunction().toLowerCase(), double.class).invoke(this,
                                    ((Number) a1).doubleValue());
                            if (ret.getClass().equals(Double.class) && Double.isInfinite((Double) ret)
                                    || Double.isNaN((Double) ret)) {
                                error("illegal arithmetic expression");
                            }
                            return ret;
                        } catch (Exception ex) {
                            error(ex.getMessage());
                        }
                    } else {
                        this.error(v.getFunction() + " function argument must be number");
                    }
                } else if (v.getArgs().size() == 2) {
                    Object a1 = this.interpretV(v.getArgs().get(0));
                    Object a2 = this.interpretV(v.getArgs().get(1));
                    if (a1.getClass().getSuperclass().equals(Number.class)
                            && a2.getClass().getSuperclass().equals(Number.class)) {
                        try {
                            return Math.class.getMethod(v.getFunction().toLowerCase(), double.class, double.class)
                                    .invoke(this, ((Number) a1).doubleValue(), ((Number) a2).doubleValue());
                        } catch (Exception ex) {
                            error(v.getFunction() + " function not found");
                        }
                    } else {
                        this.error(v.getFunction() + " function argument must be number");
                    }
                } else {
                    this.error(v.getFunction() + " function argument number does not match");
                }
            } else {
                if (this.level >= Interpreter.MAX_LEVEL) {
                    this.error("Recursive level reach max: " + this.level);
                }
                Vector<Object> fargs = new Vector<Object>();
                for (int i = 0; i < v.getArgs().size(); i++) {
                    fargs.add(this.interpretV(v.getArgs().get(i)));
                }
                subInterpreter = new Interpreter(this.reader, this.input, this.output, this.funcs, level + 1);
                subInterpreter.interpret(v.getFunction(), fargs, this.debug);
                this.subInterpret = true;
                while (subInterpreter.isFinished() == false) {
                    try {
                        subInterpreter.checkError();
                        Thread.sleep(0, 1);
                    } catch (Exception ex) {
                        throw ex;
                    }
                }
                this.subInterpret = false;
                return subInterpreter.getReturnValue();
            }
        } else if (vtree.getClass().equals(ETree.class)) {
            return this.interpretE((ETree) vtree);
        } else if (vtree.getClass().equals(VTreeV.class)) {
            return ((VTreeV) vtree).getValue();
        } else {
            this.error("invalid value type: " + vtree.getClass().toString());
        }
        return null;
    }

    private void interpretP(Vector<LTreeV> ptree, Vector<Object> args) throws Exception {
        if (ptree.size() != args.size()) {
            this.error(name + " function argument number does not match");
        }
        for (int i = 0; i < ptree.size(); i++) {
            memory.setData(this.interpretL(ptree.get(i)), args.get(i));
        }
    }

    private boolean toBoolean(Object value) {
        if (value.getClass().equals(Long.class)) {
            return ((Long) value).longValue() != 0L;
        } else if (value.getClass().equals(String.class)) {
            return !((String) value).isEmpty();
        } else if (value.getClass().equals(Double.class)) {
            return ((Double) value).compareTo(0.0) != 0;
        } else if (value.getClass().equals(Boolean.class)) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }
}

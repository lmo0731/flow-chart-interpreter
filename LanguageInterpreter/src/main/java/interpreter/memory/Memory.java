/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.memory;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author LMO
 */
public class Memory {

    Vector<Object> heap;
    Vector<Integer> isFree;
    public static int ARRAY_SIZE_LIMIT = Integer.MAX_VALUE;

    public Memory() {
        heap = new Vector<Object>();
        heap.add(new HashMap<String, Object>());
        isFree = new Vector<Integer>();
        isFree.add(1);
    }

    private Pointer getFree() {
        Vector<Object> array = new Vector<Object>();
        Vector<Integer> dimension = new Vector<Integer>();
        dimension.add(new Integer(1));
        array.add(0);
        array.add(dimension);
        int ret = isFree.indexOf(0);
        if (ret == -1) {
            ret = heap.size();
            heap.add(array);
            isFree.add(0);
        } else {
            heap.set(ret, array);
        }
        return new Pointer(ret);
    }

    public Object getData(Location location) throws MemoryException {
        Object e = this.heap.get(location.getPointer().getAddress());
        if (e != null && e.getClass().equals(Vector.class) && location.getKey().getClass().equals(Integer.class)) {
            return ((Vector<Object>) e).get((Integer) location.getKey());
        } else if (e != null && e.getClass().equals(HashMap.class) && location.getKey().getClass().equals(String.class)) {
            HashMap<String, Object> h = (HashMap<String, Object>) e;
            if (!h.containsKey((String) location.getKey())) {
                // throw new NotDeclaredMemoryException((String) location.getKey() + " variable not declared");
                //h.put((String) location.getKey(), new Long(0));
            }
            return h.get((String) location.getKey());
        } else {
            // throw new MemoryException("invalid key type " + location.getKey().toString());
        }
        return null;
    }

    public Object getArray(Pointer pointer) {
        return this.heap.get(pointer.getAddress());
    }

    public void setData(Location location, Object value) throws MemoryException {
        Object oldValue = new Long(0);
        try {
            oldValue = this.getData(location);
        } catch (NotDeclaredMemoryException ex) {
        }
        if (value.getClass().equals(Pointer.class) && location.getPointer().getAddress() != 0) {
            throw new MemoryException("Invalid set operation");
        }
        Object e = this.heap.get(location.getPointer().getAddress());
        if (e.getClass().equals(Vector.class) && location.getKey().getClass().equals(Integer.class)) {
            ((Vector<Object>) e).set((Integer) location.getKey(), value);
        } else if (e.getClass().equals(HashMap.class) && location.getKey().getClass().equals(String.class)) {
            HashMap<String, Object> h = (HashMap<String, Object>) e;
            if (!h.containsKey((String) location.getKey())) {
                h.put((String) location.getKey(), new Long(0));
            }
            h.put((String) location.getKey(), value);
        } else {
            throw new MemoryException("invalid key type " + location.getKey().toString());
        }
        if (oldValue != null && oldValue.getClass().equals(Pointer.class)) {
            int ind = ((Pointer) oldValue).getAddress();
            this.isFree.set(ind, this.isFree.get(ind) - 1);
        }
        if (value.getClass().equals(Pointer.class)) {
            int ind = ((Pointer) value).getAddress();
            this.isFree.set(ind, this.isFree.get(ind) + 1);
        }
    }

    public Pointer declareArray(Object o) {
        Pointer pointer = this.getFree();
        heap.set(pointer.getAddress(), o);
        return pointer;
    }

    public Vector<Long> length(Pointer pointer) throws MemoryException {
        Object o = heap.get(pointer.getAddress());
        if (o.getClass().equals(Vector.class)) {
            Vector<Object> v = (Vector<Object>) o;
            if (v.size() > 0 && v.lastElement().getClass().equals(Vector.class)) {
                return (Vector<Long>) v.lastElement();
            } else {
                throw new MemoryException("invalid array structure at " + pointer.toString());
            }
        } else {
            throw new MemoryException("not array at " + pointer.toString());
        }
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < heap.size(); i++) {
            Object e = heap.get(i);
            String s = "";
            if (e.getClass().equals(Vector.class)) {
                Vector<Object> v = (Vector<Object>) e;
                s += "[";
                for (int j = 0; j < Math.min(30, v.size()); j++) {
                    s += v.get(j).toString() + ", ";
                }
                if (100 < v.size()) {
                    s += "... ";
                }
                s += v.lastElement().toString();
                s += "]";
            } else {
                s = e.toString();
            }
            ret += "" + i + ": " + s + " - " + isFree.get(i) + "\n";
        }
        return ret;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        heap.clear();
    }
}

package langdon.util;

import langdon.math.Expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

public final class ArrayLists {
    
    public static void main(String[] args) {
        ArrayList<Object> toSplit = new ArrayList<Object>();
        toSplit.add("good");
        toSplit.add("bald");
        toSplit.add("bad");
        toSplit.add("bad");
        toSplit.add("not");
        toSplit.add("not");
        toSplit.add("bad");
        toSplit.add("good");
        toSplit.add("not");
        toSplit.add("bad");
        Object[] splitOn = {"good", "bad"};
        ArrayList<Object> objectFound = new ArrayList<Object>();
        
        System.out.println(toSplit);
        System.out.println("splitOn: " + Arrays.toString(splitOn));
        System.out.println(" 0: " + split(toSplit, splitOn, 0, objectFound) + "\n    " + objectFound);
        System.out.println("-1: " + split(toSplit, splitOn, -1, objectFound) + "\n    " + objectFound);
        System.out.println(" 1: " + split(toSplit, splitOn, 1, objectFound) + "\n    " + objectFound);
        System.out.println(" 2: " + split(toSplit, splitOn, 2, objectFound) + "\n    " + objectFound);
    }
    
    public static <T> ArrayList<ArrayList<T>> split(ArrayList<T> arrayList, T[] splitOn) {
        return split(arrayList, splitOn, 0);
    }
    
    public static <T> ArrayList<ArrayList<T>> split(ArrayList<T> arrayList, T[] splitOn, int direction) {
        return split(arrayList, splitOn, direction, null);
    }
    
    public static <T> ArrayList<ArrayList<T>> split(ArrayList<T> arrayList, T[] splitOn, int direction, ArrayList<T> objectFound) {
        if (!containsIn(arrayList, splitOn)) {
            return null;
        }
        
        arrayList = (ArrayList<T>) arrayList.clone();
        ArrayList<ArrayList<T>> splitted = new ArrayList<ArrayList<T>>();
        objectFound.clear();
        
        if (direction == 0) {
            while (containsIn(arrayList, splitOn)) {
                List<T> subList = arrayList.subList(0, indexIn(arrayList, splitOn, objectFound));
                ArrayList<T> tmp = new ArrayList<T>(subList);
                if (tmp.size() > 0) {
                    splitted.add(tmp);
                }
                
                subList.clear();
                arrayList.remove(0);
            }
            if (arrayList.size() > 0) {
                splitted.add(arrayList);
            }
        }
        else if (direction == -1) {
            List<T> subList = arrayList.subList(0, indexIn(arrayList, splitOn, objectFound));
            ArrayList<T> tmp = new ArrayList<T>(subList);
            
            splitted.add(tmp);
            
            subList.clear();
            arrayList.remove(0);
            
            splitted.add(arrayList);
        }
        else if (direction == 1) {
            List<T> subList = arrayList.subList(lastIndexIn(arrayList, splitOn, objectFound) + 1, arrayList.size());
            ArrayList<T> tmp = new ArrayList<T>(subList);
            
            splitted.add(tmp);
            
            subList.clear();
            arrayList.remove(arrayList.size() - 1);
            
            splitted.add(0, arrayList);
        }
        else if (direction == 2) {
            while (containsIn(arrayList, splitOn)) {
                List<T> subList = arrayList.subList(0, indexInMultFound(arrayList, splitOn, objectFound));
                ArrayList<T> tmp = new ArrayList<T>(subList);
                if (tmp.size() > 0) {
                    splitted.add(tmp);
                } else {
                    objectFound.remove(objectFound.size() - 1);
                }
                
                subList.clear();
                arrayList.remove(0);
            }
            if (arrayList.size() > 0) {
                splitted.add(arrayList);
            } else {
                objectFound.remove(objectFound.size() - 1);
            }
        }
        
        return splitted;
    }

    public static <T> int indexIn(ArrayList<T> arrayList, T[] objects) {
	    return indexIn(arrayList, objects, null);
    }
    
    public static <T> int indexIn(ArrayList<T> arrayList, T[] objects, ArrayList<T> objectFound) {
        int index = -1;
        
        for (T object : objects) {
            int indexOf = arrayList.indexOf(object);
            if (indexOf != -1 && (index == -1 || indexOf < index)) {
                index = indexOf;
                if (objectFound != null) {
                    if (objectFound.size() == 0) {
                        objectFound.add(arrayList.get(indexOf));
                    } else {
                        objectFound.set(0, arrayList.get(indexOf));
                    }
                }
            }
        }
        
        return index;
    }
    
    public static <T> int indexInMultFound(ArrayList<T> arrayList, T[] objects, ArrayList<T> objectFound) {
        int initObjectsFound = objectFound.size();
        int index = -1;
        
        for (T object : objects) {
            int indexOf = arrayList.indexOf(object);
            if (indexOf != -1 && (index == -1 || indexOf < index)) {
                index = indexOf;
                if (objectFound != null) {
                    if (objectFound.size() == initObjectsFound) {
                        objectFound.add(arrayList.get(indexOf));
                    } else {
                        objectFound.set(initObjectsFound, arrayList.get(indexOf));
                    }
                }
            }
        }
        
        return index;
    }

    public static <T> int lastIndexIn(ArrayList<T> arrayList, T[] objects) {
	    return lastIndexIn(arrayList, objects, null);
    }
    
    public static <T> int lastIndexIn(ArrayList<T> arrayList, T[] objects, ArrayList<T> objectFound) {
        int index = -1;
        
        for (T object : objects) {
            int lastIndexOf = arrayList.lastIndexOf(object);
            if (lastIndexOf > index) {
                index = lastIndexOf;
                if (objectFound != null) {
                    if (objectFound.size() == 0) {
                        objectFound.add(arrayList.get(lastIndexOf));
                    } else {
                        objectFound.set(0, arrayList.get(lastIndexOf));
                    }
                }
            }
        }
        
        return index;
    }
    
    public static <T> boolean containsIn(ArrayList<T> arrayList, T[] objects) {
        for (T object : objects) {
            if (arrayList.contains(object)) return true;
        }
        return false;
    }
    
    public static ArrayList<Expr> copyAll(ArrayList<Expr> exprs, HashMap<Expr, Expr> subs) {
        ArrayList<Expr> copies = new ArrayList<Expr>(exprs.size());
        for (Expr expr : exprs) {
            copies.add(expr.copy(subs));
        }
        return copies;
    }
    
    public static Expr productArrToExpr(ArrayList<Expr> exprs) {
        return productArrToExpr(exprs, true);
    }
    
    public static Expr productArrToExpr(ArrayList<Expr> exprs, boolean simplify) {
        if (exprs.isEmpty()) return langdon.math.Number.make(1);
        if (exprs.size() == 1) return exprs.get(0);
        return langdon.math.Product.make(exprs, simplify);
    }
    
    public static <T> ArrayList<T> castAll(ArrayList arrList, Class<T> toClass) {
        ArrayList<T> newArrList = new ArrayList<T>();
        for (Object o : arrList) {
            newArrList.add((T) o);
        }
        return newArrList;
    }
    
    public static String dumpAll(ArrayList<Expr> exprs) {
        ArrayList<String> strs = new ArrayList<String>();
        for (Expr expr: exprs) {
            strs.add(expr.toString());
        }
        return strs.toString();
    }
    
    public static boolean elemExprsEqual(ArrayList<? extends Expr> al1, ArrayList<? extends Expr> al2) {
        if (al1.size() != al2.size()) return false;
        for (int i = 0; i < al1.size(); i++) {
            if (!al1.get(i).equalsExpr(al2.get(i))) return false;
        }
        return true;
    }
    
}

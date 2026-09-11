package com.tarotcrawler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** Minimalny runner testow jednostkowych (zero zaleznosci).
 *  Uruchomienie: podaj nazwy klas testowych jako argumenty.
 *  Metoda testowa = public void test*() bez argumentow. */
public class TestRunner {

    private static int pass;
    private static int fail;
    private static final List<String> failures = new ArrayList<>();

    public static void ok(boolean cond, String msg) {
        if (!cond) {
            throw new AssertionError(msg);
        }
    }

    public static void eq(Object expected, Object actual, String msg) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(msg + " (oczekiwano=" + expected + ", jest=" + actual + ")");
        }
    }

    public static void eq(int expected, int actual, String msg) {
        if (expected != actual) {
            throw new AssertionError(msg + " (oczekiwano=" + expected + ", jest=" + actual + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uzycie: TestRunner <klasy testowe>");
            System.exit(2);
        }
        for (String cls : args) {
            Class<?> c = Class.forName(cls);
            Object inst = c.getDeclaredConstructor().newInstance();
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName().startsWith("test") && m.getParameterCount() == 0
                        && m.getReturnType() == void.class) {
                    String name = cls + "." + m.getName();
                    try {
                        m.invoke(inst);
                        pass++;
                        System.out.println("PASS " + name);
                    } catch (InvocationTargetException e) {
                        fail++;
                        failures.add(name + " -> " + e.getCause());
                        System.out.println("FAIL " + name + " -> " + e.getCause());
                    }
                }
            }
        }
        System.out.println("WYNIK: pass=" + pass + " fail=" + fail);
        for (String f : failures) {
            System.out.println("  NIE PRZESZLO: " + f);
        }
        System.exit(fail == 0 ? 0 : 1);
    }
}

package cz.vity.freerapid.sandbox;

/**
 * @author Ladislav Vitasek
 */
public class Pin {
    public static void main(String[] args) {
        int a, b, c, d, e;
        for (int i = 0; i < 100000; ++i) {
            final String s = String.format("%05d", i);
            a = getIndex(s, 0);
            b = getIndex(s, 1);
            c = getIndex(s, 2);
            d = getIndex(s, 3);
            e = getIndex(s, 4);
            if (a + b + c + d + e == 30 && c + e == 14 && b + 1 == d && a == 2 * b - 1 && b + c == 10) {
                System.out.println("a = " + a);
                System.out.println("b = " + b);
                System.out.println("c = " + c);
                System.out.println("d = " + d);
                System.out.println("e = " + e);
                System.out.println("-------");
                System.out.println("s = " + i);
                System.out.println("-------");
            }
        }
    }

    private static int getIndex(String s, int i) {
        return Integer.valueOf("" + s.charAt(i));
    }
}

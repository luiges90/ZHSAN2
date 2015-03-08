package com.zhsan.common;

/**
 * Created by Peter on 8/3/2015.
 */
public final class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point fromCSV(String s) {
        String[] t = s.split("\\s");
        return new Point(
                Integer.parseInt(t[0]),
                Integer.parseInt(t[1])
        );
    }
}

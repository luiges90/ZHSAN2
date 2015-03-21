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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        if (y != point.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

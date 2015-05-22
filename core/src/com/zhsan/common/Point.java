package com.zhsan.common;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Peter on 8/3/2015.
 */
public final class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Vector2 v) {
        this.x = MathUtils.round(v.x);
        this.y = MathUtils.round(v.y);
    }

    public static Point fromCSV(String s) {
        String[] t = s.split("\\s");
        return new Point(
                Integer.parseInt(t[0]),
                Integer.parseInt(t[1])
        );
    }

    public static List<Point> fromCSVList(String s) {
        List<Point> points = new ArrayList<>();
        String[] t = s.trim().split("\\s");

        for (int i = 0; i < t.length; i += 2) {
            points.add(new Point(Integer.parseInt(t[i]), Integer.parseInt(t[i + 1])));
        }

        return points;
    }

    public String toCSV() {
        return x + " " + y;
    }

    public static String toCSVList(List<Point> points) {
        StringBuilder sb = new StringBuilder();
        for (Point p : points) {
            sb.append(p.x).append(" ").append(p.y).append(" ");
        }
        return sb.toString();
    }

    public static Point getCenter(List<Point> shape) {
        Point minX = Collections.min(shape, (o1, o2) -> Integer.compare(o1.x, o2.x));
        Point minY = Collections.min(shape, (o1, o2) -> Integer.compare(o1.y, o2.y));
        Point maxX = Collections.max(shape, (o1, o2) -> Integer.compare(o1.x, o2.x));
        Point maxY = Collections.max(shape, (o1, o2) -> Integer.compare(o1.y, o2.y));
        int cx = (minX.x + maxX.x) / 2;
        int cy = (minY.y + maxY.y) / 2;

        return new Point(cx, cy);
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

    public static Point fromXml(Node position) {
        return new Point(
                Integer.parseInt(XmlHelper.loadAttribute(position, "X")),
                Integer.parseInt(XmlHelper.loadAttribute(position, "Y")));
    }
}

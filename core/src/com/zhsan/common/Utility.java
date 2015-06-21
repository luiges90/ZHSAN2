package com.zhsan.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.w3c.dom.Node;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by Peter on 7/3/2015.
 */
public class Utility {
    private Utility() {}

    public static Rectangle adjustRectangleByHAlignment(Rectangle in, BitmapFont.HAlignment align, float containerWidth) {
        Rectangle out = new Rectangle(in);
        switch (align) {
            case LEFT:
                break;
            case RIGHT:
                out.x = containerWidth - out.width - out.x;
                break;
            case CENTER:
                out.x = containerWidth / 2 - out.width / 2 + out.x;
                break;
        }
        return out;
    }

    public static float diminishingGrowth(float original, float growth, float factor) {
        if (original <= 0) return original + growth * GlobalVariables.diminishingGrowthMaxFactor;
        return original + growth * Math.min(factor / original, GlobalVariables.diminishingGrowthMaxFactor);
    }

    public static Collector<Float, List<Float>, Float> diminishingSum(float factor) {
        return new DiminishingSum(factor);
    }

    private static class DiminishingSum implements Collector<Float, List<Float>, Float> {

        private final float factor;

        public DiminishingSum(float f) {
            factor = f;
        }

        @Override
        public Supplier<List<Float>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<Float>, Float> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<Float>> combiner() {
            return (x, y) -> {
                x.addAll(y);
                return x;
            };
        }

        @Override
        public Function<List<Float>, Float> finisher() {
            return (list) -> {
                list.sort(Float::compare);
                float result = 0;
                for (Float f : list) {
                    result += factor * f;
                }
                return result;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(
                    Characteristics.CONCURRENT,
                    Characteristics.UNORDERED));
        }
    }

}

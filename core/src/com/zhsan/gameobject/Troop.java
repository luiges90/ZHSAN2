package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Pair;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 8/8/2015.
 */
public class Troop extends GameObject {

    public static final String SAVE_FILE = "Troop.csv";

    private enum OrderKind {
        MOVE;

        static OrderKind fromCSV(String s) {
            switch (s) {
                case "move": return MOVE;
                default: assert false; return null;
            }
        }

        String toCSV() {
            switch (this) {
                case MOVE: return "move";
            }
            assert false;
            return null;
        }
    }

    private static class Order {
        public final OrderKind kind;
        public final Point targetLocation;

        private Order(OrderKind kind, Point targetLocation) {
            this.kind = kind;
            this.targetLocation = targetLocation;
        }

        static Order fromCSV(String kind, String target) {
            OrderKind orderKind = OrderKind.fromCSV(kind);
            if (orderKind != null) {
                switch (orderKind) {
                    case MOVE:
                        return new Order(orderKind, Point.fromCSV(target));
                }
            }
            assert false;
            return null;
        }

        Pair<String, String> toCSV() {
            String orderKind = kind.toCSV();
            switch (kind) {
                case MOVE:
                    return new Pair<>(orderKind, targetLocation.toCSV());
            }
            assert false;
            return null;
        }
    }

    private GameScenario scenario;

    private Military military;

    private Point location;

    private Order order;

    public static final GameObjectList<Troop> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<Troop> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Troop data = new Troop(Integer.parseInt(line[0]), scen);
                data.military = scen.getMilitary(Integer.parseInt(line[1]));
                data.location = Point.fromCSV(line[2]);
                data.order = Order.fromCSV(line[3], line[4]);

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<Troop> types) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.TROOP_SAVE_HEADER).split(","));
            for (Troop detail : types) {
                Pair<String, String> orderStr = detail.order.toCSV();
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        String.valueOf(detail.military.getId()),
                        detail.location.toCSV(),
                        orderStr.x,
                        orderStr.y
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    public Troop(int id, GameScenario scen) {
        super(id);
        this.scenario = scen;
    }

    @Override
    public String getName() {
        return military.getName();
    }

    public Military getMilitary() {
        return military;
    }

    public Troop setMilitary(Military military) {
        this.military = military;
        return this;
    }

    public Point getLocation() {
        return location;
    }

    public Troop setLocation(Point location) {
        this.location = location;
        return this;
    }

    public void giveMoveToOrder(Point location) {
        this.order = new Order(OrderKind.MOVE, location);
    }

}

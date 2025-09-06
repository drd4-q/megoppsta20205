package ru.minced.client.util.animation.bezier;

import lombok.Setter;

public abstract class Bezier {
    private final Point start = new Point(0.0, 0.0);
    private final Point end = new Point(1.0, 1.0);
    @Setter
    private Point point2;
    @Setter
    private Point point3;

    public Bezier(Point point2, Point point3) {
        this.setPoint2(point2);
        this.setPoint3(point3);
    }

    public Bezier() {
    }

    public abstract double getValue(double var1);

    public Point getStart() {
        return this.start;
    }

    public Point getEnd() {
        return this.end;
    }

    public Point getPoint2() {
        return this.point2;
    }

    public Point getPoint3() {
        return this.point3;
    }
}

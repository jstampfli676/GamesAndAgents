package trap_the_cat;

import java.awt.*;
import java.util.ArrayList;

public class Hex extends Polygon {
    private ArrayList<Hex> neighbors;
    private boolean blocked;
    private boolean hasCat;

    private int ID;
    private static int ID_Count = 0;

    public static final int SIDES = 6;

    private Point[] points = new Point[SIDES];
    public Point center = new Point(0, 0);
    public int radius;
    private int rotation = 90;

    public Hex(Point center, int radius) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = radius;

        updatePoints();

        blocked = false;
        hasCat = false;
        neighbors = new ArrayList<Hex>();
        ID = ID_Count;
        ID_Count++;
        System.out.println(ID);
    }

    public Hex(int x, int y, int radius) {
        this(new Point(x, y), radius);
    }

    public ArrayList<Hex> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<Hex> neighbors) {
        this.neighbors = neighbors;
    }

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean getCat() {
        return hasCat;
    }

    public void setCat(boolean hasCat) {
        this.hasCat = hasCat;
    }

    public int getId() {
        return ID;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Hex)) {
            return false;
        }
        Hex h = (Hex) o;
        return h.ID == this.ID;
    }

    public static void resetIDCount() {
        ID_Count = 0;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;

        updatePoints();
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;

        updatePoints();
    }

    public void setCenter(Point center) {
        this.center = center;

        updatePoints();
    }

    public void setCenter(int x, int y) {
        setCenter(new Point(x, y));
    }

    private double findAngle(double fraction) {
        return fraction * Math.PI * 2 + Math.toRadians((rotation + 180) % 360);
    }

    private Point findPoint(double angle) {
        int x = (int) (center.x + Math.cos(angle) * radius);
        int y = (int) (center.y + Math.sin(angle) * radius);

        return new Point(x, y);
    }

    protected void updatePoints() {
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint(angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
            points[p] = point;
            //System.out.printf("%d. (%d, %d)\n", p, point.x, point.y);
        }
    }

    public void drawPolygon(Graphics2D g, int x, int y, int lineThickness, int colorValue, boolean filled) {
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        if (filled)
            g.fillPolygon(xpoints, ypoints, npoints);
        else
            g.drawPolygon(xpoints, ypoints, npoints);

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
    }

    public String toString() {
        return String.valueOf(ID);
    }
}

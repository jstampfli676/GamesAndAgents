package battleship;

public class Tile {
    private int id;
    private boolean hit;
    private boolean containsShip;
    private Ship ship;

    public Tile(int id) {
        this(id, false, false, null);
    }

    public Tile(Tile t) {
        this(t.id, t.hit, t.containsShip, t.ship);
    }

    public Tile(int id, boolean hit, boolean containsShip, Ship ship) {
        this.id = id;
        this.hit = hit;
        this.containsShip = containsShip;
        if (ship == null) {
            this.ship = ship;
        } else {
            this.ship = new Ship(ship);
        }
    }

    public int attack() {
        if (!hit) {
            hit = true;
            if (containsShip) {
                return ship.attack();
            }
            return -1;
        }
        return 0;
    }

    public boolean putShip(Ship ship) {
        if (this.containsShip) {
            System.out.println("putting a ship on top of another ship");
            this.ship = ship;
            containsShip = true;
            return false;
        }
        this.ship = ship;
        containsShip = true;
        return true;
    }

    public boolean containsShip() {
        return this.containsShip;
    }

    public boolean isHit() {
        return this.hit;
    }

    public Ship getShip() {
        return this.ship;
    }

    public String toString() {
        if (hit) {
            if (containsShip) {
                return "[X]";
            }
            return "[O]";
        }
        return "[ ]";
    }
}

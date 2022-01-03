package battleship;

public class Ship {
    private int length;
    private int health;
    private int[] tiles;

    public Ship(Ship s) {
        this(s.length, s.health, s.tiles);
    }

    public Ship(int length) {
        this(length, new int[length]);
    }

    public Ship(int length, int[] tiles) {
        this(length, length, tiles);
    }

    public Ship(int length, int health, int[] tiles) {
        this.length = length;
        this.health = health;
        this.tiles = tiles;
    }

    public int attack() {
        health-=1;
        if (health==0) {
            return 5;
        }
        return 1;
    }

    public void setLocation(int[] tiles){
        this.tiles = tiles;
    }

    public int getLength() {
        return this.length;
    }

    public boolean isSunk() {
        if (health<=0) {
            return true;
        }
        return false;
    }
}

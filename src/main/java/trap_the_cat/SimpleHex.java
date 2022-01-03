package trap_the_cat;

public class SimpleHex {
    private boolean blocked;
    private boolean containsCat;
    private int id;

    public SimpleHex(int id) {
        this(false, false, id);
    }

    public SimpleHex(SimpleHex h) {
        this(h.blocked, h.containsCat, h.id);
    }

    public SimpleHex(boolean blocked, boolean containsCat, int id) {
        this.id = id;
        this.containsCat = containsCat;
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void block() {
        blocked = true;
    }

    public void setCat(boolean value) {
        containsCat = value;
    }

    public boolean containsCat() {
        return containsCat;
    }

    public int getId() {
        return id;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SimpleHex)) {
            return false;
        }
        SimpleHex h = (SimpleHex) o;
        return h.blocked == this.blocked && h.containsCat == this.containsCat && h.id == this.id;
    }
}

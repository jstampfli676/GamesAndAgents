package farkle;

public class Die implements Comparable{
    int curValue;
    int maxValue;

    public Die(int curValue) {
        this.maxValue = 6;
        this.curValue = Math.min(curValue, maxValue);
        if (this.curValue<1) {
            this.curValue=1;
        }
    }

    public Die() {
        this.maxValue = 6;
    }

    public Die(int maxValue, int curValue) {
        this.maxValue = maxValue;
        this.curValue = curValue>maxValue?maxValue:curValue;
    }

    public int roll() {
        curValue = (int)(Math.random()*maxValue+1);
        return curValue;
    }

    public int compareTo(Object o) {
        Die d = (Die) o;
        return this.curValue - d.curValue;
    }

    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }
        if (!(o instanceof Die)) {
            return false;
        }
        Die d = (Die) o;
        return this.maxValue == d.maxValue && this.curValue == d.curValue;
    }

    public String toString() {
        return String.valueOf(this.curValue);
    }

    public int getCurValue() {
        return curValue;
    }
}

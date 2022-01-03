package black_jack;

public class PlayerDealerTotals {
    private int playerTotal;
    private int dealerTotal;

    public PlayerDealerTotals(int playerTotal, int dealerTotal) {
        this.playerTotal = playerTotal;
        this.dealerTotal = dealerTotal;
    }

    public int getPlayerTotal() {
        return playerTotal;
    }

    public int getDealerTotal() {
        return dealerTotal;
    }

    public String toString() {
        return "(" + playerTotal + ", " + dealerTotal + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerDealerTotals)) {
            return false;
        }
        PlayerDealerTotals pdt = (PlayerDealerTotals) o;
        return pdt.playerTotal == this.playerTotal && pdt.dealerTotal == this.dealerTotal;
    }

    public int hashCode() {
        return String.valueOf(playerTotal).hashCode() + String.valueOf(dealerTotal).hashCode();
    }
}

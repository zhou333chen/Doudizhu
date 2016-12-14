package Model;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
public class Card implements  Comparable{
    private int index; // 1-54 // 1：方片3 2：梅花3 3：红桃3 4：黑桃3……
    public int number;

    public void setIndex(int index) {
        this.index = index;
        if (this.index < 53) {
            this.number = (this.index - 1)/ 4;
        } else if (this.index == 53) {
            this.number = 99;
        } else if (this.index == 54) {
            this.number = 199;
        }
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        Card card = (Card)obj;
        if (card.index == index) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Object o) {
        Card card = (Card)o;
        return card.index - this.index;
    }
}

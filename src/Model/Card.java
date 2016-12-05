package Model;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
public class Card implements  Comparable{
    public int index; // 1-54 // 1：方片3 2：梅花3 3：红桃3 4：黑桃3……
    public int number;

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

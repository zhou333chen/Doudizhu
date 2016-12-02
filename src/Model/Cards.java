package Model;

import Game.GameChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jintiandalegehu on 2016/12/1.
 */

public class Cards {
    private ArrayList<Card> cardList;
    public int type;
    public int count;

    public Cards() {
        cardList = new ArrayList<>();
    }

    public void setCardList(ArrayList<Card> cardList) {
        this.cardList = cardList;
        this.count = cardList.size();
    }

    public Card get(int index) {
        return cardList.get(index);
    }

    public void sort() {
        Collections.sort(cardList);
    }

    public void addCard(Card card) {
        cardList.add(card);
        count++;
    }


    public void addCards(Cards cards) {
        cardList.addAll(cards.cardList);
        count += cards.count;
    }

    public void addCards(List<Card> cards) {
        cardList.addAll(cards);
        count += cards.size();
    }

    public void removeCard(Card card) {
        cardList.remove(card);
        count--;
    }

    public void removeAllCards() {
        cardList.clear();
        count = 0;
    }

    public void removeAllCards(Cards cards) {
        cardList.removeAll(cards.cardList);
        count -= cards.count;
    }

    public void removeAllCards(List<Card> cards) {
        cardList.removeAll(cards);
        count -= cards.size();
    }

    public List<Card> subCards(int fromIndex, int toIndex) {
        return cardList.subList(fromIndex, toIndex);
    }
}

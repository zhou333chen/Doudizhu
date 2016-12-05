package Game;

import Model.Card;
import Model.Cards;

import java.util.*;

import static Game.GameOperation.CONFIRM_OPERATION;
import static Game.GameOperation.PLAY_OPERATION;
import static Model.Cards.*;

/**
 * Created by jintiandalegehu on 2016/12/5.
 */
public class Robort {
    public static String generateConfirm(Gamer gamer) {
        System.out.println("auto confirm");
        return CONFIRM_OPERATION + "|" + gamer.user.userId;
    }

    public static String generatePlayCard(Gamer gamer, Cards compareCards) {
        System.out.print(gamer.user.userId + ":");
        for (Card card : gamer.cards.cardList) {
            System.out.print(card.number + " ");
        }
        System.out.println();
        List<Card> cards;
        if (compareCards == null || compareCards.count == 0) {
            cards = gamer.cards.cardList.subList(gamer.cards.count-1, gamer.cards.count);
        } else {
            switch (compareCards.type) {
                case TypeKingBomb:
                    cards = null;
                    break;
                case TypeNormalBomb:
                    cards = findNormalBomb(gamer.cards.cardList, compareCards.cardList);
                    break;
                case TypeStraight:
                    cards = findStraight(gamer.cards.cardList, compareCards.cardList);
                    break;
                case TypeCompany:
                    cards = findCompany(gamer.cards.cardList, compareCards.cardList);
                    break;
                case TypePlane:
                    cards = findPlane(gamer.cards.cardList, compareCards.cardList);
                    break;
                case TypeFour:
                    cards = findFour(gamer.cards.cardList, compareCards.cardList);
                    break;
                case TypeThree:
                    cards = findThree(gamer.cards.cardList, compareCards.cardList);
                    break;
                case TypeDouble:
                    cards = findDouble(gamer.cards.cardList, compareCards.cardList);
                    break;
                case TypeSingle:
                    cards = findSingle(gamer.cards.cardList, compareCards.cardList);
                    break;
                default:
                    cards = null;
                    break;
            }
            if (compareCards.type != TypeKingBomb && compareCards.type != TypeNormalBomb && cards == null) {
                cards = findNormalBomb(gamer.cards.cardList, null);
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append(PLAY_OPERATION + "|" + gamer.user.userId + "|");
        if (cards != null) {
            for (Card card : cards) {
                sb.append(card.index + "&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String generatePassLandlord(Gamer gamer) {
        System.out.println("auto pass");
        return "";
    }

    private static List<Card> findKingBomb(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        return null;
    }

    private static List<Card> findNormalBomb(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        int count = 1;
        for (int i=1; i<cards.size(); i++) {
            if (cards.get(i).index == cards.get(i-1).index + 1) {
                count++;
                if (count == 4 && (compareCards == null || cards.get(i).number > compareCards.get(0).number)) {
                    return cards.subList(i-3 , i+1);
                }
            } else {
                count = 1;
            }
        }
        return null;
    }

    private static List<Card> findStraight(ArrayList<Card> cards, ArrayList<Card> compareCards) {
//        int[] counts = new int[cards.size()];
//        int[] indexes = new int[cards.size()];
//        for (int i=0; i<cards.size(); i++) {
//            counts[i] = 1;
//            indexes[i] = i;
//        }
//        for (int i=cards.size()-2; i>=0; i--) {
//            for (int j=i+1; j<cards.size() && j<=i+7; j++) {
//                if (cards.get(i).number == cards.get(j).number + 1) {
//                    counts[i] = counts[j] + 1;
//                    indexes[i] = j;
//                    if (counts[i] >= compareCards.size() && cards.get(i).number > compareCards.get(0).number) {
//                        ArrayList<Card> result = new ArrayList<>();
//                        int index = i;
//                        for (int k=0; k<compareCards.size(); k++) {
//                            result.add(cards.get(index));
//                            index = indexes[i];
//                        }
//                        return result;
//                    }
//                    break;
//                } else if (cards.get(i).number > cards.get(j).number + 1) {
//                    break;
//                }
//            }
//        }
        Stack<Card> stack = new Stack<>();
        for (int i=cards.size()-1; i>=0; i--) {
            if (!stack.isEmpty()) {
                if (cards.get(i).index < 49 && cards.get(i).number == stack.peek().number + 1) {
                    stack.push(cards.get(i));
                    if (stack.size() == compareCards.size()) {
                        ArrayList<Card> result = new ArrayList<>();
                        while (!stack.isEmpty()) {
                            result.add(stack.pop());
                        }
                        return result;
                    }
                } else if (cards.get(i).number > stack.peek().number + 1) {
                    stack.clear();
                }
            } else if (cards.get(i).number > compareCards.get(compareCards.size()-1).number){
                stack.push(cards.get(i));
            }
        }
        return null;
    }

    private static List<Card> findCompany(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        return null;
    }

    private static List<Card> findPlane(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        return null;
    }

    private static List<Card> findFour(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        return null;
    }

    private static List<Card> findThree(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        return null;
    }

    private static List<Card> findDouble(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        return null;
    }

    private static List<Card> findSingle(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        return null;
    }
}

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
        for (int i=cards.size()-1; i>0; i--) {
            if (cards.get(i).number == cards.get(i-1).number) {
                count++;
                if (count == 4 && (compareCards == null || cards.get(i).number > compareCards.get(0).number)) {
                    return cards.subList(i-1 , i+3);
                }
            } else {
                count = 1;
            }
        }
        return null;
    }

    private static List<Card> findStraight(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        Stack<Card> stack = new Stack<>();
        for (int i=cards.size()-1; i>=0; i--) {
            if (!stack.isEmpty()) {
                if (cards.get(i).index < 49 && cards.get(i).number == stack.peek().number + 1) {
                    stack.push(cards.get(i));
                    if (stack.size() == compareCards.size()) {
                        return stackToArrayList(stack);
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
        Stack<Card> stack = new Stack<>();
        int flag = 2;   // 0代表没有牌，1代表已经有一张牌了，2代表有两张相同的牌
        for (int i=cards.size()-1; i>=0; i--) {
            if (cards.get(i).index >= 49) {
                break;
            }
            if (!stack.isEmpty()) {
                if (cards.get(i).number == stack.peek().number + 1 && flag == 2) {
                    stack.push(cards.get(i));
                    flag = 1;
                } else if (cards.get(i).number == stack.peek().number && flag == 1) {
                    stack.push(cards.get(i));
                    flag = 2;
                    if (stack.size() == compareCards.size()) {
                        return stackToArrayList(stack);
                    }
                } else {
                    stack.clear();
                }
            } else if (cards.get(i).number > compareCards.get(compareCards.size()-1).number){
                stack.push(cards.get(i));
            }
        }
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

    private static ArrayList<Card> stackToArrayList(Stack<Card> stack) {
        ArrayList<Card> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }
}

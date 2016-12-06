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
                } else if (cards.get(i).number > stack.peek().number && flag == 1){
                    stack.clear();
                }
            } else if (cards.get(i).number > compareCards.get(compareCards.size()-1).number){
                stack.push(cards.get(i));
                flag = 1;
            }
        }
        return null;
    }

    private static List<Card> findPlane(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        int comparePlaneCount = 0;
        for (int i=0; i<compareCards.size()-2; i=i+3) {
            if (compareCards.get(i).number == compareCards.get(i+1).number &&
                    compareCards.get(i).number == compareCards.get(i+2).number) {
                comparePlaneCount++;
            } else {
                break;
            }
        }
        int planeCount = 0;
        HashSet<Integer> cardNumbers = new HashSet<>();
        int lastPlaneNumber = -1;
        for (int i=cards.size()-1; i>=2; i--) {
            if (cards.get(i).number >= 49) {
                break;
            }
            // find body
            if (cards.get(i).number == cards.get(i-1).number &&
                    cards.get(i).number == cards.get(i-2).number) {
                lastPlaneNumber = cards.get(i).number;
                planeCount++;
                i -= 2;
                if (planeCount == comparePlaneCount) {
                    ArrayList<Card> result = new ArrayList<>();
                    for (int j=0; j<planeCount*3; j++) {
                        Card card = cards.get(i+j);
                        if (!cardNumbers.contains(card.number)) {
                            cardNumbers.add(card.number);
                        }
                        result.add(card);
                    }
                    // find wings
                    List<Card> wings;
                    int wingsCount = 0;
                    int indexDelta = 0;
                    if ((compareCards.size() - comparePlaneCount * 3) / comparePlaneCount == 0) {

                    } else if ((compareCards.size() - comparePlaneCount * 3) / comparePlaneCount == 1) {
                        wings = findOneCard(cards);
                        for (int j=wings.size()-1; j>=0 && wingsCount<comparePlaneCount; j--) {
                            if (!cardNumbers.contains(wings.get(j).number)) {
                                result.add(result.size()-indexDelta, wings.get(j));
                                wingsCount++;
                                indexDelta++;
                            }
                        }
                    } else if ((compareCards.size() - comparePlaneCount * 3) / comparePlaneCount == 2) {
                        wings = findTwoCard(cards);
                        for (int j=wings.size()-1; j>=1 && wingsCount<comparePlaneCount; j=j-2) {
                            if (!cardNumbers.contains(wings.get(j).number)) {
                                result.add(result.size()-indexDelta, wings.get(j));
                                result.add(result.size()-indexDelta, wings.get(j-1));
                                wingsCount++;
                                indexDelta += 2;
                            }
                        }
                    }
                    if (result.size() == compareCards.size()) {
                        return result;
                    }
                }
            } else if (lastPlaneNumber == -1 || cards.get(i).number > lastPlaneNumber + 1){
                planeCount = 0;
                lastPlaneNumber = -1;
            }
        }
        return null;
    }

    private static List<Card> findFour(ArrayList<Card> cards, ArrayList<Card> compareCards) {
        HashSet<Integer> cardNumbers = new HashSet<>();
        for (int i=cards.size()-1; i>=2; i--) {
            if (cards.get(i).number >= 49) {
                break;
            }
            // find body
            if (cards.get(i).number == cards.get(i-1).number &&
                    cards.get(i).number == cards.get(i-2).number &&
                    cards.get(i).number == cards.get(i-3).number) {
                ArrayList<Card> result = new ArrayList<>();
                for (int j=3; j>=0; j--) {
                    Card card = cards.get(i-j);
                    if (!cardNumbers.contains(card.number)) {
                        cardNumbers.add(card.number);
                    }
                    result.add(card);
                }
                // find wings
                List<Card> wings;
                int wingsCount = 0;
                int indexDelta = 0;
                if (compareCards.size() == 6) {
                    wings = findOneCard(cards);
                    for (int j=wings.size()-1; j>=0 && wingsCount<2; j--) {
                        if (!cardNumbers.contains(wings.get(j).number)) {
                            result.add(result.size()-indexDelta, wings.get(j));
                            wingsCount++;
                            indexDelta++;
                        }
                    }
                } else if (compareCards.size() == 8) {
                    wings = findTwoCard(cards);
                    for (int j=wings.size()-1; j>=1 && wingsCount<2; j=j-2) {
                        if (!cardNumbers.contains(wings.get(j).number)) {
                            result.add(result.size()-indexDelta, wings.get(j));
                            result.add(result.size()-indexDelta, wings.get(j-1));
                            wingsCount++;
                            indexDelta += 2;
                        }
                    }
                }
                if (result.size() == compareCards.size()) {
                    return result;
                }
            }
        }
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

    private static List<Card> findTwoCard(ArrayList<Card> cards) {
        HashSet<Integer> set = new HashSet<>();
        ArrayList<Card> result = new ArrayList<>();
        for (int i=0; i<cards.size()-1; i++) {
            if (set.contains(cards.get(i).number)) {
                continue;
            }
            if (cards.get(i).number == cards.get(i+1).number) {
                set.add(cards.get(i).number);
                result.add(cards.get(i));
                result.add(cards.get(i+1));
            }
        }
        return result;
    }

    private static List<Card> findOneCard(ArrayList<Card> cards) {
        HashSet<Integer> set = new HashSet<>();
        ArrayList<Card> result = new ArrayList<>();
        for (int i=0; i<cards.size()-1; i++) {
            if (set.contains(cards.get(i).number)) {
                continue;
            }
            set.add(cards.get(i).number);
            result.add(cards.get(i));
        }
        return result;
    }

    private static ArrayList<Card> stackToArrayList(Stack<Card> stack) {
        ArrayList<Card> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }
}

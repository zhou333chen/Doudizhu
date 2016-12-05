package Game;

import Model.Card;
import Model.Cards;

import static Model.Cards.*;

/**
 * Created by jintiandalegehu on 2016/12/1.
 */
public class GameChecker {

    public static void check(Cards cards) {
        analyse(cards);
    }

    public static boolean compare(Cards lastCards, Cards currentCards) {
        if ((lastCards == null || lastCards.count == 0) && currentCards.count > 0) {
            return true;
        }
        if (currentCards.type == TypeKingBomb) {
            return true;
        } else if (currentCards.type == TypeNormalBomb &&
                lastCards.type != TypeKingBomb &&
                lastCards.type != TypeNormalBomb){
            return true;
        } else if (lastCards.type == currentCards.type){
            if (lastCards.count > 0 && currentCards.count > 0 && lastCards.count == currentCards.count) {
                return currentCards.get(0).number > lastCards.get(0).number;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

    private static void analyse(Cards cards) {
        if (cards == null) {
            return;
        }
        for (int i=0; i<cards.count; i++) {
            Card card = cards.get(i);
            if (card.number < 53) {
                card.number = (card.index - 1)/ 4;
            }
        }
        if (checkKingBomb(cards) ||
                checkNormalBomb(cards) ||
                checkStraight(cards) ||
                checkCompany(cards) ||
                checkPlane(cards) ||
                checkFour(cards) ||
                checkThree(cards) ||
                checkDouble(cards) ||
                checkSingle(cards)) {
            String str = "";
            switch (cards.type) {
                case TypeKingBomb:
                    str = "王炸";
                    break;
                case TypeNormalBomb:
                    str = "普通炸";
                    break;
                case TypeStraight:
                    str = "顺子";
                    break;
                case TypeCompany:
                    str = "连队";
                    break;
                case TypePlane:
                    str = "飞机";
                    break;
                case TypeFour:
                    str = "四带二";
                    break;
                case TypeThree:
                    str = "三带";
                    break;
                case TypeDouble:
                    str = "对子";
                    break;
                case TypeSingle:
                    str = "单张";
                    break;
                default:
                    break;
            }
            System.out.println("牌型：" + str);
        } else {
            cards.type = TypeIllegal;
            System.out.println("牌型不合法");
        }
    }

    private static boolean checkKingBomb(Cards cards) {
        if (cards.count == 2 &&
                cards.get(0).index == 54 &&
                cards.get(1).index == 53) {
            cards.type = TypeKingBomb;
            return true;
        } else {
            return false;
        }
    }

    private static boolean checkNormalBomb(Cards cards) {
        if (cards.count == 4) {
            for (int i=0; i<3; i++) {
                if (cards.get(i).number != cards.get(i+1).number) {
                    return false;
                }
            }
            cards.type = TypeNormalBomb;
            return true;
        }
        return false;
    }

    private static boolean checkStraight(Cards cards) {
        if (cards.count >= 5) {
            for (int i=0; i<cards.count-1; i++) {
                if (cards.get(i).number != cards.get(i+1).number + 1) {
                    return false;
                }
            }
            cards.type = TypeStraight;
            return true;
        }
        return false;
    }

    private static boolean checkCompany(Cards cards) {
        if (cards.count >= 6 && cards.count % 2 == 0) {
            for (int i=0; i<cards.count; i=i+2) {
                if (cards.get(i).number != cards.get(i+1).number ||
                        (i < cards.count - 3 && cards.get(i).number != cards.get(i+2).number + 1)) {
                    return false;
                }
            }
            cards.type = TypeCompany;
            return true;
        }
        return false;
    }

    private static boolean checkPlane(Cards cards) {
        if (cards.count >= 6) {
            int planeCount = 0;
            for (int i=0; i<=cards.count-3; i=i+3) {
                if (i+1 < cards.count &&
                        i+2 < cards.count &&
                        cards.get(i).number == cards.get(i+1).number &&
                        cards.get(i).number == cards.get(i+2).number) {
                    planeCount++;
                } else {
                    break;
                }
            }
            // 判断飞机带的牌是否符合要求
            if (planeCount > 0) {
                int index = planeCount * 3;
                if (cards.count == planeCount * 3) {
                    // 不带牌
                    cards.type = TypePlane;
                    return true;
                } else if (cards.count == planeCount * 4) {
                    // 三带一
                    cards.type = TypePlane;
                    return true;
                } else if (cards.count == planeCount * 5){
                    // 三带二
                    for (; index<planeCount*2; index=index+2) {
                        if (cards.get(index).number != cards.get(index+1).number) {
                            return false;
                        }
                    }
                    cards.type = TypePlane;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkFour(Cards cards) {
        if (cards.count == 6 || cards.count == 8) {
            if (cards.get(0).number == cards.get(1).number &&
                    cards.get(0).number == cards.get(2).number &&
                    cards.get(0).number == cards.get(3).number) {
                int index = 4;
                if (cards.count == 6) {
                    cards.type = TypeFour;
                    return true;
                } else if (cards.count == 8) {
                    if (cards.get(index).number == cards.get(index+1).number &&
                            cards.get(index+2).number == cards.get(index+3).number) {
                        cards.type = TypeFour;
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private static boolean checkThree(Cards cards) {
        if (cards.count >= 3 && cards.count <=5) {
            if (cards.get(0).number == cards.get(1).number &&
                    cards.get(0).number == cards.get(2).number) {
                int index = 3;
                if (cards.count == 3) {
                    cards.type = TypeThree;
                    return true;
                } else if (cards.count == 4){
                    cards.type = TypeThree;
                    return true;
                } else {
                    if (cards.get(index).number == cards.get(index+1).number) {
                        cards.type = TypeThree;
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private static boolean checkDouble(Cards cards) {
        if (cards.count == 2) {
            if (cards.get(0).number == cards.get(1).number) {
                cards.type = TypeDouble;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private static boolean checkSingle(Cards cards) {
        if (cards.count == 1) {
            cards.type = TypeSingle;
            return true;
        }
        return false;
    }
}

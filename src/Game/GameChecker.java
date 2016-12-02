package Game;

import Model.Card;
import Model.Cards;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by jintiandalegehu on 2016/12/1.
 */
public class GameChecker {
    public final static int TypeNormal = 0;     // 手牌
    public final static int TypeKingBomb = 1;   // 王炸
    public final static int TypeNormalBomb = 2; // 普通炸弹
    public final static int TypeStraight = 3;   // 顺子
    public final static int TypeCompany = 4;    // 连队
    public final static int TypePlane = 5;      // 飞机
    public final static int TypeFour = 6;       // 四带二
    public final static int TypeThree = 7;      // 三带
    public final static int TypeDouble = 8;     // 对子
    public final static int TypeSingle = 9;     // 单张
    public final static int TypeIllegal = 10;   // 不合法的牌

    public static boolean check(Cards lastCards, Cards currentCards) {
        analyse(lastCards);
        analyse(currentCards);

        return false;
    }

    private static void analyse(Cards cards) {
        if (cards == null) {
            return;
        }
        cards.sort();
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
                cards.get(0).index == 53 &&
                cards.get(1).index == 54) {
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
                if (cards.get(i).number != cards.get(i+1).number - 1) {
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
                        (i < cards.count - 2 && cards.get(i).number != cards.get(i+2).number - 1)) {
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
                if (cards.get(i).number == cards.get(i+1).number &&
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
            int flag = 0;   // 1代表飞机在前面，2代表飞机在后面
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

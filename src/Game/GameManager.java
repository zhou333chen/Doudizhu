package Game;

import Model.Card;
import Model.Cards;

import java.util.*;

import static Game.GameOperation.*;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
public class GameManager implements Gamer.GamerListener{
    ArrayList<Gamer> gamers = new ArrayList<>(3);
    ArrayList<Card> cards;
    Cards lastPlayCards;
    Cards currentPlayCards = new Cards();
    Gamer lastPlayGamer;    // 上一个出牌的人
    Gamer currentGamer;
    int currentGamerIndex;

    public void addGamer(Gamer gamer) {
        gamers.add(gamer);
        gamer.listener = this;
        gamer.start();
    }

    private void startGame() {
        System.out.println("start game");
        // 洗牌
        currentGamer = gamers.get((int)(Math.random()*3));
        deal();
    }

    private void deal() {
        // 随机分配牌组
        cards = new ArrayList<>();
        for (int i=0;i<54;i++) {
            Card card = new Card();
            card.index = i+1;
            cards.add(card);
        }
        Collections.shuffle(cards);
        // 随机产生叫地主的人
        currentGamerIndex = 0;//(int)(Math.random() * 3);
        currentGamer = gamers.get(currentGamerIndex);
        // 发票
        for (int i=0; i<3; i++) {
            // 发牌
            Gamer gamer = gamers.get(i);
            gamer.cards = new Cards();
            StringBuilder sb = new StringBuilder(DEAL_OPERATION + "|");
            for (int j=0; j<3; j++) {
                sb.append(gamers.get((i + j) % 3).user.userId + "&");
            }
            sb.append(currentGamer.user.userId + "|");
            for (int j=0; j<17; j++) {
                Card card = cards.get(i * 17 + j);
                sb.append(card.index + "&");
                gamer.cards.addCard(card);
            }
            sb.deleteCharAt(sb.length()-1);
            gamer.notifyInfo(sb.toString());
        }

        if (currentGamer.isAuto) {
            Timer timer1 = new Timer();
            timer1.schedule(new TimerTask() {
                public void run() {
                    handleConfirmLandlord(generateConfirm(currentGamer));
                }
            }, 3000);

            Timer timer2 = new Timer();
            timer2.schedule(new TimerTask() {
                public void run() {
                    handlePlayCard(generatePlayCard(currentGamer, currentGamer.cards.subCards(0, 1)));
                }
            }, 6000);
        }
    }

    @Override
    public void receiveOperation(String operation) {
        if (operation.startsWith(LOGIN_OPERATION)) {
            // 所有用户都连接上了
            handleLogin();
        } else if (operation.startsWith(CONFIRM_OPERATION)){
            handleConfirmLandlord(operation);
        } else if (operation.startsWith(PLAY_OPERATION)) {
            handlePlayCard(operation);
        } else if (operation.startsWith(PASS_LANDLORD_OPERATION)) {
            handlePassLandlord(operation);
        }
    }

    private void handleLogin() {
        if (gamers.size() == 3 &&
                gamers.get(0).user.userId != null &&
                gamers.get(1).user.userId != null &&
                gamers.get(2).user.userId != null) {
            startGame();
        }
    }

    private void handleConfirmLandlord(String str) {
        String[] strs = str.split("\\|");
        if (strs.length == 2) {
            String id = strs[1];
            if (id.equals(currentGamer.user.userId)) {
                List<Card> bottomCards = cards.subList(51, 54);
                currentGamer.cards.addCards(bottomCards);
                for (Gamer gamer : gamers) {
                    gamer.notifyInfo(str + "|" +
                            bottomCards.get(0).index + "&" +
                            bottomCards.get(1).index + "&" +
                            bottomCards.get(2).index);
                }
            }
        }
    }

    private void handlePlayCard(String str) {
        currentPlayCards.removeAllCards();

        String[] strs = str.split("\\|");
        if (strs.length == 3) {
            String id = strs[1];
            if (id.equals(currentGamer.user.userId)) {
                String[] cardStrs = strs[2].split("&");
                for (int i=0; i<cardStrs.length; i++) {
                    Card card = new Card();
                    card.index = Integer.valueOf(cardStrs[i]);
                    currentPlayCards.addCard(card);
                }
                // 第一张牌必须出
                if (lastPlayGamer == null && currentPlayCards.count == 0) {
                    return;
                }
                // 检查出牌是否合理
                if (lastPlayGamer != null && currentGamer != lastPlayGamer && !GameChecker.check(lastPlayCards, currentPlayCards)) {
                    currentGamer.notifyInfo(ILLEGAL_OPERATION + "|" + currentGamer.user.userId);
                    return;
                }
                // 更新手牌
                currentGamer.cards.removeAllCards(currentPlayCards);
                return;
            }
        }
        // 群发消息
        if (lastPlayCards == null) {
            lastPlayCards = new Cards();
        } else {
            lastPlayCards.removeAllCards();
        }
        lastPlayCards.addCards(currentPlayCards);
        for (Gamer gamer : gamers) {
            gamer.notifyInfo(str);
        }
        currentGamerIndex = (currentGamerIndex + 1) % 3;
        lastPlayGamer = currentGamer;
        currentGamer = gamers.get(currentGamerIndex);
        if (currentGamer.isAuto) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    handlePlayCard(generatePlayCard(currentGamer, currentGamer.cards.subCards(0, 1)));
                }
            }, 500);
        }
    }

    private void handlePassLandlord(String str) {

    }

    private String generateConfirm(Gamer gamer) {
        System.out.println("auto confirm");
        return CONFIRM_OPERATION + "|" + gamer.user.userId;
    }

    private String generatePlayCard(Gamer gamer, List<Card> cards) {
        System.out.println("auto play");
        StringBuffer sb = new StringBuffer();
        sb.append(PLAY_OPERATION + "|" + gamer.user.userId + "|");
        for (Card card : cards) {
            sb.append(card.index + "&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String generatePassLandlord(Gamer gamer) {
        System.out.println("auto pass");
        return "";
    }
}

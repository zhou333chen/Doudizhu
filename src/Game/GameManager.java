package Game;

import Model.Card;
import Model.Cards;

import java.util.*;

import static Game.GameOperation.*;
import static Model.Cards.TypeIllegal;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
public class GameManager implements Gamer.GamerListener{
    public ArrayList<Gamer> gamers = new ArrayList<>(3);
    private ArrayList<Card> cards;
    private Cards lastPlayCards;
    private Cards currentPlayCards;
    private Gamer lastPlayGamer;    // 上一个出牌的人
    private Gamer currentGamer;
    private int currentGamerIndex;
    private int passCount;
    private TimerTask confirmLandlordTimerTask;
    private TimerTask playTimerTask;

    public void addGamer(Gamer gamer) {
        gamers.add(gamer);
        gamer.listener = this;
        gamer.start();
    }

    private void startGame() {
        System.out.println("start game");
        // 初始化
        if (confirmLandlordTimerTask != null) {
            confirmLandlordTimerTask.cancel();
        }
        if (playTimerTask != null) {
            playTimerTask.cancel();
        }
        lastPlayCards = new Cards();
        currentPlayCards = new Cards();
        lastPlayGamer = null;
        currentGamer = null;
        currentGamerIndex = -1;
        passCount = 0;
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
            if (card.index < 53) {
                card.number = (card.index - 1)/ 4;
            }
            cards.add(card);
        }
//        Collections.shuffle(cards);
        // 随机产生叫地主的人
        currentGamerIndex = (int)(Math.random() * 3);
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
            gamer.cards.sort();
            sb.deleteCharAt(sb.length()-1);
            gamer.notifyInfo(sb.toString());
        }

        if (currentGamer.isAuto) {
            autoLandlord(currentGamer);
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
                // 检查出牌是否合理
                GameChecker.check(currentPlayCards);
                if (currentPlayCards.type != TypeIllegal && GameChecker.compare(lastPlayCards, currentPlayCards)) {
                    currentGamer.cards.removeAllCards(currentPlayCards);
                } else {
                    currentGamer.notifyInfo(ILLEGAL_OPERATION + "|" + currentGamer.user.userId);
                    return;
                }
            }
        }
        // 统计有几次不要，如果有两次连续的不要，则新开一轮
        if (passCount == 2 && currentPlayCards.count == 0) {
            currentGamer.notifyInfo(ILLEGAL_OPERATION + "|" + currentGamer.user.userId);
            return;
        }
        if (currentPlayCards.count == 0) {
            passCount++;
            if (passCount == 2) {
                lastPlayCards.removeAllCards();
            }
        } else {
            passCount = 0;
        }
        // 第一张牌必须出
        if (lastPlayGamer == null && currentPlayCards.count == 0) {
            return;
        }

        if (currentPlayCards.count > 0) {
            lastPlayCards.removeAllCards();
            lastPlayCards.addCards(currentPlayCards);
            lastPlayCards.type = currentPlayCards.type;
        }

        for (Gamer gamer : gamers) {
            gamer.notifyInfo(str);
        }
        if (currentGamer.cards.count == 0) {
            return;
        }
        currentGamerIndex = (currentGamerIndex + 1) % 3;
        lastPlayGamer = currentGamer;
        currentGamer = gamers.get(currentGamerIndex);
        if (currentGamer.isAuto) {
            Timer timer = new Timer();
            playTimerTask = new TimerTask() {
                @Override
                public void run() {
                    handlePlayCard(Robort.generatePlayCard(currentGamer, lastPlayCards));
                }
            };
            timer.schedule(playTimerTask, 1000);
        }
    }

    private void handlePassLandlord(String str) {
        String[] strs = str.split("\\|");
        if (strs.length == 2 && strs[1].equals(currentGamer.user.userId)) {
            currentGamerIndex = (currentGamerIndex + 1) % 3;
            currentGamer = gamers.get(currentGamerIndex);
            for (Gamer gamer : gamers) {
                gamer.notifyInfo(PASS_LANDLORD_OPERATION + "|" + currentGamer.user.userId);
            }
            if (currentGamer.isAuto) {
                autoLandlord(currentGamer);
            }
        }
    }

    private void autoLandlord(Gamer gamer) {
        Timer timer1 = new Timer();
        confirmLandlordTimerTask = new TimerTask() {
            @Override
            public void run() {
                handleConfirmLandlord(Robort.generateConfirm(gamer));
            }
        };
        timer1.schedule(confirmLandlordTimerTask, 3000);

        Timer timer2 = new Timer();
        playTimerTask = new TimerTask() {
            @Override
            public void run() {
                handlePlayCard(Robort.generatePlayCard(gamer, null));

            }
        };
        timer2.schedule(playTimerTask, 6000);
    }
}

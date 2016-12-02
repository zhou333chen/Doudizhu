package Game;

import Model.Card;
import Model.Cards;
import Model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static Game.GameOperation.*;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
public class Gamer implements Runnable {
    public Socket socket;
    public User user;
    public int isLandlord;
    public boolean isAuto;
    public Cards cards;
    public GamerListener listener;
    private BufferedReader in;
    private BufferedWriter out;

    public interface GamerListener {
        void receiveOperation(String operation);
    }

    public void start() {
        try {
            if (socket != null) {
                Thread thread = new Thread(this);
                thread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if (socket != null) {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                user = new User();
                String info = null;
                while ((info = in.readLine()) != null) {
                    System.out.println("from Client " + user.userId + ": " + info);
                    if (info.startsWith(LOGIN_OPERATION)) {
                        String[] args = info.split("\\|");
                        if (args.length > 1) {
                            user.userId = args[1];
                        }
                    }
                    listener.receiveOperation(info);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyInfo(String info) {
        try {
            if (out != null) {
                System.out.println("to Client:" + user.userId + ": " + info);
                out.write(info);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

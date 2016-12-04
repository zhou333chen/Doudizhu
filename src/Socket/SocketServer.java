package Socket;

import Game.GameManager;
import Game.Gamer;
import Model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
public class SocketServer {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<GameManager> managers = new ArrayList<>();

    public SocketServer()
    {
        try {
            serverSocket = new ServerSocket(9527);

            while(true) {
                socket = serverSocket.accept();
                System.out.println("user login");

                GameManager gameManager;
                if (managers.size() > 0 && managers.get(managers.size()-1).gamers.size() < 3) {
                    gameManager = managers.get(managers.size()-1);
                } else {
                    gameManager = new GameManager();
                    managers.add(gameManager);
                }

                managers.add(gameManager);
                Gamer gamer = new Gamer();  // 创建玩家
                gamer.socket = socket;
                gameManager.addGamer(gamer);

                Gamer computer1 = new Gamer();  // 创建电脑1
                computer1.isAuto = true;
                computer1.user = new User();    // mock数据
                computer1.user.userId = "2";
                gameManager.addGamer(computer1);

                Gamer computer2 = new Gamer();  // 创建电脑2
                computer2.isAuto = true;
                computer2.user = new User();    // mock数据
                computer2.user.userId = "3";
                gameManager.addGamer(computer2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close()  {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("socket server closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package Socket;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
public class SocketThread extends Thread{

    SocketServer socketServer;
    @Override
    public void run() {
        socketServer = new SocketServer();
    }

    public void close() {
        socketServer.close();
    }
}

package Socket;

/**
 * Created by jintiandalegehu on 2016/11/27.
 */
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 将socket service随tomcat启动
 * @author zhangzhongwen
 *
 */
public class SocketServiceLoader implements ServletContextListener{
    //socket server 线程
    private SocketThread socketThread;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        if(null!=socketThread && !socketThread.isInterrupted())
        {
            socketThread.close();
            socketThread.interrupt();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        if(null==socketThread)
        {
            //新建线程类
            socketThread=new SocketThread();
            //启动线程
            socketThread.start();
        }
    }

}

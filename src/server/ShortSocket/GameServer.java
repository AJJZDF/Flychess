package server.ShortSocket;

import protocol.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gitfan on 4/15/18.
 */
public class GameServer {

    Map<String,Room> rooms = new HashMap<>();//房间
    Map<String, GameThread> gameThreads = new HashMap<>();//所有的游戏控制线程


    //返回在线的房间给客户端:vector<string>的形式
    private void send_msg_online_rooms(InetAddress address) throws IOException {

        //连接到手机客户端
        Socket socket = new Socket(address, UnifiedStandard.CLIENT_PORT);

        System.out.println("send to " + socket.getInetAddress().toString());

        //在线房间
        Vector<String> online_rooms = new Vector<String>();

        //所有的房间名
        for(String roomname: rooms.keySet())
        {
            online_rooms.add(roomname);
        }

        //发送给客户端的消息
        Protocol msg = new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        msg.set_msgUnity(new MsgUnity(MsgUnity.S2C_ROOM_LIST),online_rooms);

        //protocol对象序列化传送
        Protocol.socketSerilize(socket,msg);

        //及时关闭连接
        socket.close();

        System.out.println("finish send");
    }

    //为每个连进来的socket服务
    private void do_server(Socket socket) throws IOException {

        System.out.println(socket.getInetAddress());

        Protocol protocol = Protocol.socketUnSerilize(socket);

        System.out.println(protocol);

        switch (protocol.getMsg_type())
        {
            case Protocol.MSG_TYPE_GAME_MSG_FROM_CLIENT:
            {

                MsgLogic msgLogic = protocol.get_msgLogic();

                switch (msgLogic.getType())
                {
                    //请求在线的房间，返回有有哪些房间在线
                    case MsgLogic.C2S_REQUEST_ONLINE_ROOM:
                    {
                        send_msg_online_rooms(socket.getInetAddress());
                        break;
                    }

                    default:
                        break;
                }


                break;
            }
            default:
                break;
        }


    }

    class ChildThread extends Thread
    {
        Socket socket;

        ChildThread(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run() {

            //只做简单的一次性的任务

            try {
                do_server(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //结束连接
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startServer() throws IOException {
        // 监听指定的端口
        ServerSocket server = new ServerSocket(UnifiedStandard.SERVER_PORT);
        // server将一直等待连接的到来
        System.out.println("server将一直等待连接的到来");

        //如果使用多线程，那就需要线程池，防止并发过高时创建过多线程耗尽资源
        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        while (true) {

            Socket socket = server.accept();

            threadPool.submit(new ChildThread(socket));
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println(InetAddress.getLocalHost());//获得本机IP);

        GameServer gameServer = new GameServer();
        gameServer.rooms.put("hello",new Room("hello"));
        gameServer.rooms.put("world",new Room("world"));
        gameServer.rooms.put("lucky",new Room("lucky"));

        gameServer.startServer();
    }
}

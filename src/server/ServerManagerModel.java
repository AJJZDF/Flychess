package server;

import protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class ServerManagerModel {


    private Vector<MyClient> vec_clients;              //存储已经accept的client
    private Map<Socket,Integer> map_client2Index;   //映射socket到Vector中的index

    private ServerSocket serverSocket;                  //绑定端口

    /*
    1.自动匹配功能：申请了‘匹配’命令的人会被加入一个列表，在该列表中组成四人房间
    2.房间功能：
                client发送刷新命令（获取当前的房间列表）给server
                client收到房间列表之后，一旦点击，则可以发送申请加入的消息。超过三个人的时候返回加入失败的信息，房间已满或者已经开始游戏返回相应信息。

     */

    class MyClient extends Thread{
        private Socket client;

        public volatile boolean exitRecvThread =false;
        private BufferedReader is ;
        private PrintWriter os ;

        MyClient(Socket i){
            this.client=i;

            try {
                //初始化输入输出流
                is = new BufferedReader(new InputStreamReader(client.getInputStream()));
                os = new PrintWriter(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override   //Recv线程  (recv from client)
        public void run() {
            while (!exitRecvThread) {

                //recv protocol(msg from server)
                String msg;

                try {
                    if (is.ready()) {
                        msg = is.readLine();
                        if (!msg.equals("")) {
                            System.out.println("msg from server:"+msg);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //sendToClient

            }
        }
        //用于退出recv线程
        public void exitRecvThread(){
            exitRecvThread=true;
        }



        public void sendToClient(String msg){
            //后期改造成protocol对象序列化传送
            os.print(msg);
            os.flush();
        }

        public void send2Client(Protocol msg){
            //protocol对象序列化传送
            Protocol.socketSerilize(client,msg);
        }
    }


    ServerManagerModel(){
        vec_clients=new Vector<MyClient>();
        map_client2Index=new HashMap<Socket,Integer>();

        init();
    }
    void init(){
        try {
            serverSocket = new ServerSocket(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void run(){
        Socket client=null;
        while(true){
            try {
                //收到一个client，首先将其push进去vec列表，
                //然后新建client_recv_thread去创建接受进程
                client = serverSocket.accept();

                MyClient clientTmp=new MyClient(client);


                vec_clients.add(clientTmp);
                map_client2Index.put(client,new Integer(vec_clients.size()-1));


                clientTmp.start();

                BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));

                String inputString="Hello,tcp client\n";

                Protocol msg=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);
                msg.set_msgFromServer(Protocol.MsgFromServer.ChessToChoose);

                clientTmp.send2Client(msg);
                //clientTmp.sendToClient(inputString);


                clientTmp.send2Client(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        ServerManagerModel serverManagerTest=new ServerManagerModel();
        serverManagerTest.run();

    }
}

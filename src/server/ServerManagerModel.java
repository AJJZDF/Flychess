package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ServerManagerModel {


    private Vector<MyClient> vec_clients;              //存储已经accept的client
    private Map<Socket,Integer> map_client2Index;   //映射socket到Vector中的index

    private ServerSocket serverSocket;                  //绑定端口

    /*
    1.自动匹配功能：申请了‘匹配’命令的人会被加入一个列表，在该列表中组成四人房间
    2.房间功能：
                服务器每隔几秒发送当前房间列表
                client收到房间列表之后，一旦点击，则可以发送申请加入的消息。超过三个人的时候返回加入失败的信息，房间已满或者已经开始游戏返回相应信息。

     */

    class MyClient extends Thread{
        private Socket client;//client 和他的接收线程的生命周期一致

        public volatile boolean exitRecvThread =false;
        private BufferedReader is ;
        private PrintWriter os ;

        @Override   //Recv线程  (recv from client)
        public void run() {
            while (!exitRecvThread) {

                //recv protocol(msg from client)

                //send

            }
        }
        public void exitRecvThread(){
            exitRecvThread=true;
        }

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

        public void send(String msg){
            //后期改造成protocol对象序列化传送
            os.print(msg);
            os.flush();
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

                //add lock 多线程可能会产生return的size不一致
                vec_clients.add(clientTmp);
                map_client2Index.put(client,new Integer(vec_clients.size()-1));
                //release lock


                BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));

                String inputString="Hello,tcp client\n";

                clientTmp.send(inputString);

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

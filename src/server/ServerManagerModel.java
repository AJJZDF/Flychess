package server;

import protocol.Protocol;
import protocol.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class ServerManagerModel {

    private Vector<MyClient> vec_clients;                //存储已经accept的client
    private Map<Socket,Integer> map_client2Index;        //映射socket到Vector中的index

    private Map<String ,Room> map_roomName2Room;           //存储roomlist

    private ServerSocket serverSocket;                  //绑定端口

    private HashMap<MyClient,Protocol> msgBuffer;//使用类似于假脱机？（nio？）的思想，每个thread收到消息，通过public void synchor putMsg(){]
                                                    //到信箱msgBuffer，server多开一个线程（deal with msgbuffer）去while true sleep处理信箱内容，处理完就清空
                                                    //疑问，如果处理的过程中新加入了msg，怎么版（只给这一个变量枷锁lock），【学霸林说同步块？】


    /*
    1.自动匹配功能：申请了‘匹配’命令的人会被加入一个列表，在该列表中组成四人房间
    2.房间功能：
                client发送刷新命令（获取当前的房间列表）给server
                client收到房间列表之后，一旦点击，则可以发送申请加入的消息。超过三个人的时候返回加入失败的信息，房间已满或者已经开始游戏返回相应信息。

     */
//```````````房间function
    public boolean checkIfExistRoomNamed(String roomName){
        if(map_roomName2Room.containsKey(roomName)){

            return true;}
        else return false;
    }
//```````````房间function


    ServerManagerModel(){
        vec_clients=new Vector<MyClient>();
        map_client2Index=new HashMap<Socket,Integer>();
        map_roomName2Room=new HashMap<>();


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

                client = serverSocket.accept();

                MyClient clientTmp=new MyClient(client);
                vec_clients.add(clientTmp);
                map_client2Index.put(client,new Integer(vec_clients.size()-1));
                //然后新建client_recv_thread  接收线程
                clientTmp.start();

                //BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));

                Protocol testMsg=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);
                testMsg.set_game_msgFromServer(new GameMsgFromServer(GameMsgFromServer.NOW_YOUR_COLOR_HAVE_BEEN_INIT),5);
                clientTmp.send2Client(testMsg);


                sendTest(clientTmp);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendTest(MyClient tmp){

        Vector<Protocol> testExamples=new Vector<>();
        Protocol tmp1=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);
        tmp1.set_game_msgFromServer(new GameMsgFromServer(GameMsgFromServer.NOW_YOUR_COLOR_HAVE_BEEN_INIT),2);
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);
        tmp1.set_game_msgFromServer(new GameMsgFromServer(GameMsgFromServer.WHOSE_TURN),3);
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);
        Vector<Integer> selectableChess=new Vector<>();
        selectableChess.add(new Integer(0));
        selectableChess.add(new Integer(1));
        selectableChess.add(new Integer(3));
        tmp1.set_game_msgFromServer(new GameMsgFromServer(GameMsgFromServer.CHESS_TO_CHOOSE),selectableChess,2);
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);
        tmp1.set_game_msgFromServer(new GameMsgFromServer(GameMsgFromServer.REQUEST_DICE),3);//msgPass 为 playerId
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);
        Vector<String > logicSeri=new Vector<>();
        logicSeri.add("不哄我");
        logicSeri.add("是吗");
        logicSeri.add("那就不红吧");
        tmp1.set_game_msgFromServer(new GameMsgFromServer(GameMsgFromServer.LOGIC_SERIAL),logicSeri);
        testExamples.add(tmp1);

        //---------------
        tmp1=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_CLIENT);
        tmp1.set_game_msgFromClient(new GameMsgFromClient(GameMsgFromClient.DICE_NUM),6);
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_CLIENT);
        tmp1.set_game_msgFromClient(new GameMsgFromClient(GameMsgFromClient.THE_CHESS_WHO_CHOOSE),2);
        testExamples.add(tmp1);


        //--------------------
        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_MSG);
        tmp1.set_msgLogic(new MsgLogic(MsgLogic.C2S_CLIENT_TRY_JOIN_ROOM),"roommmmmmmid");
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_MSG);
        tmp1.set_msgLogic(new MsgLogic(MsgLogic.C2S_INROOM_TRY_GAME_START));
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_MSG);
        tmp1.set_msgLogic(new MsgLogic(MsgLogic.C2S_INROOM_QUIT_ROOM),"quit usr is me");
        testExamples.add(tmp1);

        //-----------------
        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        //set room list
        Vector<String > roomlist=new Vector<>();
        roomlist.add("room:不哄我");
        roomlist.add("room:是吗");
        roomlist.add("room:那就不红吧");
        tmp1.set_msgUnity(new MsgUnity(MsgUnity.S2C_ROOM_LIST),roomlist);
        testExamples.add(tmp1);


        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        tmp1.set_msgUnity(new MsgUnity(MsgUnity.S2C_JOIN_ROOM_SUCCESS));
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        tmp1.set_msgUnity(new MsgUnity(MsgUnity.S2C_JOIN_ROOM_FAIL));
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        //set room person
        Vector<String > roomPerson=new Vector<>();
        roomPerson.add("Cmhowl");
        roomPerson.add("AJJDF");
        roomPerson.add("CJJ");
        tmp1.set_msgUnity(new MsgUnity(MsgUnity.S2C_INROOM_ROOM_PERSONS),roomPerson);
        testExamples.add(tmp1);


        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        tmp1.set_msgUnity(new MsgUnity(MsgUnity.S2C_INROOM_GAME_START));
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        tmp1.set_msgUnity(new MsgUnity(MsgUnity.S2C_INROOM_SB_QUIT_ROOM),"cmhowl");
        testExamples.add(tmp1);

        tmp1=new Protocol(Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI);
        tmp1.set_msgUnity(new MsgUnity(MsgUnity.S2C_INROOM_GAME_FINISH));
        testExamples.add(tmp1);



        //----------start to send
        for (int i = 0; i <testExamples.size() ; i++) {
            tmp.send2Client(testExamples.elementAt(i));
        }
    }

    public static void main(String[] args) {
        ServerManagerModel serverManagerTest=new ServerManagerModel();
        serverManagerTest.run();

    }
}
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
            Protocol recvMsg = Protocol.socketUnSerilize(client);
            if(recvMsg!=null)
                System.out.println(recvMsg);

            else System.out.println("recv null");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    //用于退出recv线程
    public void exitRecvThread(){
        exitRecvThread=true;
    }



    public void send2Client(String msg){
        os.print(msg);
        os.flush();
    }

    public void send2Client(Protocol msg){
        //protocol对象序列化传送
        Protocol.socketSerilize(client,msg);
    }

    @Override
    public String toString() {
        return "[ socket client = "
                +client.toString()
                +",exitRecvThread = "
                +exitRecvThread
                +" ]";

    }
}

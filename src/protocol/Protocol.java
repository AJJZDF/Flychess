package protocol;

import java.io.*;
import java.net.Socket;


//msg打包类
public class Protocol implements Serializable {

    public static final int MSG_TYPE_GAME_MSG_FROM_SERVER=0;
    public static final int MSG_TYPE_GAME_MSG_FROM_CLIENT=1;
    public static final int MSG_TYPE_LOGI_MSG = 2;
    public static final int MSG_TYPE_LOGI_UNITY_UPDATE_UI =3;

    private int msg_type;//true represent 现在是server的msg，false代表现在是client的msg

    //server
    //send2Client  谁的回合
    //      可选棋子
    //     逻辑序列
    public enum GameMsgFromServer {
        NowYourColor,           //自身被初始化的颜色
        WhoseTurn,              //谁的回合
        ChessToChoose,          //可选棋子
        LogicSerial             //逻辑序列
    }


    //client
    //sendToServer 筛子数值
    //      选的棋子
    public enum GameMsgFromClient {
        DiceNum,                //筛子数值
        TheChessWhoChoose       //选的棋子
    }


    /*
    房间流程
    server向client发送当前已有房间号  当前房间列表（刷新ui）  S2C_ROOM_LIST
    client发送试图进入哪个房间                                C2S_CLIENT_TRY_JOIN_ROOM
    server返回加入是否成功信息(是 和 否 两条枚举信息)（刷ui） S2C_JOIN_ROOM_SUCCESS/S2C_JOIN_ROOM_FAIL
        client成功加入房间后，server向房间内所有人播报当前在房间里的person（刷新ui）    S2C_ROOM_PERSONS
        client发送退出房间指令                                                          C2S_QUIT_ROOM
        server播报某某某退出房间指令（刷新ui）                                          S2C_SB_QUIT_ROOM
    房主发送，申请开始游戏指令。                              C2S_TRY_GAME_START
    server播报开始游戏指令（刷新ui）                          S2C_GAME_START

    server播报游戏结束（退出游戏ui）                          S2C_SB_QUIT_ROOM
     */
    public enum MsgUnity{//用于unity 更新ui

        //逻辑控制：server向client发送当前已有房间号  当前房间列表（刷新ui）
        S2C_ROOM_LIST,

        //server返回加入是否成功信息（是 和 否 两条枚举信息）（刷ui）
        S2C_JOIN_ROOM_SUCCESS,
        S2C_JOIN_ROOM_FAIL,

        //client成功加入房间后，server向房间内所有人播报当前在房间里的person（刷新ui）
        S2C_ROOM_PERSONS,

        //server播报开始游戏指令（刷新ui）
        S2C_GAME_START,

        //server播报某某某退出房间指令（刷新ui）
        S2C_SB_QUIT_ROOM,

        //server播报游戏结束（退出游戏ui）
        S2C_GAME_FINISH
    }
    public enum MsgLogic {
        //client发送试图进入哪个房间
        C2S_CLIENT_TRY_JOIN_ROOM,

        //房主发送，申请开始游戏指令。
        C2S_TRY_GAME_START,

        //发送退出房间指令
        C2S_QUIT_ROOM;

        //---------------数据
            //Join room number
        public String tryRoomNum=null;
        public String usr=null;

    }


    private GameMsgFromServer _msgFromServer;
    private GameMsgFromClient _game_msgFromClient;
    private MsgLogic _msgLogic;
    private MsgUnity _msgUnity;

    public Protocol(int type){
        assert (type>=0&&type<4);
        msg_type=type;
    }


    public void set_msgFromServer(GameMsgFromServer mfs){
        _msgFromServer=mfs;
    }
    public void set_game_msgFromClient(GameMsgFromClient mfc){
        _game_msgFromClient=mfc;
    }
    public void set_msgLogic(MsgLogic ml){
        _msgLogic=ml;
    }
    public void set_msgUnity(MsgUnity mu){
        _msgUnity=mu;
    }
    public void server_send2Client(){

    }

    public void server_recv(){}

    public void client_send2Server(){}

    public void client_recv(){}


    public String toString(){
        switch (msg_type){
            case Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER:
                return "[ MsgType = GAME_MSG_FROM_SERVER    "
                        +"Intent = "
                        + GameMsgFromServer.values()[_msgFromServer.ordinal()].toString()
                        +" ]";
            case Protocol.MSG_TYPE_GAME_MSG_FROM_CLIENT:
                return "[ MsgType = GAME_MSG_FROM_CLIENT    "
                        +"Intent = "
                        + GameMsgFromClient.values()[_game_msgFromClient.ordinal()].toString()
                        +" ]";
            case Protocol.MSG_TYPE_LOGI_MSG:
                return "[ MsgType = LOGI_MSG    "
                        +"Intent = "
                        +MsgLogic.values()[_msgLogic.ordinal()].toString()
                        +" ]";
            case Protocol.MSG_TYPE_LOGI_UNITY_UPDATE_UI:
                return "[ MsgType = LOGI_UNITY_UPDATE_UI    "
                        +"Intent = "
                        +MsgUnity.values()[_msgLogic.ordinal()].toString()
                        +" ]";

        }

        return "[ MsgNull ]";
    }

    //序列化后直接传送给socket对象
    public static void socketSerilize(Socket socket,Protocol msg){
        try {
            OutputStream outputStream  = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(msg);

            objectOutputStream.flush();
            //objectOutputStream.close(); 会把socket关掉
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Protocol socketUnSerilize(Socket socket){
        Protocol rb=null;
        try {

            InputStream inputStream = socket.getInputStream();
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if (is.ready()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                rb = (Protocol) objectInputStream.readObject();
                return rb;

            }
            else return null;
        }
        catch (ClassNotFoundException e) {
            System.out.println("err 收到客户端的请求不能发序列化到Protocal");
            //e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return rb;
    }
    public static void main(String[] args) {
        Protocol msg=new Protocol(Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER);

        msg.set_msgFromServer(GameMsgFromServer.ChessToChoose);
        System.out.println(msg);
    }
}

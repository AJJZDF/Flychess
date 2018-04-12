package protocol;

import java.io.*;
import java.net.Socket;


//msg打包类
public class Protocol implements Serializable {

    public static final int MSG_TYPE_GAME_MSG_FROM_SERVER=0;
    public static final int MSG_TYPE_GAME_MSG_FROM_CLIENT=1;
    public static final int MSG_TYPE_LOGI_MSG=2;

    private int msg_type;//true represent 现在是server的msg，false代表现在是client的msg

    //server
    //sendToClient  谁的回合
    //      可选棋子
    //     逻辑序列
    public enum MsgFromServer {
        NowYourColor,           //自身被初始化的颜色
        WhoseTurn,              //谁的回合
        ChessToChoose,          //可选棋子
        LogicSerial             //逻辑序列
    }


    //client
    //sendToServer 筛子数值
    //      选的棋子
    public enum MsgFromClient {
        DiceNum,                //筛子数值
        TheChessWhoChoose       //选的棋子
    }



    public enum MsgUnity{

    }
    public enum MsgLogic {//逻辑控制：server向client发送当前已有房间号  当前房间列表（刷新ui）
                        //              client发送试图进入哪个房间
                        //              server返回加入是否成功信息（是 和 否 两条枚举信息）（刷ui）
                        //              client成功加入房间后，server向房间内所有人播报当前在房间里的person（刷新ui）


        //房主发送，申请开始游戏指令。
        // server播报开始游戏指令（刷新ui）
        //发送退出房间指令
        //server播报某某某退出房间指令（刷新ui）
    }


    private MsgFromServer _msgFromServer;
    private MsgFromClient _msgFromClient;
    private MsgLogic _msgLogic;

    public Protocol(int type){
        assert (type>=0&&type<3);
        msg_type=type;
    }


    public void set_msgFromServer(MsgFromServer mfs){
        _msgFromServer=mfs;
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
                        +MsgFromServer.values()[_msgFromServer.ordinal()].toString()
                        +" ]";
            case Protocol.MSG_TYPE_GAME_MSG_FROM_CLIENT:
                return "[ MsgType = GAME_MSG_FROM_CLIENT    "
                        +"Intent = "
                        +MsgFromClient.values()[_msgFromClient.ordinal()].toString()
                        +" ]";
            case Protocol.MSG_TYPE_LOGI_MSG:
                return "[ MsgType = LOGI_MSG    "
                        +"Intent = "
                        +MsgLogic.values()[_msgLogic.ordinal()].toString()
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

        msg.set_msgFromServer(MsgFromServer.ChessToChoose);
        System.out.println(msg);
    }
}

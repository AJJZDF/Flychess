package protocol;

import java.io.Serializable;

public class Protocol implements Serializable {

    public static final int MSG_TYPE_GAME_MSG_FROM_SERVER=0;
    public static final int MSG_TYPE_GAME_MSG_FROM_CLIENT=1;
    public static final int MSG_TYPE_LOGI_MSG=2;

    private int msg_type;//true represent 现在是server的msg，false代表现在是client的msg

    //server
    //sendToClient  谁的回合
    //      可选棋子
    //     逻辑序列
    public enum msgFromServer{
        NowYourColor,           //自身被初始化的颜色
        WhoseTurn,              //谁的回合
        ChessToChoose,          //可选棋子
        LogicSerial             //逻辑序列
    }


    //client
    //sendToClient 筛子数值
    //      选的棋子
    public enum msgFromClient{
        DiceNum,                //筛子数值
        TheChessWhoChoose       //选的棋子
    }


    public enum msgLogic{//逻辑控制：server向client发送当前已有房间号
                        //              client发送试图进入哪个房间
                        //              server返回加入是否成功信息（是 和 否 两条枚举信息）

    }


    private msgFromServer _msgFromServer;
    private msgFromClient _msgFromClient;
    private msgLogic _msgLogic;

    Protocol(int type){
        msg_type=type;
    }

    public void server_send2Client(){

    }

    public void server_recv(){}

    public void client_send2Server(){}

    public void client_recv(){}


    public String toString(){
        switch (msg_type){
            case Protocol.MSG_TYPE_GAME_MSG_FROM_SERVER:
                return "[ MsgType = GAME_MSG_FROM_SERVER"

                        +"]";
        }
        return "Msg:    MsgType = "
                +msg_type
                +"";

    }

}

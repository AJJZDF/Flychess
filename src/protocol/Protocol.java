package protocol;

import java.io.Serializable;

public class Protocol implements Serializable {

    private boolean msg_type;//true represent 现在是server的msg，false代表现在是client的msg

    //server
    //send  谁的回合
    //      可选棋子
    //     逻辑序列
    public enum msgFromServer{
        NowYourColor,           //自身被初始化的颜色
        WhoseTurn,              //谁的回合
        ChessToChoose,          //可选棋子
        LogicSerial             //逻辑序列
    }


    //client
    //send 筛子数值
    //      选的棋子
    public enum msgFromClient{
        DiceNum,                //筛子数值
        TheChessWhoChoose       //选的棋子
    }


    public void server_send2Client(){}

    public void server_recv(){}

    public void client_send2Server(){}

    public void client_recv(){}



}

package protocol;

import java.io.Serializable;

public class MsgLogic implements Serializable {
    public static final int C2S_CLIENT_TRY_JOIN_ROOM=0;          //client发送试图进入哪个房间          (tryRoomNum)
    public static final int C2S_INROOM_TRY_GAME_START=1;         //房主发送，申请开始游戏指令         (无操作数)
    public static final int C2S_INROOM_QUIT_ROOM=2;              //发送退出房间指令                    ( usr)

    public static final int C2S_SEND_CREATE_ROOM=3;             //client 发送房间名string 给server    （tryCreateRoomName）


    private int type;
    //---------------数据
        //Join room number(uuid)
    private String tryRoomNum=null;
    private String usr=null;
    private String tryCreateRoomName=null;

    public MsgLogic(int _type){
        type=_type;
    }
    public void setTryRoomNum(String tryRoomNum) {
        this.tryRoomNum = tryRoomNum;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public void setTryCreateRoomName(String tryCreateRoomName) {
        this.tryCreateRoomName = tryCreateRoomName;
    }

    public String getTryCreateRoomName() {
        return tryCreateRoomName;
    }

    public String getUsr() {
        return usr;
    }

    public String getTryRoomNum() {
        return tryRoomNum;
    }

    @Override
    public String toString() {

        if(this.type==MsgLogic.C2S_INROOM_TRY_GAME_START)
            return "[ MsgType = LOGI_MSG    "
                    +"Intent = C2S_INROOM_TRY_GAME_START "
                    +" ]";
        if(this.type==MsgLogic.C2S_CLIENT_TRY_JOIN_ROOM)//client发送试图进入哪个房间          (tryRoomNum)
            return "[ MsgType = LOGI_MSG    "
                    +"Intent = C2S_CLIENT_TRY_JOIN_ROOM    "
                    +"client发送试图进入房间    "
                    +"  Try Room Number = "
                    +this.tryRoomNum
                    +" ]";
        if(this.type==MsgLogic.C2S_INROOM_QUIT_ROOM)//发送退出房间指令                    ( usr)
            return "[ MsgType = LOGI_MSG    "
                    +"Intent = C2S_INROOM_QUIT_ROOM    "
                    +"client Usr 发送退出房间指令    "
                    +"  Quit Room Usr = "
                    +this.usr
                    +" ]";
        if(this.type==MsgLogic.C2S_SEND_CREATE_ROOM)//client 发送房间名string 给server    （tryCreateRoomName）
            return "[ MsgType = LOGI_MSG    "
                    +"Intent = C2S_SEND_CREATE_ROOM    "
                    +"client 发送房间名string 给server    "
                    +"tryCreateRoomName = "
                    +this.tryCreateRoomName
                    +" ]";
        return "[error type]";
    }
    public int getType(){
        return type;
    }
}
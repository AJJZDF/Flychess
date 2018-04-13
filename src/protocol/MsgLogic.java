package protocol;

import java.io.Serializable;

public class MsgLogic implements Serializable {
    //client发送试图进入哪个房间          (tryRoomNum)
    public static final int C2S_CLIENT_TRY_JOIN_ROOM=0;

    //房主发送，申请开始游戏指令         (无操作数)
    public static final int C2S_INROOM_TRY_GAME_START=1;

    //发送退出房间指令                    ( usr)
    public static final int C2S_INROOM_QUIT_ROOM=2;


    private int type;
    public MsgLogic(int _type){
        type=_type;
    }
    //---------------数据
    //Join room number(uuid)
    private String tryRoomNum=null;
    private String usr=null;

    public void setTryRoomNum(String tryRoomNum) {
        this.tryRoomNum = tryRoomNum;
    }

    public void setUsr(String usr) {
        this.usr = usr;
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
                    +"Intent = C2S_CLIENT_TRY_JOIN_ROOM \n"
                    +"client发送试图进入房间\n"
                    +"  Try Room Number = "
                    +this.tryRoomNum
                    +" ]";
        if(this.type==MsgLogic.C2S_INROOM_QUIT_ROOM)//发送退出房间指令                    ( usr)
            return "[ MsgType = LOGI_MSG    "
                    +"Intent = C2S_INROOM_QUIT_ROOM \n"
                    +"client Usr 发送退出房间指令\n"
                    +"  Quit Room Usr = "
                    +this.usr
                    +" ]";
        return "[error type]";
    }
    public int ordinal(){
        return type;
    }
}
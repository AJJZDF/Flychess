package NetworkRoom4Unity;

import java.io.Serializable;

public class NetworkRoommateState implements Serializable{


    public static final int STATE_BAN = 1;             //ban掉                  (posId  null    null   STATE_BAN)
    public static final int STATE_AI=2;                //当前为ai               (posId  null    null    STATE_AI)
    public static final int STATE_CHOOSE=3;            //被选择了               (posId  playerid    playerName   STATE_CHOOSE)
    public static final int STATE_WAITCHOOSE=4;        //初始状态 等待          (posId  null    null   STATE_WAITCHOOSE)
    public static final int STATE_READY=5;             //准备号了               (posId  playerid    playerName    STATE_READY)

    private int posId;          //对应的位置id,              取值[0,3]
    private int playerId;       //第几个玩家，从0开始数起。     取值[0,3]
    private String playerName;  //playerId玩家绑定了名字
    private int state;         //状态

    NetworkRoommateState(int posId, int playerId, String playerName, int state){
        this.posId=posId;
        this.playerId=playerId;
        this.playerName=playerName;
        this.state=state;
    }
    public NetworkRoommateState()
    {
        this.posId=-1;
        this.playerId=-1;
        this.playerName="null";
        this.state=STATE_WAITCHOOSE;
    }
    public int getPlayerId() {
        return playerId;
    }
    public int getPosId() {
        return posId;
    }
    public int getState() {
        return state;
    }
    public String getPlayerName() {
        return playerName;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setState(int state) {
        this.state = state;
    }


    @Override
    public String toString() {
        String str="[ posId = "
                +this.posId
                +"  playerId = "
                +this.playerId
                +"  playerName = "
                +this.playerName;
        if(this.state == STATE_BAN) str+="  state = STATE_BAN ]";
        else if(this.state == STATE_AI)str+="  state = STATE_AI ]";
        else if(this.state == STATE_CHOOSE)str+="  state = STATE_CHOOSE ]";
        else if(this.state == STATE_WAITCHOOSE)str+="  state = STATE_WAITCHOOSE ]";
        else if(this.state == STATE_READY)str+="  state = STATE_READY ]";

        return str;
    }
    public String toRoomActionString(){
        String str = "";
        str += Integer.toString(this.posId);
        str += " ";
        str += Integer.toString(this.playerId);
        str += " ";
        str += this.playerName;
        str += " ";
        str += Integer.toString(this.state);
        return str;
    }

    public static void main(String[] args) {
        //房间界面按下按钮  client发送protocol.MsgLogic.C2S_REQUSET_ROOMMATE_STATE_CHANGE 并且包含 【欲改变后的特定位置的状态】供服务器【在room逻辑】检查合法性
        //服务器检查不合法则无反应
        //服务器检查合法，则发送protocol.MsgUnity.S2C_INROOM_ROOMMATE_STATE_CHANGE 包含roommateStateList 四个人的状态表
    }
}

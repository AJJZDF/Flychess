package NetworkRoom4Unity;

import java.io.Serializable;

public class NetworkRoomAction implements Serializable{


    public static final int STATE_BAN = 1;             //ban掉                  (posId  null    null   STATE_BAN)
    public static final int STATE_AI=2;                //当前为ai               (posId  null    null    STATE_AI)
    public static final int STATE_CHOOSE=3;            //被选择了               (posId  playerid    playerName   STATE_CHOOSE)
    public static final int STATE_WAITCHOOSE=4;        //初始状态 等待          (posId  null    null   STATE_WAITCHOOSE)
    public static final int STATE_READY=5;             //准备号了               (posId  playerid    playerName    STATE_READY)

    private int posId;          //对应的位置id,              取值[0,3]
    private int playerId;       //第几个玩家，从0开始数起。     取值[0,3]
    private String playerName;  //playerId玩家绑定了名字
    private int state;         //状态

    NetworkRoomAction(int posId,int playerId,String playerName,int state){
        this.posId=posId;
        this.playerId=playerId;
        this.playerName=playerName;
        this.state=state;
    }
    public NetworkRoomAction()
    {
        this.posId=-1;
        this.playerId=-1;
        this.playerName="null";
        this.state=-1;
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
}

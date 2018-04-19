package NetworkRoom4Unity;

public class NetworkRoomAction {


    public static int STATE_BAN = 1;//简单地移动到起始点,用到的参数：playerid,chessid,action
    public static int STATE_AI=2;
    public static int STATE_CHOOSE=3;
    public static int STATE_WAITCHOOSE=4;
    public static int STATE_READY=5;

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

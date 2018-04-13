package protocol;

import java.io.Serializable;

//client
//sendToServer 筛子数值
//      选的棋子
public class GameMsgFromClient implements Serializable {
    public static final int DICE_NUM=0;                   //筛子数值
    public static final int THE_CHESS_WHO_CHOOSE=1;       //选的棋子


    private int type;
    //-------------数据
    private int dice;
    private int chosenChess;
    public GameMsgFromClient(int _type){
        type=_type;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public void setChosenChess(int chosenChess) {
        this.chosenChess = chosenChess;
    }

    public String  toString(){
        if(this.type==GameMsgFromClient.DICE_NUM)//筛子数值
            return "[ MsgType = GAME_MSG_FROM_CLIENT    "
                    +"Intent = DICE_NUM     "
                    +"筛子数值\n"
                    +" diceNum = "
                    +this.dice
                    +" ]";

        if(this.type==GameMsgFromClient.THE_CHESS_WHO_CHOOSE)//选的棋子
            return "[ MsgType = GAME_MSG_FROM_CLIENT    "
                    +"Intent = THE_CHESS_WHO_CHOOSE     "
                    +"选的棋子\n"
                    +" chosenChess = "
                    +this.chosenChess
                    +" ]";

        return "[error type]";
    }
    public int ordinal(){
        return type;
    }
}
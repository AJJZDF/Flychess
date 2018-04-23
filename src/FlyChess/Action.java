package FlyChess;

/**
 * Created by gitfan on 3/26/18.
 */
public class Action
{

    /********************************************************************************************************/

    //游戏动作专用指令集

    public static int MOVE_TO_STARTLINE = 1;    //简单地移动到起始点,用到的参数：playerid,chessid,action

    public static int NORMAL_MOVE = 2;          // 简单地向前走 step 步，不用考虑任何情况,放心走吧，用到的参数：playerid,chessid,action,step

    public static int HIDE = 3;                //隐藏棋子,记得把第playerid个玩家的第chessid个棋子设置为隐藏,用到的参数：playerid,chessid,action

    public static int FALLEN = 4;              //击落棋子，回到停机坪

    public static int FINISHED = 5;            //棋子到达终点，完成使命，把第playerid个玩家的第chessid个棋子设置为完成状态，用到的参数：playerid,chessid,action

    public static int  REVERSE = 6;            //旋转180度，把第playerid个玩家的第chessid个棋子旋转180度，然后不用做任何事。用到的参数：playerid,chessid,action
    public static int TURNRIGHT = 7;           //右转，把第playerid个玩家的第chessid个棋子向右旋转90度，然后不用做任何事。用到的参数：playerid,chessid,action
    public static int QUICK_MOVE = 8;          //加快速度移动向前移动step步，主要是飞行时或者跳步时用的。用到的参数：playerid,chessid,action,step

    public static int ACTIVATE = 9;            //当前棋子为可选状态

    public static int TURNLEFT = 12;           //左转

    public static int ENDTHISTURN = 16;        //本轮回合结束

    /********************************************************************************************************/

    //更新UI的一些指令集

    public static int SHOW_DICE = 15; //显示骰子
    public static int HIDE_DICE = 20; //隐藏骰子

    public static int SHOWTURN = 13;   //轮到谁

    public static int HIDE_SCROLL_BAR = 17; //隐藏显示轮次的滚动条

    public static int SHOW_RANKING_LIST = 18;//显示排行榜

    public static int THROW_DICE_BY_NUMBER = 21;//扔出一个特定数字的骰子,骰子数放在step

    /********************************************************************************************************/
    //恢复棋盘专用指令集

    public static int FALLEN_INSTANT = 22; //回到停机坪,但是是瞬间动作

    public static int MOVE_TO_STARTLINE_INSTANT = 23; //移动到起点线,瞬间动作

    public static int RECOVER_CHESS_BY_POS = 24;//根据pos恢复某个棋子在棋盘的unity位置,pos放到step里面

    /********************************************************************************************************/

    private int playerid;       //第几个玩家，从0开始数起
    private int chessid;        //玩家的第几个棋子，从0开始数起
    private int action;         //指令类型
    private int step;           //步数

    public Action(int playerid,int chessid,int action)
    {
        this.playerid = playerid;
        this.chessid = chessid;
        this.action = action;
    }
    public Action(int playerid,int chessid,int action,int step)
    {
        this.playerid = playerid;
        this.chessid = chessid;
        this.action = action;
        this.step = step;
    }
    public Action(Action that)
    {
        this.playerid = that.playerid;
        this.chessid = that.chessid;
        this.action = that.action;
        this.step = that.step;
    }
    public Action()
    {
        this.playerid = -1;
        this.chessid = -1;
        this.action = -1;
        this.step = -1;
    }

    public int getPlayerid()
    {
        return playerid;
    }
    public int getChessid()
    {
        return chessid;
    }
    public int getAction()
    {
        return action;
    }
    public int getStep()
    {
        return step;
    }
    /**
     * Returns a string representation of action.
     *
     * @return an action,followed by playerid,chessid,actionkind
     */
    public String toString()
    {
        String ply;
        if(playerid == 0) ply = "Red";
        else if(playerid == 1) ply = "Yellow";
        else if(playerid == 2) ply = "Blue";
        else ply = "Green";

        String str = "[ " + ply +"," + chessid +" ] " ;
        String str1 = step + " steps ";
        if(action == MOVE_TO_STARTLINE)
        {
            return (str + "move_to_startline");
        }
        else if(action == NORMAL_MOVE)
        {
            return (str + "normal_move " + str1);
        }
        else if(action == HIDE)
        {
            return (str + "hide");
        }
        else if(action == FALLEN)
        {
            return (str + "fallen");
        }
        else if(action == FINISHED)
        {
            return (str + "finished");
        }
        else if(action == REVERSE)
        {
            return (str + "revere 180°");
        }
        else if(action == TURNRIGHT)
        {
            return (str + "turnright");
        }
        else if(action == TURNLEFT)
        {
            return (str + "turnleft");
        }
        else if(action == ACTIVATE)
        {
            return (str + ACTIVATE);
        }
        else if(action == ENDTHISTURN)
        {
            return "end this turn";
        }
        else if(action == HIDE_SCROLL_BAR)
        {
            return Integer.toString(playerid);
        }
        else if(action == SHOW_RANKING_LIST)
        {
            return "show ranking list...";
        }
        else if(action == SHOW_DICE)
        {
            return "show dice";
        }
        else if(action == HIDE_DICE)
        {
            return "hide dice";
        }
        else if(action == SHOWTURN)
        {
            return "show turn " + playerid;
        }
        else if(action == QUICK_MOVE)
        {
            return (str + "quick_move " +str1);
        }
        else if(action == THROW_DICE_BY_NUMBER)
        {
            return "throwing dice " + step;
        }
        else if(action == RECOVER_CHESS_BY_POS)
        {
            return str + "set pos(" + step + ")";
        }
        else if(action == FALLEN_INSTANT)
        {
            return (str + "fallen(instant)");
        }
        else if(action == MOVE_TO_STARTLINE_INSTANT)
        {
            return (str + "move_to_startline(instant)");
        }
        else return "unknow instruction";
    }
    public String toActionString(){
        String str = "";
        str += Integer.toString(this.playerid);
        str += " ";
        str += Integer.toString(this.chessid);
        str += " ";
        str += Integer.toString(this.action);
        str += " ";
        str += Integer.toString(this.step);
        return str;
    }

}
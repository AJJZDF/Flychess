package server.ShortSocket;

import FlyChess.GameManager;
import NetworkRoom4Unity.NetworkRoommateState;
import server.ShortSocket.myRoom.Roommates;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * Created by gitfan on 4/23/18.
 */
public class Room {

    public static final int STATE_BAN = 1;             //ban掉                  (posId  null    null   STATE_BAN)
    public static final int STATE_AI=2;                //当前为ai               (posId  null    null    STATE_AI)
    public static final int STATE_CHOOSE=3;            //被选择了               (posId  playerid    playerName   STATE_CHOOSE)
    public static final int STATE_WAITCHOOSE=4;        //初始状态 等待          (posId  null    null   STATE_WAITCHOOSE)
    public static final int STATE_READY=5;             //准备号了               (posId  playerid    playerName    STATE_READY)

//    public static final int ONEP = 1;//1p
//    public static final int TWOP = 2;//2p
//    public static final int THREEP = 3;//3p
//    public static final int FOURP = 4;//4p

    public static final int AI = 5;//AI
    public static final int NOT_EXIT = -1;//没有玩家

    private String roomname;//房间名
    private String roomowner;//房主名字，创建房间的时候绑定，用于判断当前玩家可不可以点开始游戏

    private static String INIT_NAME = " ";

    private NetworkRoommateState [] roommateStates;

    public boolean isGaming;//非开始游戏状态则说明，需要维护roommateStates

    private Vector<Roommates> roommates;

    public static String AI_NAMES[];
    static
    {
        AI_NAMES = new String[4];
        AI_NAMES[0] = "红色AI";
        AI_NAMES[1] = "黄色AI";
        AI_NAMES[2] = "蓝色AI";
        AI_NAMES[3] = "绿色AI";
    }
    //通过ip地址获得玩家名字
    private String getNameByIP(InetAddress address)
    {
        for(int i = 0 ; i < roommates.size();i ++)
        {
            if(roommates.get(i).compareIP(address))
            {
                return roommates.get(i).getName();
            }
        }
        return "error_ip";
    }
    //根据IP获取玩家
    private Roommates getPlayerByIP(InetAddress address)
    {
        for(int i = 0 ; i < roommates.size();i ++)
        {
            if(roommates.get(i).compareIP(address))
            {
                return roommates.get(i);
            }
        }
        return null;
    }
    public Room(String roomname,String roomowner)
    {
        this.roomname = roomname;
        this.roomowner = roomowner;

        roommateStates = new NetworkRoommateState[4];

        for(int i = 0 ; i < 4; i++)
        {
            roommateStates[i].setState(STATE_WAITCHOOSE);
            roommateStates[i].setPlayerKind(NOT_EXIT);
            roommateStates[i].setPosId(i);
            roommateStates[i].setPlayerName(INIT_NAME);
        }

        isGaming = false;
        roommates = new Vector<>();
    }
    public String getRoomname(){ return roomname;}
    public String getRoomowner(){ return roomowner;}

    //分配一个可以用的位置
    private int allocate_position()
    {
        for(int i = 0 ; i < 4; i++)
        {
            if(roommateStates[i].getState() == STATE_WAITCHOOSE){
                return i;
            }
        }
        return -1;
    }
    //测试一下是不是可以再加一个玩家进入房间
    //不允许含有重名玩家
    //true表示可以再加入,false表示不可以再加入
    public boolean join_test(String name)
    {
        if(isGaming) return false;

        for(int i = 0 ; i < roommates.size();i++)
        {
            if(roommates.get(i).getName().equals(name)) return false;
        }
        return (allocate_position() != -1);
    }
    //添加一个玩家:(主要是进入房间时自动分配)
    public boolean addPlayer(InetAddress address,String name)
    {
        if(isGaming) return false;

        int pos = allocate_position();
        if(pos < 0 ) return false;

        Roommates mate = new Roommates(address,name,roommates.size()+1);

        roommates.add(mate);
        roommateStates[pos].setState(STATE_CHOOSE);
        roommateStates[pos].setPlayerName(name);
        roommateStates[pos].setPosId(pos);
        roommateStates[pos].setPlayerKind(mate.getXp());// x p: 1p,2p,3p或4p

        return true;
    }
    //删除一个玩家:玩家退出房间时使用
    public boolean removePlayer(InetAddress address)
    {
        int idx = 0;

        for(; idx < roommates.size(); idx++)
        {
            if(roommates.get(idx).compareIP(address)) break;
        }

        if(idx == roommates.size()) return false;

        Roommates mate = roommates.get(idx);//先获得玩家名,然后去清空他之前选过的位置

        //清空位置
        for(int i = 0 ; i < 4; i++)
        {
            if(roommateStates[i].getPlayerName().equals(mate.getName()))
            {
                roommateStates[i].setPlayerName(INIT_NAME);//清空
                roommateStates[i].setState(STATE_WAITCHOOSE);//恢复到初始化状态
                roommateStates[i].setPosId(i);
                roommateStates[i].setPlayerKind(NOT_EXIT);
            }
        }

        roommates.remove(idx);

        return true;
    }

    /****************************************************************************************************/
    //房主专有操作

    //关闭某个位置
    private boolean pos_ban(String name,int pos)
    {
        //不是房主,没有权利关闭位置
        if(!roomowner.equals(name)) return false;

        if(roommateStates[pos].getState() == STATE_AI || roommateStates[pos].getState() == STATE_WAITCHOOSE)
        {
            roommateStates[pos].setState(STATE_BAN);
            roommateStates[pos].setPlayerName(INIT_NAME);
            roommateStates[pos].setPlayerKind(NOT_EXIT);
            roommateStates[pos].setPosId(pos);
            return true;
        }
        return false;
    }
    public boolean pos_ban(InetAddress address,int pos)
    {
        String playername = getNameByIP(address);
        if(playername.equals("error_ip")) return false;
        return pos_ban(playername,pos);
    }
    //将禁止的位置恢复为初始状态
    private boolean pos_ban_undo(String name,int pos)
    {
        //不是房主,没有权利
        if(!roomowner.equals(name)) return false;
        if(roommateStates[pos].getState() == STATE_BAN)
        {
            roommateStates[pos].setState(STATE_WAITCHOOSE);
            roommateStates[pos].setPlayerName(INIT_NAME);
            roommateStates[pos].setPosId(pos);
            roommateStates[pos].setPlayerKind(NOT_EXIT);
            return true;
        }
        return false;
    }
    public boolean pos_ban_undo(InetAddress address,int pos)
    {
        String playername = getNameByIP(address);
        if(playername.equals("error_ip")) return false;
        return pos_ban_undo(playername,pos);
    }

    //添加AI
    private boolean pos_addAI(String name,int pos)
    {
        //不是房主,没有权利
        if(!roomowner.equals(name)) return false;
        if(roommateStates[pos].getState() == STATE_WAITCHOOSE)
        {
            roommateStates[pos].setState(STATE_AI);
            roommateStates[pos].setPlayerName(AI_NAMES[pos]);
            roommateStates[pos].setPosId(pos);
            roommateStates[pos].setPlayerKind(AI);
            return true;
        }
        return false;
    }
    public boolean pos_addAI(InetAddress address,int pos)
    {
        String playername = getNameByIP(address);
        if(playername.equals("error_ip")) return false;
        return pos_addAI(playername,pos);
    }

    //删除AI
    private boolean pos_removeAI(String name,int pos)
    {
        //不是房主,没有权利
        if(!roomowner.equals(name)) return false;
        if(roommateStates[pos].getState() == STATE_AI)
        {
            roommateStates[pos].setPlayerKind(NOT_EXIT);
            roommateStates[pos].setState(STATE_WAITCHOOSE);
            roommateStates[pos].setPlayerName(INIT_NAME);
            roommateStates[pos].setPosId(pos);
            return true;
        }
        return false;
    }
    //删除AI
    public boolean pos_removeAI(InetAddress address,int pos)
    {
        String playername = getNameByIP(address);
        if(playername.equals("error_ip")) return false;
        return pos_removeAI(playername,pos);
    }
    /****************************************************************************************************/

    //玩家选择一个位置
    private boolean pos_choose(Roommates mate,int pos)
    {
        String name = mate.getName();
        //先判断下是不是可以选择该位置
        if(roommateStates[pos].getState() != STATE_WAITCHOOSE) return false;

        //先清除该玩家之前已经选择的位置
        for(int i = 0 ; i < 4; i ++)
        {
            if(roommateStates[i].getPlayerName().equals(name)){
                //如果玩家已经准备,那么它不可以改变之前的选择
                if(roommateStates[i].getState() == STATE_READY) {
                    return false;
                }
                else if(roommateStates[i].getState() == STATE_CHOOSE){
                    //清除之前的选择
                    roommateStates[i].setState(STATE_WAITCHOOSE);
                    roommateStates[i].setPlayerName(INIT_NAME);
                    roommateStates[i].setPlayerKind(NOT_EXIT);
                    roommateStates[i].setPosId(i);
                    break;
                }
            }
        }

        //再为该玩家重新选择
        roommateStates[pos].setState(STATE_CHOOSE);
        roommateStates[pos].setPlayerName(name);
        roommateStates[pos].setPosId(pos);
        roommateStates[pos].setPlayerKind(mate.getXp());

        return true;
    }
    //玩家选择一个位置
    public boolean pos_choose(InetAddress address,int pos)
    {
        Roommates mate = getPlayerByIP(address);
        if(mate == null) return false;
        return pos_choose(mate,pos);
    }

    //玩家请求准备
    private boolean pos_setReady(Roommates mate)
    {
        String name = mate.getName();

        for(int i = 0 ; i < 4; i ++)
        {
            if(roommateStates[i].getPlayerName().equals(name))
            {
                if(roommateStates[i].getState() == STATE_CHOOSE)
                {
                    roommateStates[i].setPlayerKind(mate.getXp());
                    roommateStates[i].setPosId(i);
                    roommateStates[i].setPlayerName(name);
                    roommateStates[i].setState(STATE_READY);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean pos_setReady(InetAddress address)
    {
        Roommates mate = getPlayerByIP(address);
        if(mate == null) return false;
        return pos_setReady(mate);
    }

    //玩家取消准备
    private boolean pos_set_unready(Roommates mate)
    {
        String name = mate.getName();

        for(int i = 0 ; i < 4; i ++)
        {
            if(roommateStates[i].getPlayerName().equals(name))
            {
                if(roommateStates[i].getState() == STATE_READY)
                {
                    roommateStates[i].setState(STATE_CHOOSE);
                    roommateStates[i].setPlayerKind(mate.getXp());
                    roommateStates[i].setPosId(i);
                    roommateStates[i].setPlayerName(name);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean pos_set_unready(InetAddress address)
    {
        Roommates mate = getPlayerByIP(address);
        if(mate == null) return false;
        return pos_set_unready(mate);
    }

    //判断是否可以开始游戏
    private boolean start_test(String name)
    {
        //只有房主才可以开始游戏
        if(!name.equals(roomowner)) return false;

        Roommates mate;
        int ready_cnt = 0;
        for(int i = 0 ; i < roommates.size();i++)
        {
            mate = roommates.get(i);
            for(int j = 0 ; j < 4; j++)
            {
                if(roommateStates[j].getPlayerName().equals(mate.getName()))
                {
                    if(roommateStates[j].getState()!= STATE_READY) return false;//有玩家没有准备好
                    else ready_cnt ++;
                }
            }
        }
        //只有所有玩家都准备好才可以开始游戏
        if(ready_cnt != roommates.size()) return false;
        else return true;
    }
    public boolean start_test(InetAddress address)
    {
        String name = getNameByIP(address);
        return start_test(name);
    }
    //设置游戏开始
    public void set_game_start()
    {
        isGaming = true;
    }

    /**********************************************************************************************************/
    //与GameManager的初始化相关
    //记得要设置游戏为开始状态
    public int[] getGameKind()
    {
        int gamekind [] = new int [4];
        for(int i = 0 ; i < 4; i ++)
        {
            switch (roommateStates[i].getState())
            {
                case STATE_AI:
                {
                    gamekind[i] = GameManager.AI_KIND;
                    break;
                }
                case STATE_BAN:
                {
                    gamekind[i] = GameManager.NOT_USE_KIND;
                    break;
                }
                case STATE_CHOOSE:
                {
                    gamekind[i] = GameManager.NOT_USE_KIND;
                    break;
                }
                case STATE_READY:
                {
                    gamekind[i] = GameManager.PLAYER_KIND;
                    break;
                }
                case STATE_WAITCHOOSE:
                {
                    gamekind[i] = GameManager.NOT_USE_KIND;
                    break;
                }
                default:
                    gamekind[i] = GameManager.NOT_USE_KIND;
                    break;
            }
        }
        return gamekind;
    }
    public String[] getNameList()
    {
        String namelist[]=  new String[4];
        for(int i = 0 ; i < 4; i ++)
        {
            switch (roommateStates[i].getState())
            {
                case STATE_AI:
                {
                    namelist[i] = roommateStates[i].getPlayerName();
                    break;
                }
                case STATE_BAN:
                {
                    namelist[i] = INIT_NAME;
                    break;
                }
                case STATE_CHOOSE:
                {
                    namelist[i] = INIT_NAME;
                    break;
                }
                case STATE_READY:
                {
                    namelist[i] = roommateStates[i].getPlayerName();
                    break;
                }
                case STATE_WAITCHOOSE:
                {
                    namelist[i] = INIT_NAME;
                    break;
                }
                default:
                    namelist[i] = INIT_NAME;
                    break;
            }
        }
        return namelist;
    }

    public static void main(String[] args) throws UnknownHostException {

    }
}

package server.LongSocket;

import NetworkRoom4Unity.NetworkRoommateState;

import java.util.Vector;

public class Room {
    //1.房间点击ui的交互处理
    //2.进入房间的时候自动分配一个颜色
    //3。当前这个玩家可不可以点开始游戏。开始条件，这个人是房主。并且所有玩家已经准备

    //负责转发逻辑

    //存放四个人的socket对象
    //用vector（可能有俩人三人四人）
    public Vector<MyClient> roomMembers;

    public String roomName;
    public String roomMasterName;                           //房主名字，创建房间的时候绑定，用于判断当前玩家可不可以点开始游戏

    private NetworkRoommateState [] roommateStates;    //当前游戏状态
    private boolean isGaming=false;                         //非开始游戏状态则说明，需要维护roommateStates





    //

    private static final int ROOM_MASTER=0;//初始化为第一个进入房间的人就是你的master




    public Room(String _roomName){
        roomMembers=new Vector<>();
        roommateStates=new NetworkRoommateState[4];
        for (int i = 0; i <4 ; i++) {
            roommateStates[i].setPosId(i);
        }
    }

    //返回当前房间内的总人数
    public int nowPersons(){
        return roomMembers.size();
    }

    @Override
    public String toString() {
        return "[ roomName = "
                +roomName
                +",roomMembers = "
                +roomMembers
                +" ]";
    }
}

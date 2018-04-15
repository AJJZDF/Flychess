package server;

import java.util.Vector;

public class Room {

    //存放四个人的socket对象
    //用vector（可能有俩人三人四人）
    public Vector<MyClient> roomMembers;
    public String roomName;
    //当前游戏状态
    private boolean isGaming=false;

    private static final int ROOM_MASTER=0;//初始化为第一个进入房间的人就是你的master

    //负责转发逻辑



    public Room(String _roomName){
        roomMembers=new Vector<>();

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

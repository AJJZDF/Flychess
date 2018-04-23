package server.ShortSocket;

import NetworkRoom4Unity.NetworkRoommateState;
import protocol.Protocol;
import protocol.UnifiedStandard;
import server.ShortSocket.myRoom.Roommates;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by gitfan on 4/15/18.
 */
public class Room {

    String roomname;

    //详细各个函数还未从InetAddress改成Roommates类
    Vector<Roommates> roommates;


    public String roomName;
    public String roomMasterName;                           //房主名字，创建房间的时候绑定，用于判断当前玩家可不可以点开始游戏

    private NetworkRoommateState[] roommateStates;          //在非游戏状态时候，当前每个位置的人物的状态
    private boolean isGaming=false;                         //非开始游戏状态则说明，需要维护roommateStates

    private static final int ROOM_MASTER=0;//初始化为第一个进入房间的人就是你的master



    public Room(String roomname)
    {
        this.roomname = roomname;
        this.roommates = new Vector<>();
    }
    public Room(String roomname, InetAddress address)
    {
        this.roomname = roomname;
        this.roommates = new Vector<>();
        roommates.add(address);
    }
    public void addPlayer(InetAddress address)
    {
        roommates.add(address);
    }
    public boolean removePlayer(InetAddress address)
    {
        int idx = 0;
        for(; idx < roommates.size(); idx++){
            if(roommates.get(idx).equals(address))
            {
                roommates.remove(idx);
                return true;
            }
        }
        return false;
    }

    //当协议进入游戏逻辑后，只要是Game前缀的消息都给他调用这个函数
    public void broadcast(Protocol msg) throws IOException {

        for (int i = 0; i < roommates.size(); i++) {
            _sendMsgto(roommates[i].getInetAddress(),msg);
        }

    }

    private void _sendMsgto(InetAddress address,Protocol msg)throws IOException{
        //连接到手机客户端
        Socket socket = new Socket(address, UnifiedStandard.CLIENT_PORT);

        System.out.println("send to " + socket.getInetAddress().toString());

        //protocol对象序列化传送
        Protocol.socketSerilize(socket,msg);

        //及时关闭连接
        socket.close();

        System.out.println("finish send");
    }
}

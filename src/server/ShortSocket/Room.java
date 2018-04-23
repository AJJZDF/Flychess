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

    Vector<Roommates> roommates;

    String roomName;
    public String roomMasterName;                           //房主名字，创建房间的时候绑定，用于判断当前玩家可不可以点开始游戏

    private boolean isGaming=false;                         //非开始游戏状态则说明，需要维护roommateStates
    private NetworkRoommateState[] roommateStates;          //在非游戏状态时候，当前每个位置的人物的状态


    private static final int ROOM_MASTER=0;//初始化为第一个进入房间的人就是你的master



    public Room(String roomName)
    {
        this.roomName = roomName;
        this.roommates = new Vector<>();
        roommateStates=new NetworkRoommateState[4];
        for (int i = 0; i <4 ; i++) {
            roommateStates[i].setPosId(i);
        }
    }
    public Room(String roomName, InetAddress address,String roomMasterName)
    {
        this.roomName = roomName;
        this.roommates = new Vector<>();
        this.roomMasterName=roomMasterName;

        roommates.add(new Roommates(address,roomMasterName));

        roommateStates=new NetworkRoommateState[4];
        for (int i = 0; i <4 ; i++) {
            roommateStates[i].setPosId(i);
        }
    }
    public void addPlayer(InetAddress address,String name)
    {
        roommates.add(new Roommates(address,name));
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
        if(isGaming)
        for (int i = 0; i < roommates.size(); i++) {
            _sendMsgto(roommates.elementAt(i).getInetAddress(),msg);
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

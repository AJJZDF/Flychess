package server.ShortSocket.myRoom;

import java.net.InetAddress;

public class Roommates {
    //一个房间里的一个人的抽象
    //存放ip和名字
    private InetAddress inetAddress;
    private String name;

    public String getName() {
        return name;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Roommates(InetAddress inetAddress,String name){
        this.inetAddress=inetAddress;
        this.name=name;
    }
}

package server.ShortSocket.myRoom;

import java.net.InetAddress;

public class Roommates {
    //一个房间里的一个人的抽象
    //存放ip和名字
    private InetAddress inetAddress;
    private String name;

    private int xp;//1p 2p 3p 4p

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

    //第几p
    public void setXp(int playerkind)
    {
        xp = playerkind;
    }
    //几p
    public int getXp()
    {
        return xp;
    }

    //比较IP地址是否一样
    public boolean compareIP(InetAddress address)
    {
        return inetAddress.equals(address);
    }

    public Roommates(InetAddress inetAddress,String name,int xp){
        this.inetAddress=inetAddress;
        this.name=name;
        this.xp = xp;
    }
}

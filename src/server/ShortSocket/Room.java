package server.ShortSocket;

import java.net.InetAddress;
import java.util.Vector;

/**
 * Created by gitfan on 4/15/18.
 */
public class Room {

    String roomname;

    Vector<InetAddress> addresses;

    public Room(String roomname)
    {
        this.roomname = roomname;
        this.addresses = new Vector<>();
    }
    public Room(String roomname, InetAddress address)
    {
        this.roomname = roomname;
        this.addresses = new Vector<>();
        addresses.add(address);
    }
    public void addPlayer(InetAddress address)
    {
        addresses.add(address);
    }
    public boolean removePlayer(InetAddress address)
    {
        int idx = 0;
        for(;idx < addresses.size();idx++){
            if(addresses.get(idx).equals(address))
            {
                addresses.remove(idx);
                return true;
            }
        }
        return false;
    }
}

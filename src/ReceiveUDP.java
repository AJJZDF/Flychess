
import java.net.DatagramPacket;

import java.net.DatagramSocket;
import java.util.*;
import java.util.Queue;


public class ReceiveUDP {

    Set<String> recvRoom ;
    ReceiveUDP(){
        recvRoom= new HashSet<String>();
    }
    void recv()throws Exception{
        int listenPort = 9999;

        byte[] buf = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        @SuppressWarnings("resource")
        DatagramSocket responseSocket = new DatagramSocket(listenPort);

        System.out.println("Server started, Listen port: " + listenPort);

        while (true) {

            responseSocket.receive(packet);

            String rcvd = "Received "

                    + new String(packet.getData(), 0, packet.getLength())

                    + " from address: " + packet.getSocketAddress();


            System.out.println(rcvd);

            //如果room 集合中没有这个房间（以ip地址作为唯一标识符）
            if(recvRoom.add(packet.getSocketAddress().toString())){
                // Send a response packet to sender
                //这个packet包含了client的局域网地址，可用于建立tcp连接
                String backData = "halo,server .including client address for tcp";

                byte[] data = backData.getBytes();

                System.out.println("Send " + backData + " to " + packet.getSocketAddress());

                DatagramPacket backPacket = new DatagramPacket(data, 0,

                        data.length, packet.getSocketAddress());

                responseSocket.send(backPacket);
            }




        }
    }

    public static void main(String[] args) throws Exception {
        ReceiveUDP receiveUDP=new ReceiveUDP();

        receiveUDP.recv();

    }
}
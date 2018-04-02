import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class UDP_server {//send udp  广播udp
    //接收
    private Queue<String> queueClient;
    // Use this port to send broadcast packet
    @SuppressWarnings("resource")
    final DatagramSocket detectSocket;

    class sendMsg implements Runnable {
        @Override
        public void run() {

            System.out.println("Send thread started.");

            while (true) {
                try {

                    byte[] buf = new byte[1024];

                    int packetPort = 9999;

                    // Broadcast address
                    InetAddress hostAddress = InetAddress.getByName("192.168.1.255");

                    //输出调试,控制发送的间隔
                    BufferedReader stdin = new BufferedReader(

                            new InputStreamReader(System.in));

                    String outMessage = stdin.readLine();


                    if (outMessage.equals("bye"))

                        break;

                    buf = outMessage.getBytes();

                    System.out.println("Send " + outMessage + " to " + hostAddress);


                    // Send packet to hostAddress:9999, server that listen
                    // 9999 would reply this packet

                    String bufString="Room Number:   "+ UUID.randomUUID().toString();//uuid 表示房间号，绝对唯一
                    buf=bufString.getBytes();
                    DatagramPacket out = new DatagramPacket(buf,

                            buf.length, hostAddress, packetPort);

                    detectSocket.send(out);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    class recMsg implements Runnable{
        @Override
        public void run() {
            System.out.println("Receive thread started.");

            while(true) {

                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    detectSocket.receive(packet);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //getSocketAddress()包含端口号
                String rcvd = "Received from " + packet.getSocketAddress();

                //把响应广播的局域网地址，加入到外部类的队列。形式为"/192.168.1.136:8888"
                UDP_server.this.queuePush(packet.getSocketAddress().toString());

                System.out.println(rcvd);
                System.out.println("Data="

                        + new String(packet.getData(), 0, packet.getLength()) );
            }

        }
    }

    /*
    检查队列是否超过三个人（加上自己一共四人）
     */
    boolean queuePush(String address){

        if (queueClient.size()<4){
            queueClient.add(address);
            return true;
        }
        else return false;
    }

    private sendMsg _thread_sendMsg;
    private recMsg _thread_recMsg;
    UDP_server() throws SocketException {
        _thread_sendMsg =this.new sendMsg();
        _thread_recMsg=this.new recMsg();
        detectSocket = new DatagramSocket(8888);
        queueClient=new LinkedList<String>();
    }

    public static void main(String[] args) throws SocketException {

        UDP_server udp_server=new UDP_server();

        // Send packet thread
        new Thread(udp_server._thread_sendMsg).start();

        // Receive packet thread.
        new Thread(udp_server._thread_recMsg).start();
    }


}

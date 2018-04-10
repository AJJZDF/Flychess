import java.io.IOException;

import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class UDP_server {//send udp  广播udp
    //接收
    private Queue<String> queueClient;
    // Use this port to send broadcast packet
    @SuppressWarnings("resource")
    private final DatagramSocket detectSocket;

    //接收线程在等到三个玩家回应UDP广播后结束，三个玩家的地址存在queueClient里
    class recMsg extends Thread{
        @Override
        public void run() {
            System.out.println("Receive thread started.");

            //一直尝试加锁
            while(!lock_toCheckPlayerReady.tryLock());

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
                boolean checkSuccess= UDP_server.this.queuePush(packet.getSocketAddress().toString());
                if(!checkSuccess)break;//线程退出出口


                System.out.println(rcvd);
                System.out.println("Data="

                        + new String(packet.getData(), 0, packet.getLength()) );
            }

            //解锁
            lock_toCheckPlayerReady.unlock();
        }
    }
    class broadCastMsg extends Thread {
        public volatile boolean exit = false;

        public String nowRoomNum=UUID.randomUUID().toString();
        //广播我是房主，识别附近的机器
        void broadcastRoomMaster(){
            try {
                byte[] buf = new byte[1024];

                int packetPort = 9999;

                // Broadcast address
                InetAddress hostAddress = InetAddress.getByName("192.168.1.255");


                //System.out.println("Send " + outMessage + " to " + hostAddress);

                // Send packet to hostAddress:9999, server that listen
                // 9999 would reply this packet

                String bufString="Room Number:   "+ nowRoomNum;//uuid 表示房间号，绝对唯一
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
        @Override
        public void run() {
            while (!exit) {
                //每隔一秒广播一次我是房主，以供后续发现房间的人联系
                broadcastRoomMaster();

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setExit(){
            exit=true;
        }
    }
    private recMsg _thread_recMsg;
    private broadCastMsg _thread_broadCastMsg;
    private Lock lock_toCheckPlayerReady =new ReentrantLock();

    UDP_server() throws SocketException {
        _thread_recMsg =this.new recMsg();
        _thread_broadCastMsg=this.new broadCastMsg();
        detectSocket = new DatagramSocket(8888);
        queueClient=new LinkedList<String>();
    }



    /*
    检查队列是否超过三个人（加上自己一共四人）,加了三个人之后开始return false，以退出recv线程
     */
    boolean queuePush(String address){

        if (queueClient.size()<3){
            queueClient.add(address);
            if(queueClient.size()==3) {
                //此时满人了，吧broadCast线程关掉
                _thread_broadCastMsg.setExit();
                return false;
            }
            return true;
        }
        else return false;
    }

    //线程自动销毁
    void newThreadRecv(){
        _thread_recMsg.start();
    }
    void newThreadBroadCast(){
        _thread_broadCastMsg.start();
    }

    Queue<String> getQueueClient(){
        //试图枷锁
        myTryLock:
        while(true) {
            while (lock_toCheckPlayerReady.tryLock()) {
                if (queueClient.size() != 3) {//client队列还没有生成好
                    lock_toCheckPlayerReady.unlock();
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                else break myTryLock;//client 队列生成好了
            }
            //尝试枷锁失败，继续尝试
        }
        return queueClient;
    }

    public static void main(String[] args) throws SocketException {

        UDP_server udp_server=new UDP_server();

        //broadCast thread
        udp_server.newThreadBroadCast();

        // Receive packet thread.
        udp_server.newThreadRecv();

        //
        System.out.println(udp_server.getQueueClient());
        //udp_server.getQueueClient();
    }


}

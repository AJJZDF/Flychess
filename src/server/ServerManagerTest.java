package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ServerManagerTest {


    private Vector<recMsg> vec_clients;              //存储已经accept的client
    private Map<Socket,Integer> map_client2Index;   //映射socket到Vector中的index

    class recMsg extends Thread{
        Socket client;//client 和他的接收线程的生命周期一致
        @Override
        public void run() {

        }
    }


    ServerManagerTest(){
        vec_clients=new Vector<recMsg>();
        map_client2Index=new HashMap<Socket,Integer>();
    }
    public static void main(String[] args) {
        ServerSocket serverSocket= null;
        try {
            serverSocket = new ServerSocket(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket client=null;

        while(true){
            try {
                //收到一个client，首先将其push进去vec列表，
                //然后新建client_recv_thread去创建接受进程
                client = serverSocket.accept();




                BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter os = new PrintWriter(client.getOutputStream());

                BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));

                String inputString="Hello,tcp client\n";

                os.print(inputString);
                os.flush();



            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }
}

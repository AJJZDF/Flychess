package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClientModel {


    class MyServer extends Thread{
        private Socket server=null;

        public volatile boolean exitRecvThread =false;
        private BufferedReader is ;
        private PrintWriter os ;

        @Override   //Recv线程(Recv from server)
        public void run() {
            while (!exitRecvThread) {

                //recv protocol(msg from server)
                String msg;

                try {
                    if (is.ready()) {
                        msg = is.readLine();
                        if (!msg.equals("")) {
                            System.out.println("msg from server:"+msg);

                        }
                    }
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
                //sendToClient

            }
        }
        public void exitRecvThread(){
            exitRecvThread=true;
        }

        MyServer(String intelAddress,int port){//intelAddress eg."127.0.0.1",port端口号


            try {
                //向intelAddress 的ip地址，port端口号发出请求
                this.server =new Socket(intelAddress,port);

                //初始化输入输出流
                is = new BufferedReader(new InputStreamReader(server.getInputStream()));
                os = new PrintWriter(server.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void send2Server(String msg){
            //后期改造成protocol对象序列化传送
            os.print(msg);
            os.flush();
        }
    }

    public static void main(String[] args) {

        GameClientModel gameClientModel=new GameClientModel();
        MyServer myServer=gameClientModel.new MyServer("127.0.0.1",8888);

        myServer.start();
    }
}

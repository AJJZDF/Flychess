package FlyChess;

import java.io.*;
import java.util.Date;
import java.util.Vector;

/**
 * Created by gitfan on 4/19/18.
 */
public class Replayer {
    Vector<Action> initGame;
    Vector<Vector<Action>> recover_game;
    Vector<Vector<Action>> action_list;
    public Replayer(FileInputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String read_line = reader.readLine();
        initGame = string_to_actionlist(read_line);//第一行是初始化游戏

        recover_game = new Vector<>();
        action_list = new Vector<>();

        int cnt = 0;
        while((read_line = reader.readLine()) != null)
        {
            if(read_line.length() == 0) continue;
            cnt ++;
            //是奇数(加入recoverGame)
            if((cnt&1) != 0){
                recover_game.add(string_to_actionlist(read_line));
            }
            else action_list.add(string_to_actionlist(read_line));
        }

        in.close();
        reader.close();
    }

    public Vector<Action> getInitGame()
    {
        return initGame;
    }
    public Vector<Vector<Action>> getRecoverGame()
    {
        return recover_game;
    }
    public Vector<Vector<Action>> getActionlist()
    {
        return action_list;
    }
    // [ 0 , size-1 ]
    public int size()
    {
        return recover_game.size();
    }

    private static Vector<Action> string_to_actionlist(String str)
    {
        Vector<Action> vector = new Vector<>();
        Action action;
        String arr[] = str.split("\\s+");
        int action_cnt = arr.length/4;
        for(int i = 0 ; i < action_cnt; i++)
        {
            action = new Action(Integer.parseInt(arr[i*4 + 1]),Integer.parseInt(arr[i*4+2]),Integer.parseInt(arr[i*4+3]),Integer.parseInt(arr[i*4+4]));

            vector.add(action);
        }
        return vector;
    }
    private Vector<Action> fun_recoverGame(int i)
    {
        return recover_game.get(i);
    }
    private Vector<Action> fun_actionlist(int i)
    {
        return action_list.get(i);
    }

    public static String create_file_name_by_time()
    {
        //用时间作为文件名: eg:   2018-04-19-23:18
        Date date = new Date();
        String time_str = String.format("%tF",date);
        time_str += String.format("-%tR",date);
        return time_str;
    }

    public static void main(String[] args) throws IOException {

        Replayer replayer = new Replayer(new FileInputStream("/home/gitfan/桌面/test"));

        System.out.println(replayer.size());
        for(Action action: replayer.fun_recoverGame(21))
        {
            System.out.println(action);
        }
        System.out.println("**************************************************************************");
        for(Action action: replayer.fun_actionlist(21))
        {
            System.out.println(action);
        }
    }
}
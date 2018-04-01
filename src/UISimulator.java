import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

/**
 * Created by gitfan on 3/31/18.
 */


/*
 *模拟UI界面扔骰子，选择棋子
 */

class Button
{
    private boolean visable;
    private String name;
    public Button(String name)
    {
        visable = true;
    }
    public void setVisable(boolean visable)
    {
        this.visable = visable;
        if(visable){
            //System.out.println("显示按钮:"+name);
        }
        else{
            //System.out.println("隐藏按钮:"+name);
        }
    }
    public void addListener()
    {

    }
    public void clicked()
    {

    }
}

public class UISimulator extends Thread{

    private int dice; //骰子数
    private int choice; //选择哪个玩家

    private String clicked;//代替点击按钮那个动作

    Button diceButton = new Button("投掷");

    Button chessbuttons [] [];


    private GameManager gameManager;

//    private JFrame frame;


    private void setAllHide()
    {

        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                chessbuttons[i][j].setVisable(false);
            }
        }
        diceButton.setVisable(false);
    }

    public UISimulator(GameManager gameManager)
    {

//        frame = new JFrame("临时转换");
//        frame.setSize(400, 300);
//        frame.setLocation(200, 200);
//        frame.setLayout(null);

        JButton toAI = new JButton("toAI");
        toAI.setBounds(100,100,100,30);
        toAI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!gameManager.isAI())
                {
                    UISimulator.this.gameManager.switchToAI(0);
                }

            }
        });


//        JButton toPlayer = new JButton("toPlayer");
//        toPlayer.setBounds(100,150,100,30);
//        toPlayer.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(gameManager.isAI())
//                {
//                    UISimulator.this.gameManager.switchToUser(0);
//                }
//
//            }
//        });

//        frame.add(toAI);
//        frame.add(toPlayer);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);


        //初始化16个棋子
        chessbuttons = new Button[4][];
        for(int i = 0; i < 4; i++)
        {
            chessbuttons[i] = new Button[4];

            String str = "";
            if(i == Chess.RED) str = "Red";
            else if(i == Chess.GREEN) str = "Green";
            else if(i == Chess.BLUE) str = "Blue";
            else if(i == Chess.YELLOW) str = "Yellow";


            for(int j = 0; j < 4; j++)
            {
                chessbuttons[i][j] = new Button(str + " " + j);
            }
        }

        this.gameManager = gameManager;
    }

    //投掷棋子的驱动
    //先通过gamemanager判断是人还是AI
    //如果是人，则应该出现投掷的按钮，并让玩家自己选择
    private void throwDice()
    {

        //先隐藏所有的棋子，防止玩家误触，也不是一定要隐藏，就是要让玩家点击这些按钮也没用
        setAllHide();

        //如果是AI
        if(gameManager.isAI())
        {

            //AI玩时不显示扔骰子的按钮，或者让扔骰子的按钮卡住，就是这个意思,理解就好。。。。
            diceButton.setVisable(false);

            //然后随机生成骰子

            /***************************************************************/

            //这块区域代表扔骰子的动画

            dice = createDice();//随机产生一个dice

            dicePlayer();//根据骰子数目来播放动画



            /***************************************************************/


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //扔完骰子记得给gameManager设置骰子！！！
            //切记，很重要！！！
            gameManager.setDice(dice);

        }
        //如果是人，则需要让人来选择
        else
        {
            //如果是人，显示扔骰子的按钮，让人来选择什么时候扔骰子
            diceButton.setVisable(true);
            System.out.print("please throw a dice: ");



            /***************************************************************/

            //以下这段代码放在diceButton的监听器里，即ClickedListener

            //模拟用户点击了扔骰子的按钮
            Scanner in = new Scanner(System.in);
            clicked = in.next();


            //随机产生骰子以及播放动画

            dice = createDice();
            dicePlayer();


            //扔完骰子记得给gameManager设置骰子！！！
            //切记，很重要！！！

            gameManager.setDice(dice);

            /****************************************************************/
        }
    }
    //随机产生骰子点数
    private int createDice()
    {
        return ((int)(Math.random()*1000000))%6 + 1;
    }
    //根据骰子数来播放相应的动画
    private void dicePlayer()
    {
        if(dice == 1){
            System.out.println("Throwing a dice...");
        }
        else if(dice == 2)
        {
            System.out.println("Throwing a dice...");
        }
        else if(dice == 3)
        {
            System.out.println("Throwing a dice...");
        }
        else if(dice ==4){
            System.out.println("Throwing a dice...");
        }
        else if(dice == 5){
            System.out.println("Throwing a dice...");
        }
        else {
            System.out.println("Throwing a dice...");
        }
        System.out.println("dice: "+dice);
    }

    //扔完骰子之后选择移动哪个棋子
    //这里也分玩家和AI
    //玩家可以自己棋子，而AI只能自己选棋子
    public void selectChess()
    {
        //先隐藏所有的棋子，防止玩家误触，也不是一定要隐藏，就是要让玩家点击这些按钮也没用
        setAllHide();

        //如果是AI
        if(gameManager.isAI())
        {
            //如果是AI，则让AI自己选择
            int choose = gameManager.getAIChoice();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //记得给gameManager设置棋子的选择！！！
            //切记，很重要！！！
            gameManager.setChoice(choose);
        }
        //如果是人，则让人自己选择哪个棋子
        else
        {
            //step 1: 获取可供选择的棋子（只是获得棋子编号：0,1,2,3）
            Queue<Integer> availableChoices = gameManager.getChessAvailable();

            //step 2: 设置这些棋子为可以单击

            //有一种情况是由于点数太小，没有可以移动的棋子
            //这时要提示玩家：没有棋子可以选择！！！
            //同时，一定要设置选择为-1！！！！
            //通过isEmpty来判断是不是没有选择

            if(availableChoices.isEmpty())
            {
                System.out.println("No choice...");
                gameManager.setChoice(-1);//很重要，要设置为-1
            }
            else
            {
                System.out.print("your available choice ");
                for(Integer chessindex:availableChoices)
                {
                    //ps：通过getTurn来获得当前的玩家是谁，然后去设置对应玩家的棋子
                    chessbuttons[gameManager.getTurn()][chessindex].setVisable(true);
                    System.out.print(" " + chessindex);
                }
                System.out.print(" : ");






                /********************************************************/


                //以下这段代码放在棋子的监听器里，即ClickedListener

                //模拟用户点击了扔骰子的按钮
                Scanner in = new Scanner(System.in);
                choice = in.nextInt();//玩家点击了某个棋子

                //隐藏所有的棋子，防止误触
                setAllHide();


                //记得给gameManager设置棋子的选择！！！
                //切记，很重要！！！
                //如果没有选择要设置为-1！！！
                gameManager.setChoice(choice);


                /*********************************************************/
            }
        }

    }
    public void run() {

        while(!gameManager.isGameOver())
        {

            String str = "";
            if(gameManager.getTurn() == Chess.RED) str = "Red";
            else if(gameManager.getTurn() == Chess.GREEN) str = "Green";
            else if(gameManager.getTurn() == Chess.BLUE) str = "Blue";
            else if(gameManager.getTurn() == Chess.YELLOW) str = "Yellow";


            //UI界面显示一下现在轮到谁了
            System.out.println("\ncurrplayer :" + str);
            //先扔骰子
            throwDice();

            //再选择棋子

            selectChess();

            //获取动作列表
            Queue<Action> actions = gameManager.actionlist();

            //翻译为unity动画
            for(Action action:actions)
            {
                System.out.println(action);

            }

         }
         System.exit(0);
    }

    public static void main(String[] args) {
        //必须按照Red，Yellow，Blue，Green的颜色顺序，玩家类型随意指定
        GameManager manager = new GameManager(new AutoAI(Chess.RED),new AutoAI(Chess.YELLOW),
                new AutoAI(Chess.BLUE),new AutoAI(Chess.GREEN));
        UISimulator ui = new UISimulator(manager);
        ui.start();
    }

}

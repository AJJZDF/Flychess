import java.util.Scanner;

/**
 * Created by gitfan on 3/29/18.
 */
public class GameManager extends Thread{

    private BasicAI player[];
    private Chess chessboard[];
    private int dice;
    private int turn;

    public GameManager(BasicAI player1,BasicAI player2,BasicAI player3,BasicAI player4)
    {
        player = new BasicAI[4];
        player[0] = player1;
        player[1] = player2;
        player[2] = player3;
        player[3] = player4;
        chessboard = new Chess[72];
        for(int i = 0 ; i < 72; i++)
        {
            chessboard[i] = new Chess(i);
        }
        dice = -1;
        turn = 0;
    }
    public boolean isGameOver()
    {
        for(int i = 0 ; i < 4 ;i++){
            if(player[i].isFinish()){
                return true;
            }
        }
        return false;
    }
    //提供给扔骰子的按钮，按钮时调用这个函数
    public void setDice(int dice){
        if(dice <= 0 || dice > 6){
            System.out.print("dice out of range in GameManager: setDice(int dice)");
            System.exit(0);
        }
        this.dice = dice;
    }
    //主要是没有棋子可供选择时，需要手动调用setNextTern函数
    public void setNextTern()
    {
        turn = (turn + 1)%4;
    }
    //检测骰子是否已经准备好
    private boolean chessDice()
    {
        if(dice <= 0 || dice > 6){
            return false;
        }
        return true;
    }

    //根据骰子dice,移动playid玩家的第chessindex个棋子后产生的一系列动作
    public Queue<Action> actionlist(int playerid,int chessindex)
    {
        Queue<Action> queue = new Queue<Action>();
        Action action;
        Chess chess;
        if(chessindex == -1)
        {
            turn  = (turn + 1)%4;
            return  null;
        }
        else
        {
            chess = new Chess(player[playerid].getChess(chessindex));
            //位于停机坪
            if(chess.getStatus() == Chess.STATUS_AIRPORT)
            {
                if(dice >= 5 && dice <= 6)
                {
                    chess.setStatus(Chess.STATUS_STARTLINE);
                    chess.setPos(Chess.originPos[chess.getColor()]);
                    chess.clearIndexList();
                    chess.insertToIndexList(new Pair(playerid,chessindex));

                    //记得更新棋盘或者玩家的棋子(自己的棋子或者他人的棋子)

                    player[playerid].setChess(chessindex,chess);//更新玩家棋子，当前棋盘无影响，无需更新棋盘

                    action = new Action(playerid,chessindex,Action.MOVE_TO_STARTLINE);
                    queue.enqueue(action);

                    if(dice == 5){
                        turn = (turn + 1) % 4;//轮到下一个人
                        return queue;
                    }
                    else
                    {
                        return queue;//不用改变tern，还是这个人
                    }
                }
                else
                {
                    turn  = (turn + 1)%4;
                    return  null;
                }
            }
            //位于起飞点
            else if(chess.getStatus() == Chess.STATUS_STARTLINE)
            {
                chess.setStatus(Chess.STATUS_FLYING);
                chess.setPos((chess.getPos() + dice) % 52);

                //初步移动
                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice);
                queue.enqueue(action);


                //可以和自己人合体
                //记得更新自己的棋子和棋盘棋子
                if(chess.mergeTest(chessboard[chess.getPos()]))
                {
                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                    {
                        //插入自己的棋子列表
                        chess.insertToIndexList(pair);
                        //记得隐藏其他的棋子
                        player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                        action = new Action(pair.x,pair.y,Action.HIDE);
                        queue.enqueue(action);
                    }
                    //更新棋盘
                    chessboard[chess.getPos()] = new Chess(chess);
                    //更新自己的棋子
                    player[playerid].setChess(chessindex,chess);
                }
                //可以吃掉其他玩家
                //记得更新别的玩家的棋子，自己的棋子，以及棋盘
                else if(chess.eatTest(chessboard[chess.getPos()]))
                {
                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                    {
                        //坠落，回到停机坪
                        player[pair.x].chesslist[pair.y].setFallen();
                        action = new Action(pair.x,pair.y,Action.FALLEN);
                        queue.enqueue(action);
                    }

                    //更新棋盘
                    chessboard[chess.getPos()] = new Chess(chess);
                    //更新自己的棋子
                    player[playerid].setChess(chessindex,chess);
                }
                //很普通的一步，没有合并或者吃掉，但是有可能是跳步
                //记得更新自己的棋子和棋盘
                else
                {
                    //更新棋盘
                    chessboard[chess.getPos()] = new Chess(chess);
                    //更新自己的棋子
                    player[playerid].setChess(chessindex,chess);
                }

                //正常步
                if(!chess.isLucky())
                {
                    if(dice != 6) turn = (turn + 1)%4;
                    return queue;
                }
                //跳步！！！
                //位于起飞点的棋子不可能会有飞步
                else
                {
                    //有可能合并或者吃掉，但不能再连跳

                    //跳之后记得清空跳之前的位置的棋盘

                    //清空之前的棋子
                    chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                    chessboard[chess.getPos()].clearIndexList();

                    chess.setPos((chess.getPos() + 4)%52);

                    //移动
                    action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                    queue.enqueue(action);

                    //可以和自己人合体
                    //记得更新自己的棋子和棋盘棋子
                    if(chess.mergeTest(chessboard[chess.getPos()]))
                    {
                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //插入自己的棋子列表
                            chess.insertToIndexList(pair);
                            //记得隐藏其他的棋子
                            player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                            action = new Action(pair.x,pair.y,Action.HIDE);
                            queue.enqueue(action);
                        }
                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                        //更新自己的棋子
                        player[playerid].setChess(chessindex,chess);
                    }
                    //可以吃掉其他玩家
                    //记得更新别的玩家的棋子，自己的棋子，以及棋盘
                    else if(chess.eatTest(chessboard[chess.getPos()]))
                    {
                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //坠落，回到停机坪
                            player[pair.x].chesslist[pair.y].setFallen();
                            action = new Action(pair.x,pair.y,Action.FALLEN);
                            queue.enqueue(action);
                        }

                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                        //更新自己的棋子
                        player[playerid].setChess(chessindex,chess);
                    }
                    //很普通的一步，没有合并或者吃掉，但是有可能是跳步
                    //记得更新自己的棋子和棋盘
                    else
                    {
                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                        //更新自己的棋子
                        player[playerid].setChess(chessindex,chess);
                    }

                    if(dice != 6) turn = (turn + 1)%4;
                    return queue;
                }

            }
            //位于飞行途中
            //注意！！！棋子还没有正式移动！！！！
            else if(chess.getStatus() == Chess.STATUS_FLYING)
            {

                //是否接近终点线？？？
                if(chess.presprint(dice))
                {
                    //是否可以合并自己人？？
                    //是否可以直接到达终点？？

                    //记得清除原来的棋盘的位置

                    chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                    chessboard[chess.getPos()].clearIndexList();

                    int lastposition = chess.getPos();


                    chess.setEndLine(dice);

//                    //移动
//                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice);
//                    queue.enqueue(action);

                    if(lastposition != chess.getEntry())
                    {
                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,chess.getEntry() - lastposition);
                        queue.enqueue(action);
                        action = new Action(playerid,chessindex,Action.TURNRIGHT);
                        queue.enqueue(action);
                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice - (chess.getEntry() - lastposition));
                        queue.enqueue(action);
                    }
                    //刚刚好在入口就不用转身了，直接走就可以了
                    else
                    {
                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice);
                        queue.enqueue(action);
                    }

                    if(chess.getStatus() == Chess.STATUS_FINISH)
                    {
                        for(Pair pair:chess.getIndexlist())
                        {
                            player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_FINISH);
                            action = new Action(pair.x,pair.y,Action.FINISHED);
                            queue.enqueue(action);
                        }
                    }
                    //还要考虑是否合并自己人
                    else
                    {
                        //可以和自己人合体
                        //记得更新自己的棋子和棋盘棋子
                        if(chess.mergeTest(chessboard[chess.getPos()]))
                        {
                            for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                            {
                                chess.insertToIndexList(pair);

                                player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                                action = new Action(pair.x,pair.y,Action.HIDE);
                                queue.enqueue(action);
                            }
                            //更新棋盘
                            chessboard[chess.getPos()] = new Chess(chess);
                            //更新自己的棋子
                            player[playerid].setChess(chessindex,chess);
                        }
                        //记得更新自己的棋子和棋盘
                        else
                        {
                            //更新棋盘
                            chessboard[chess.getPos()] = new Chess(chess);
                            //更新自己的棋子
                            player[playerid].setChess(chessindex,chess);
                        }
                    }

                    if(dice != 6) turn = (turn + 1)%4;
                    return queue;

                }

                //是否已经进入终点线
                else if(chess.sprint())
                {

                    chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                    chessboard[chess.getPos()].clearIndexList();

                    int lastpos = chess.getPos();

                    //反弹~~~
                    boolean rebounded = chess.rebound(dice);


                    //记得添加action
                    //需要考虑直接到达终点,直接到达终点不用考虑反弹
                    //否则需要考虑反弹并且需要考虑是否合并

                    if(chess.getStatus() == Chess.STATUS_FINISH)
                    {
                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice);
                        queue.enqueue(action);
                        for(Pair pair:chess.getIndexlist())
                        {
                            player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_FINISH);
                            action = new Action(pair.x,pair.y,Action.FINISHED);
                            queue.enqueue(action);
                        }
                    }
                    else
                    {
                        //如果反弹
                        if(rebounded)
                        {
                            int endpoint = chess.endPoint();

                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,endpoint - lastpos);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.REVERSE);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,endpoint - chess.getPos());
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.REVERSE);
                            queue.enqueue(action);
                        }
                        //否则
                        else
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice);
                            queue.enqueue(action);
                        }


                        //可以和自己人合体
                        //记得更新自己的棋子和棋盘棋子
                        if(chess.mergeTest(chessboard[chess.getPos()]))
                        {
                            for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                            {
                                chess.insertToIndexList(pair);

                                player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                                action = new Action(pair.x,pair.y,Action.HIDE);
                                queue.enqueue(action);
                            }
                            //更新棋盘
                            chessboard[chess.getPos()] = new Chess(chess);
                            //更新自己的棋子
                            player[playerid].setChess(chessindex,chess);
                        }
                        //记得更新自己的棋子和棋盘
                        else
                        {
                            //更新棋盘
                            chessboard[chess.getPos()] = new Chess(chess);
                            //更新自己的棋子
                            player[playerid].setChess(chessindex,chess);
                        }
                    }

                    if(dice != 6) turn = (turn + 1)%4;
                    return queue;
                }
                //普通线路
                else
                {
                    //先清除以前的棋盘
                    chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                    chessboard[chess.getPos()].clearIndexList();


                    //基础移动
                    chess.setPos((chess.getPos() + dice) % 52);
                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice);
                    queue.enqueue(action);


                    //可以和自己人合体
                    //记得更新自己的棋子和棋盘棋子
                    if(chess.mergeTest(chessboard[chess.getPos()]))
                    {
                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //插入自己的棋子列表
                            chess.insertToIndexList(pair);
                            //记得隐藏其他的棋子
                            player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                            action = new Action(pair.x,pair.y,Action.HIDE);
                            queue.enqueue(action);
                        }
                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                    }
                    //可以吃掉其他玩家
                    //记得更新别的玩家的棋子，自己的棋子，以及棋盘
                    else if(chess.eatTest(chessboard[chess.getPos()]))
                    {
                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //坠落，回到停机坪
                            player[pair.x].chesslist[pair.y].setFallen();
                            action = new Action(pair.x,pair.y,Action.FALLEN);
                            queue.enqueue(action);
                        }
                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                    }
                    else
                    {
                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                    }


                    //正常步
                    if(!chess.isLucky())
                    {
                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                        //更新自己的棋子
                        player[playerid].setChess(chessindex,chess);

                    }
                    else
                    {
                        //先特判一下是不是可以导致飞步的跳步；
                        if(chess.getPos() == chess.getPreFlyingPoint())
                        {
                            //先清除以前的棋盘
                            chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                            chessboard[chess.getPos()].clearIndexList();

                            //再移动
                            chess.setPos((chess.getPos() + 4)%52);
                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                            queue.enqueue(action);


                            //可以和自己人合体
                            //记得更新自己的棋子和棋盘棋子
                            if(chess.mergeTest(chessboard[chess.getPos()]))
                            {
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //插入自己的棋子列表
                                    chess.insertToIndexList(pair);
                                    //记得隐藏其他的棋子
                                    player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                                    action = new Action(pair.x,pair.y,Action.HIDE);
                                    queue.enqueue(action);
                                }
                                //更新棋盘
                                chessboard[chess.getPos()] = new Chess(chess);
                            }
                            //可以吃掉其他玩家
                            //记得更新别的玩家的棋子，自己的棋子，以及棋盘
                            else if(chess.eatTest(chessboard[chess.getPos()]))
                            {
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //坠落，回到停机坪
                                    player[pair.x].chesslist[pair.y].setFallen();
                                    action = new Action(pair.x,pair.y,Action.FALLEN);
                                    queue.enqueue(action);
                                }
                                //更新棋盘
                                chessboard[chess.getPos()] = new Chess(chess);
                            }
                            else
                            {
                                //更新棋盘
                                chessboard[chess.getPos()] = new Chess(chess);
                            }
                        }

                        //要先先判断飞步，因为飞步也是跳步的一种
                        if(chess.isSuperLucky())
                        {
                            //飞步

                            //是否可以攻击别人？？？

                            //飞步前要先旋转！！！！
                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                            queue.enqueue(action);

                             int attackpos = chess.getAttackPos();
                             if(chess.attackTest(chessboard[attackpos]))
                             {
                                 action = new Action(playerid,chessindex,Action.QUICK_MOVE,3);
                                 queue.enqueue(action);

                                 //记得修改玩家的棋子和棋盘
                                 for(Pair pair:chessboard[attackpos].getIndexlist())
                                 {
                                     //坠落，回到停机坪
                                     player[pair.x].chesslist[pair.y].setFallen();
                                     action = new Action(pair.x,pair.y,Action.FALLEN);
                                     queue.enqueue(action);
                                 }

                                 //清空中间的棋盘
                                 chessboard[attackpos].setStatus(Chess.STATUS_EMPTY);
                                 chessboard[attackpos].clearIndexList();


                                 //踢完人继续走
                                 action = new Action(playerid,chessindex,Action.QUICK_MOVE,3);
                                 queue.enqueue(action);
                             }
                             else
                             {
                                 //直接飞过对面
                                 action = new Action(playerid,chessindex,Action.QUICK_MOVE,6);
                                 queue.enqueue(action);
                             }

                            //先清除棋子以前的棋盘
                            chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                            chessboard[chess.getPos()].clearIndexList();
                            //再前进
                            chess.setPos(chess.getFlyingPoint());

                            //有人吗？？自己人还是别人？

                            //合体
                            if(chess.mergeTest(chessboard[chess.getPos()]))
                            {

                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //插入自己的棋子列表
                                    chess.insertToIndexList(pair);
                                    //记得隐藏其他的棋子
                                    player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                                    action = new Action(pair.x,pair.y,Action.HIDE);
                                    queue.enqueue(action);
                                }
                            }
                            //吃掉
                            else if(chess.eatTest(chessboard[chess.getPos()]))
                            {
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //坠落，回到停机坪
                                    player[pair.x].chesslist[pair.y].setFallen();
                                    action = new Action(pair.x,pair.y,Action.FALLEN);
                                    queue.enqueue(action);
                                }
                            }

                            //右转
                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                            queue.enqueue(action);

                            //直接删除棋盘，因为还要继续跳
                            chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                            chessboard[chess.getPos()].clearIndexList();


                            //在走四步
                            //会有人吗？
                            //会是自己人吗，还是其他人

                            chess.setPos((chess.getPos() + 4)%52);
                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                            queue.enqueue(action);


                            //合体
                            if(chess.mergeTest(chessboard[chess.getPos()]))
                            {
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //插入自己的棋子列表
                                    chess.insertToIndexList(pair);
                                    //记得隐藏其他的棋子
                                    player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                                    action = new Action(pair.x,pair.y,Action.HIDE);
                                    queue.enqueue(action);
                                }
                            }
                            //吃掉
                            else if(chess.eatTest(chessboard[chess.getPos()]))
                            {
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //坠落，回到停机坪
                                    player[pair.x].chesslist[pair.y].setFallen();
                                    action = new Action(pair.x,pair.y,Action.FALLEN);
                                    queue.enqueue(action);
                                }
                            }

                            //更新棋盘
                            chessboard[chess.getPos()] = new Chess(chess);
                            //更新自己的棋子
                            player[playerid].setChess(chessindex,chess);
                        }
                        else
                        {
                            //如果不在entry才可以跳步，否则就会超过entry了
                            if(chess.getPos() != chess.getEntry())
                            {
                                //普通跳步
                                chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                                chessboard[chess.getPos()].clearIndexList();

                                //前进
                                //前面有人吗？
                                //自己人还是别人？

                                chess.setPos((chess.getPos() + 4)%52);
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                                queue.enqueue(action);


                                //合体
                                if(chess.mergeTest(chessboard[chess.getPos()]))
                                {
                                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                    {
                                        //插入自己的棋子列表
                                        chess.insertToIndexList(pair);
                                        //记得隐藏其他的棋子
                                        player[pair.x].chesslist[pair.y].setStatus(Chess.STATUS_HIDING);
                                        action = new Action(pair.x,pair.y,Action.HIDE);
                                        queue.enqueue(action);
                                    }
                                }
                                //吃掉
                                else if(chess.eatTest(chessboard[chess.getPos()]))
                                {
                                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                    {
                                        //坠落，回到停机坪
                                        player[pair.x].chesslist[pair.y].setFallen();
                                        action = new Action(pair.x,pair.y,Action.FALLEN);
                                        queue.enqueue(action);
                                    }
                                }
                            }

                            //刚好到达入口，需要右转
                            if(chess.getPos() == chess.getEntry())
                            {
                                action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                queue.enqueue(action);
                            }

                            player[playerid].chesslist[chessindex] = new Chess(chess);
                            chessboard[chess.getPos()] = new Chess(chess);
                        }
                    }
                    if(dice != 6) turn = (turn + 1)%4;

                    return queue;
                }
            }
            else
            {
                if(dice != 6) turn = (turn + 1)%4;
                return null;
            }
        }
    }

    public void run() {
        while(!isGameOver())
        {
            for(int i = 0 ; i < 4;i++)
            {
                if(player[i].isMyturn(turn))
                {

                    while(player[i].isMyturn(turn))
                    {

                        String str ="";
                        if(player[i].color == Chess.RED) str = "Red";
                        else if(player[i].color == Chess.GREEN) str = "Green";
                        else if(player[i].color == Chess.BLUE) str = "Blue";
                        else if(player[i].color == Chess.YELLOW) str = "Yellow";

                        Queue<Action> actions;

                        if(player[i].getKind() == BasicAI.PEOPLE)
                        {
                            /*
                             * 显示扔骰子的按钮
                             * 只有等玩家点击扔按钮才可以开始扔骰子
                             * 记得在触发器里面调用setDice(dice)
                             */


                            System.out.println("\ncurrplayer :" + str);

                            System.out.print("please throw a dice: ");

                            Scanner in  = new Scanner(System.in);

                            str = in.next();

                            //临时用这个代替扔骰子
                            //扔骰子的功能完成后记得去掉
                            int currdice = ((int)(Math.random()*1000000))%6 + 1;
                            setDice(currdice);

                            System.out.println("dice: " + currdice);

                            while(!chessDice()) try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            PlayerAI ai = (PlayerAI) player[i];
                            Queue<Integer> choicelist = ai.choice(this.dice,chessboard);

                            if(choicelist != null)
                            {
                                System.out.print("your available choice: ");
                                for(int choice:choicelist)
                                {
                                    System.out.print(" " + choice);
                                }
                                System.out.print(" :");

                                int val;

                                val = in.nextInt();

                                actions = actionlist(i,val);

                                for(Action action:actions)
                                {
                                    System.out.println(action);
                                }
                            }
                            else
                            {
                                //没有棋子可供选择时，需要手动调用setNextTern函数！！！
                                setNextTern();
                                System.out.println("No choice");
                            }
                        }
                        else
                        {
                        /*
                         * 显示扔骰子的按钮
                         * 因为是机器人，所以可以立即扔骰子，但是要显示人扔出的骰子的动画
                         * 记得在触发器里面调用setDice(dice)
                         */

                            System.out.println("\nAIplayer :" + str);

                            System.out.println("Throwing a dice...");

                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            //临时用这个代替扔骰子
                            //扔骰子的功能完成后记得去掉
                            int currdice = ((int)(Math.random()*1000000))%6 + 1;
                            setDice(currdice);

                            System.out.println("dice: " + currdice);

                            //不断扫描骰子状态，判断骰子有没有更新
                            while(!chessDice()) try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            AutoAI ai = (AutoAI) player[i];
                            //AI自己选择移动的棋子
                            int choose = ai.choice(this.dice,chessboard);

                            if(choose != -1)
                            {
                                actions = actionlist(i,choose);

                                for(Action action:actions)
                                {
                                    System.out.println(action);
                                }

                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                //没有棋子可供选择时，需要手动调用setNextTern函数！！！
                                setNextTern();
                                System.out.println("No choice");
                            }
                        }

                        //每轮都设置骰子为未扔状态
                        dice = -1;
                    }
                    break;
                }
            }

        }
    }
    public static void main(String[] args) {
        //必须按照Red，Yellow，Blue，Green的颜色顺序，玩家类型随意指定
        GameManager manager = new GameManager(new AutoAI(Chess.RED),new AutoAI(Chess.YELLOW),
                new AutoAI(Chess.BLUE),new AutoAI(Chess.GREEN));
        manager.start();
    }
}

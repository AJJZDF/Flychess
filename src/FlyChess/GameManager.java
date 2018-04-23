package FlyChess;

/**
 * Created by gitfan on 3/29/18.
 */

//单机版GameManager
public class GameManager{

    private BasicAI player[];
    private Chess chessboard[];
    private boolean waitingdice;//还在等待扔骰子
    private boolean waitingchoice;//还在等待选择

    private int dice;//当前的骰子
    private int turn;//当前轮到谁？
    private int choice;//当前的选择是什么?

    private int players_cnt = -1;

    public GameManager(BasicAI red,BasicAI yellow,BasicAI blue,BasicAI green)
    {
        player = new BasicAI[4];
        player[0] = red;
        player[1] = yellow;
        player[2] = blue;
        player[3] = green;
        chessboard = new Chess[72];
        for(int i = 0 ; i < 72; i++)
        {
            chessboard[i] = new Chess(i);
        }
        dice = -1;
        choice = -1;
        turn = 0;

        waitingdice = true;
        waitingchoice = true;

        players_cnt = 4;

    }
    public GameManager(BasicAI red,BasicAI yellow,BasicAI blue)
    {
        player = new BasicAI[3];
        player[0] = red;
        player[1] = yellow;
        player[2] = blue;
        chessboard = new Chess[72];
        for(int i = 0 ; i < 72; i++)
        {
            chessboard[i] = new Chess(i);
        }
        dice = -1;
        choice = -1;
        turn = 0;

        waitingdice = true;
        waitingchoice = true;

        players_cnt = 3;
    }
    public GameManager(BasicAI red,BasicAI blue)
    {
        player = new BasicAI[3];//好像必须得三个...虽然只需要两个位置..但是蓝色玩家一定要处于第2个位置上（从0数起）
        player[0] = red;
        player[2] = blue;
        chessboard = new Chess[72];
        for(int i = 0 ; i < 72; i++)
        {
            chessboard[i] = new Chess(i);
        }
        dice = -1;
        choice = -1;
        turn = 0;

        waitingdice = true;
        waitingchoice = true;

        players_cnt = 2;
    }

    //返回下一次轮到的位置
    //如果是四个人，步长为1，如果是两个人，步长是2，如果是三个人，则红到黄，黄到蓝步长为1，但蓝不经过绿，直接到红，步长为2
    private void nextTurn()
    {
        if(players_cnt == 4) turn = (turn + 1)%4;
        else if(players_cnt == 2) turn = (turn +2)%4;
        else if(players_cnt == 3)
        {
            //三个玩家
            if(turn == 2) turn = 0;//蓝色 --> 红色
            else turn = (turn + 1) %4;
        }
    }


    public boolean isGameOver()
    {

        if(players_cnt == 4 || players_cnt == 3)
        {
            for(int i = 0 ; i < players_cnt ;i++){
                if(player[i].isFinish()){
                    return true;
                }
            }
            return false;
        }
        else if(players_cnt == 2)
        {
            if(player[0].isFinish() || player[2].isFinish()) return true;
            else return false;
        }
        //未定义的人数...直接返回false？
        return false;
    }
    //提供给扔骰子的按钮，按按钮时调用这个函数
    public void setDice(int dice){
        if(dice <= 0 || dice > 6){
            System.out.print("dice out of range in FlyChess.GameManager: setDice(int dice)");
            System.exit(0);
        }

        waitingdice = false;//玩家已经投掷骰子了
        this.dice = dice;
        //扔完骰子后开始等待玩家选择棋子
        this.waitingchoice = true;
    }
    //提供给点击棋子的按钮，玩家点击棋子时调用这个函数
    //用来设置当前选择的棋子,如果没有棋子可以选择请使用setChoice(-1)
    public void setChoice(int choice)
    {
        //扔了骰子才可以进行选择
        if(waitingdice){
            System.out.println("unexpected error in FlyChess.GameManager: setChoice(int choice)");
            System.exit(0);
        }

        waitingchoice = false;//取消等待选择的标记
        this.choice = choice;

        //为下一轮游戏做准备
        waitingdice = true;
    }
    //检测骰子是否已经准备好
    public boolean waitDice()
    {
        return waitingdice;
    }

    //检测玩家是否已经选择棋子
    public boolean waitChoice()
    {
        return waitingchoice;
    }

    //返回当前玩家可以选择的棋子
    //前提：扔了骰子才可以调用
    //如果返回的queue的size为0，那么没有棋子可以选择
    public Queue<Integer> getChessAvailable()
    {
        if(waitingdice){
            System.out.println("Unexpected error in FlyChess.GameManager: getChessAvailable(int playerid)");
            System.exit(0);
        }
        PlayerAI playerAI = (PlayerAI) player[getTurn()];
        return playerAI.available_choice(dice);
    }

    //返回当前玩家可以选择的棋子(已翻译成一系列指令集)
    //前提：扔了骰子才可以调用
    //如果返回的queue的size为0，那么没有棋子可以选择
    public Queue<Action> getChessAvailable_Action()
    {
        if(waitingdice){
            System.out.println("Unexpected error in FlyChess.GameManager: getChessAvailable(int playerid)");
            System.exit(0);
        }
        PlayerAI playerAI = (PlayerAI) player[getTurn()];
        Queue<Integer> choices = playerAI.available_choice(dice);
        Queue<Action> actionlist = new Queue<Action>();
        for(Integer choose:choices)
        {
            actionlist.enqueue(new Action(getTurn(),choose,Action.ACTIVATE));
        }
        return actionlist;
    }


    //设置玩家为挂机模式
    public void switchToAI(int playerid)
    {
        //只有玩家模式才可以转换位挂机
        if(player[playerid].getKind() != BasicAI.PEOPLE)
        {
            System.out.println("Unexpected error in FlyChess.GameManager: switchToAI(int playerid)");
            System.exit(0);
        }

        PlayerAI playerAI = (PlayerAI) player[playerid];
        playerAI.switchToAI();
        player[playerid] = playerAI;
    }

    //从挂机模式中恢复
    public void switchToUser(int playerid)
    {
        //只有玩家AI模式才可以恢复为玩家模式，全自动AI不可以切换为玩家模式
        if(player[playerid].getKind() != BasicAI.PLAYERAI)
        {
            System.out.println("Unexpected error in FlyChess.GameManager: switchToUser(int playerid)");
            System.exit(0);
        }

        PlayerAI playerAI = (PlayerAI) player[playerid];
        playerAI.switchToUser();
        player[playerid] = playerAI;
    }

    //AI自己选择棋子
    public int getAIChoice()
    {
        //只有扔了骰子AI才可以自动选择
        if(waitingdice){
            System.out.println("Unexpected error in FlyChess.GameManager: getAIChoice(int playerid)");
            System.exit(0);
        }
        return player[getTurn()].ai_choice(dice,chessboard);
    }

    //联机部分可能用到
    //主要提供给UI界面，获取现在轮到谁玩游戏
    //UI那里可以根据这个函数确定轮到谁玩游戏
    //然后设置相关的界面（比如扔骰子，只有轮到的人才出现那个可以出现扔骰子的按钮）
    public int getTurn()
    {
        return turn;
    }

    //判断现在是不是AI在玩游戏
    //还是提供给UI界面，主要是给UI界面用来确定扔骰子时是自动扔，还是等人点击才扔?
    public boolean isAI()
    {
        return (player[getTurn()].getKind() == BasicAI.AUTOAI || player[getTurn()].getKind() == BasicAI.PLAYERAI );
    }
    public boolean isAI(int playid)
    {
        //没加范围检测，应该不会有问题吧...
        return (player[playid].getKind() == BasicAI.AUTOAI || player[playid].getKind() == BasicAI.PLAYERAI );
    }


    private static final int RIGHT = 1;
    private static final int LEFT = 2;
    private static final int NOTURN = 3;

    //转向测试
    private static int turnTest(int prePos,int afterPos)
    {
        if((prePos == 0 || (prePos <= 51 && prePos > 47 ) ) && afterPos > 1 && afterPos <= 5) return RIGHT;
        if(prePos <= 4 && afterPos >= 5) return LEFT;
        if(prePos < 8 && afterPos > 8) return RIGHT;
        if(prePos < 14 && afterPos > 14) return RIGHT;
        if(prePos <= 17 && afterPos >= 18) return LEFT;
        if(prePos < 21 && afterPos > 21) return RIGHT;
        if(prePos < 27 && afterPos > 27) return RIGHT;
        if(prePos <= 30 && afterPos >= 31) return LEFT;
        if(prePos < 34 && afterPos > 34) return RIGHT;
        if(prePos < 40 && afterPos > 40) return RIGHT;
        if(prePos <= 43 && afterPos >= 44) return LEFT;
        if(prePos < 47 && afterPos > 47) return RIGHT;
        return NOTURN;
    }
    private static boolean turnLeftTest(int prePos,int afterPos)
    {
        if(prePos <= 4 && afterPos >= 5) return true;
        if(prePos <= 17 && afterPos >= 18) return true;
        if(prePos <= 30 && afterPos >= 31) return true;
        if(prePos <= 43 && afterPos >= 44) return true;
        return false;
    }
    private static boolean turnRightTest(int prePos,int afterPos)
    {
        if((prePos == 0 || (prePos <= 51 && prePos > 47 ) ) && afterPos > 1 && afterPos <= 4) return true;
        if(prePos < 8 && afterPos > 8) return true;
        if(prePos < 14 && afterPos > 14) return true;
        if(prePos < 21 && afterPos > 21) return true;
        if(prePos < 27 && afterPos > 27) return true;
        if(prePos < 34 && afterPos > 34) return true;
        if(prePos < 40 && afterPos > 40) return true;
        if(prePos < 47 && afterPos > 47) return true;
        return false;
    }
    private static int getRightCorner(int prePos,int afterPos)
    {
        if(prePos < 8 && afterPos > 8) return 8;
        if(prePos < 14 && afterPos > 14) return 14;
        if(prePos < 21 && afterPos > 21) return 21;
        if(prePos < 27 && afterPos > 27) return 27;
        if(prePos < 34 && afterPos > 34) return 34;
        if(prePos < 40 && afterPos > 40) return 40;
        if(prePos < 47 && afterPos > 47) return 47;
        if((prePos == 0 || (prePos <= 51 && prePos >47 )) && afterPos > 1) return 1;
        return -1;
    }
    private static int getLeftCorner(int prePos,int afterPos)
    {
        if(prePos <= 4 && afterPos >= 5) return 5;
        if(prePos <= 17 && afterPos >= 18) return 18;
        if(prePos <= 30 && afterPos >= 31) return 31;
        if(prePos <= 43 && afterPos >= 44) return 44;
        return -1;
    }
    private static boolean isLeftCorner(int pos)
    {
        if(pos == 5 || pos == 18 || pos == 31 || pos == 44) return true;
        return false;
    }
    private static boolean isRightCorner(int pos)
    {
        if(pos == 8 || pos == 14 || pos == 21 || pos == 27 || pos == 34 || pos == 40 || pos == 47 || pos == 1) return true;
        return false;
    }

    //用户选完棋子后，产生的一系列动作
    public Queue<Action> actionlist()
    {
        //只有选了棋子才能发生动作
        if(waitingchoice)
        {
            System.out.println("Unexpected error in FlyChess.GameManager: actionlist()");
            System.exit(0);
        }


        Queue<Action> queue = new Queue<Action>();
        Action action;
        Chess chess;
        int playerid = getTurn();
        int chessindex = choice;
        int currPos = -1, leftStep = -1,turnKind = NOTURN,currStep = -1;
        if(chessindex < 0)
        {
            nextTurn();
            return  queue;
        }
        else
        {
            chess = new Chess(player[playerid].getChess(chessindex));
            //位于停机坪
            //finish
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
                        nextTurn(); //轮到下一个人
                        return queue;
                    }
                    else
                    {
                        return queue;//不用改变tern，还是这个人
                    }
                }
                else
                {
                    nextTurn();
                    return  queue;
                }
            }
            //位于起飞点
            //finish
            else if(chess.getStatus() == Chess.STATUS_STARTLINE)
            {
                currPos = chess.getPos();
                chess.setStatus(Chess.STATUS_FLYING);
                chess.setPos((chess.getPos() + dice) % 52);
                leftStep = dice;

                if(!chess.eatTest(chessboard[chess.getPos()]))
                {
//                    //初步移动
//                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,dice);
//                    queue.enqueue(action);

                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                    queue.enqueue(action);
                    currPos = (currPos + 1)%52;
                    leftStep -= 1;

                    action = new Action(playerid,chessindex,Action.TURNLEFT);
                    queue.enqueue(action);

                    if(leftStep != 0)
                    {
                        //需要左转
                        if(leftStep >= 4)
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,4);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.TURNLEFT);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep - 3);
                            queue.enqueue(action);
                            currPos = (currPos + leftStep) % 52;
                            leftStep = 0;
                        }
                        //直接走
                        else
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                            queue.enqueue(action);
                            currPos = (currPos + leftStep)%52;
                            leftStep -= leftStep;

                        }
                    }
                }

                //可以和自己人合体
                //记得更新自己的棋子和棋盘棋子
                if(chess.mergeTest(chessboard[chess.getPos()]))
                {
                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                    {
                        //插入自己的棋子列表
                        chess.insertToIndexList(pair);
                        //记得隐藏其他的棋子
                        player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                        action = new Action(pair.playerId,pair.chessId,Action.HIDE);
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
//                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,dice - 1);
//                    if(dice - 1 != 0) queue.enqueue(action);

                    boolean lefflag = false;

                    if(dice - 1 != 0)
                    {
//                        action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,dice - 1);
//                        queue.enqueue(action);

                        leftStep = dice -1;
                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                        queue.enqueue(action);
                        currPos = (currPos + 1)%52;
                        leftStep -= 1;

                        //左转
                        action = new Action(playerid,chessindex,Action.TURNLEFT);
                        queue.enqueue(action);

                        if(leftStep != 0)
                        {
                            //需要左转
                            if(leftStep >= 4)
                            {
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,4);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep - 3);
                                queue.enqueue(action);
                                currPos = (currPos + leftStep) % 52;
                                leftStep = 0;
                            }
                            else if(leftStep == 3)
                            {
                                lefflag = true;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep + 1);
                                queue.enqueue(action);
                                currPos = (currPos + leftStep)%52;
                                leftStep -= leftStep;
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);

                            }
                            else
                            {
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                queue.enqueue(action);
                                currPos = (currPos + leftStep)%52;
                                leftStep -= leftStep;
                            }
                        }
                    }


                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                    {
                        //坠落，回到停机坪
                        player[pair.playerId].chesslist[pair.chessId].setFallen();
                        action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                        queue.enqueue(action);
                    }

//                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,1);
//                    queue.enqueue(action);

                    leftStep = 1;
                    turnKind = turnTest(currPos,currPos + 1);
                    //需要左转
                    if(turnKind == LEFT)
                    {
                        if(lefflag)
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                            queue.enqueue(action);
                            leftStep -= 1;
                            currPos = (currPos + 1)%52;
                        }
                        else
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.TURNLEFT);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                            queue.enqueue(action);
                            leftStep -= 1;
                            currPos = (currPos + 1)%52;
                        }

                    }
                    else
                    {
                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                        queue.enqueue(action);
                        if(isRightCorner(currPos + 1))
                        {
                            action = new Action(playerid,chessindex,Action.TURNLEFT);
                            queue.enqueue(action);
                        }
                        leftStep = 0;
                        currPos = (currPos + 1)%52;
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
                    if(dice != 6) nextTurn();
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

                    currPos = chess.getPos();
                    chess.setPos((chess.getPos() + 4)%52);
                    leftStep = 4;

                    if(!chess.eatTest(chessboard[chess.getPos()]))
                    {
                        //移动
//                        action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.QUICK_MOVE,4);
//                        queue.enqueue(action);
                        turnKind = turnTest(currPos,currPos + leftStep);
                        if(turnKind == LEFT)
                        {
                            //quickmove很烦啊，还要判断下是不是偶数，不是偶数需要分解
                            if((getLeftCorner(currPos,currPos + leftStep) - currPos)%2 == 0)
                            {
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,getLeftCorner(currPos,currPos + leftStep) - currPos);
                                queue.enqueue(action);
                            }
                            else
                            {
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,getLeftCorner(currPos,currPos + leftStep) - currPos -1);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                            }
                            leftStep -= (getLeftCorner(currPos,currPos + leftStep) - currPos - 1);
                            action = new Action(playerid,chessindex,Action.TURNLEFT);
                            queue.enqueue(action);
                            //warning:quick move without check
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                            queue.enqueue(action);
                            leftStep = 0;
                            currPos = (currPos + 4)%52;
                        }
                        else if(turnKind == RIGHT)
                        {
                            if((getRightCorner(currPos,currPos + leftStep) - currPos)%2 == 0)
                            {
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,getRightCorner(currPos,currPos + leftStep) - currPos);
                                queue.enqueue(action);
                            }
                            else
                            {
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,getRightCorner(currPos,currPos + leftStep) - currPos -1);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                            }
                            leftStep -= (getRightCorner(currPos,currPos + leftStep) - currPos);
                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                            queue.enqueue(action);
                            leftStep -= leftStep;
                            currPos = (currPos + 4)%52;
                        }
                        else
                        {
                            //移动
                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,leftStep);
                            queue.enqueue(action);
                            if(isRightCorner((currPos + leftStep)%52))
                            {
                                action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                queue.enqueue(action);
                            }
                            leftStep -= leftStep;
                            currPos = (currPos + leftStep)%52;
                        }
                    }


                    //可以和自己人合体
                    //记得更新自己的棋子和棋盘棋子
                    if(chess.mergeTest(chessboard[chess.getPos()]))
                    {
                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //插入自己的棋子列表
                            chess.insertToIndexList(pair);
                            //记得隐藏其他的棋子
                            player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                            action = new Action(pair.playerId,pair.chessId,Action.HIDE);
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
//                        action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.QUICK_MOVE,2);
//                        queue.enqueue(action);
//                        action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,1);
//                        queue.enqueue(action);

                        leftStep = 2;
                        action = new Action(playerid,chessindex,Action.QUICK_MOVE,2);
                        queue.enqueue(action);
                        if(isRightCorner(currPos + leftStep))
                        {
                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                            queue.enqueue(action);
                        }

                        leftStep = 1;
                        currPos = currPos + 2;

                        turnKind = turnTest(currPos,currPos + 1);

                        if(turnKind == LEFT)
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.TURNLEFT);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                            queue.enqueue(action);
                        }
                        else
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                            queue.enqueue(action);
                        }

                        leftStep = 0;
                        currPos = currPos + 1;

                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //坠落，回到停机坪
                            player[pair.playerId].chesslist[pair.chessId].setFallen();
                            action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                            queue.enqueue(action);
                        }
                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                        queue.enqueue(action);
                        leftStep = 0;
                        currPos = currPos + 1;

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

                    if(dice != 6) nextTurn();
                    return queue;
                }

            }
            //位于飞行途中
            //注意！！！棋子还没有正式移动！！！！
            else if(chess.getStatus() == Chess.STATUS_FLYING)
            {

                //是否接近终点线？？？
                //finish
                if(chess.presprint(dice))
                {
                    //是否可以合并自己人？？
                    //是否可以直接到达终点？？

                    //记得清除原来的棋盘的位置

                    chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                    chessboard[chess.getPos()].clearIndexList();

                    int lastposition = chess.getPos();
                    currPos = chess.getPos();



                    chess.setEndLine(dice);

//                    //移动
//                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,dice);
//                    queue.enqueue(action);

                    if(lastposition != chess.getEntry())
                    {
//                        action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,chess.getEntry() - lastposition);
//                        queue.enqueue(action);

                        leftStep = chess.getEntry() - lastposition;

                        turnKind = turnTest(currPos,(currPos + leftStep)%52) ;

                        if(turnKind == RIGHT)
                        {
                            currStep = getRightCorner(currPos,(currPos+leftStep)%52) - currPos;
                            if(currStep < 0) currStep += 52;
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                            queue.enqueue(action);
                            leftStep -= currStep;
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                            queue.enqueue(action);
                        }
                        else
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                            queue.enqueue(action);
                        }

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
                            player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_FINISH);
                            action = new Action(pair.playerId,pair.chessId,Action.FINISHED);
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

                                player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                                action = new Action(pair.playerId,pair.chessId,Action.HIDE);
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

                    if(dice != 6) nextTurn();
                    return queue;

                }
                //是否已经进入终点线
                //finish
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
                            player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_FINISH);
                            action = new Action(pair.playerId,pair.chessId,Action.FINISHED);
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

                                player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                                action = new Action(pair.playerId,pair.chessId,Action.HIDE);
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

                    if(dice != 6) nextTurn();
                    return queue;
                }
                //普通线路
                else
                {
                    //先清除以前的棋盘
                    chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                    chessboard[chess.getPos()].clearIndexList();

                    currPos = chess.getPos();//4
                    leftStep = dice;//2

                    //基础移动
                    chess.setPos((chess.getPos() + dice) % 52);//6

                    if( !chess.eatTest(chessboard[chess.getPos()]))
                    {
//                        action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,dice);
//                        queue.enqueue(action);

                        turnKind = turnTest(currPos,(currPos + leftStep) % 52);

                        boolean changeturn = false;
                        boolean turnlef = false;
                        boolean turnrig = false;
                        boolean emptySpace = false;

                        //需要左转
                        if(turnKind == LEFT)
                        {

                            currStep = getLeftCorner(currPos,(currPos + leftStep) % 52) - currPos;// 2
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.TURNLEFT);
                            queue.enqueue(action);

                            leftStep -= (currStep - 1);//5
                            currPos = (currPos + currStep - 1)%52;//30
                            if(currStep -1 == 0) emptySpace = true;
                            changeturn = true;
                            turnlef = true;
                        }
                        //需要右转
                        else if(turnKind == RIGHT)
                        {

                            currStep = getRightCorner(currPos,(currPos + leftStep) % 52) - currPos;
                            if(currStep < 0) currStep += 52;
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                            queue.enqueue(action);
                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                            queue.enqueue(action);
                            leftStep -= currStep;
                            currPos = (currPos + currStep)%52;
                            changeturn = true;
                            turnrig = true;
                        }
                        else
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,dice);
                            queue.enqueue(action);
                            if(isRightCorner((currPos + dice)%52))
                            {
                                action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                queue.enqueue(action);
                            }
                            currPos = (currPos + dice)%52;
                            leftStep -= dice;
                            changeturn = false;
                        }

                        if(changeturn)
                        {
                            // 30 30+5
                            turnKind = turnTest(currPos,(currPos + leftStep) % 52);//LEFT

                            if(emptySpace){

                                if(turnRightTest(currPos,(currPos + leftStep) %52)){
                                    turnKind = RIGHT;
                                }
                            }
                            if(turnlef)
                            {
                                if(turnRightTest(currPos,(currPos + leftStep) %52)){
                                    turnKind = RIGHT;
                                }
                            }
                            if(turnrig)
                            {
                                if(turnLeftTest(currPos,(currPos + leftStep)%52))
                                {
                                    turnKind = LEFT;
                                }
                            }

                            //需要左转
                            if(turnKind == LEFT && !turnlef)
                            {
                                currStep = getLeftCorner(currPos,(currPos + leftStep) % 52) - currPos;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);
                                leftStep -= (currStep - 1);
                                currPos = (currPos + currStep - 1)%52;

                                currStep = leftStep;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                queue.enqueue(action);
                                leftStep -= leftStep;
                                currPos = (currPos + leftStep)%52;
                            }
                            //需要右转
                            else if(turnKind == RIGHT && !turnrig)
                            {
                                currStep = getRightCorner(currPos,(currPos + leftStep) % 52) - currPos;
                                if(currStep < 0) currStep += 52;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                queue.enqueue(action);
                                leftStep -= currStep;
                                currPos = (currPos + currStep)%52;

                                currStep = leftStep;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                queue.enqueue(action);
                                leftStep -= leftStep;
                                currPos = (currPos + leftStep)%52;

                            }
                            else
                            {
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                queue.enqueue(action);
                                if(isRightCorner((currPos + leftStep)%52))
                                {
                                    action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                    queue.enqueue(action);
                                }
                                currPos = (currPos + leftStep)%52;
                                leftStep -= leftStep;
                            }
                        }
                    }

                    //可以和自己人合体
                    //记得更新自己的棋子和棋盘棋子
                    if(chess.mergeTest(chessboard[chess.getPos()]))
                    {
                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //插入自己的棋子列表
                            chess.insertToIndexList(pair);
                            //记得隐藏其他的棋子
                            player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                            action = new Action(pair.playerId,pair.chessId,Action.HIDE);
                            queue.enqueue(action);
                        }
                        //更新棋盘
                        chessboard[chess.getPos()] = new Chess(chess);
                    }
                    //可以吃掉其他玩家
                    //记得更新别的玩家的棋子，自己的棋子，以及棋盘
                    else if(chess.eatTest(chessboard[chess.getPos()]))
                    {
                        boolean lefflag = false;
                        boolean changeturn = false;
                        boolean turnlef = false;
                        boolean turnrig = false;
                        boolean emptyspace = false;

                        leftStep = dice -1;
                        if(dice - 1 != 0)
                        {
//                            action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,dice - 1);
//                            queue.enqueue(action);
//5

                            // 0， 0+5=5
                            turnKind = turnTest(currPos,currPos + leftStep);

                            if(turnKind == LEFT)
                            {
                                currStep = getLeftCorner(currPos,(currPos + leftStep)%52) - currPos;//5 - 4 = 1
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);

                                leftStep -= (currStep - 1);//1
                                currPos = (currPos + currStep - 1)%52;//4
                                if(currStep -1 == 0) emptyspace = true;
                                changeturn = true;
                                turnlef = true;
                            }
                            else if(turnKind == RIGHT)
                            {
                                currStep = getRightCorner(currPos,(currPos + leftStep) %52) - currPos;
                                if(currStep < 0) currStep += 52;

                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                queue.enqueue(action);

                                leftStep -= currStep;
                                currPos = (currPos + currStep)%52;
                                changeturn = true;
                                turnrig = true;

                                System.out.println("currpos: " + currPos +" leftstep: " + leftStep);
                            }
                            else if(isLeftCorner((currPos + dice)))
                            {
                                lefflag = true;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep + 1);
                                queue.enqueue(action);
                                currPos = (currPos + currStep)%52;
                                leftStep -= leftStep;
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);
                            }
                            else
                            {

                                currStep = leftStep;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                queue.enqueue(action);

                                if(isRightCorner((currPos + currStep)%52))
                                {
                                    action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                    queue.enqueue(action);
                                }

                                currPos = (currPos + currStep)%52;
                                leftStep -= currStep;
                                changeturn = false;
                            }
                        }

//                        if(turnlef)
//                        {
//                            action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,1);
//                            queue.enqueue(action);
//                            leftStep -= 1;
//                            currPos = (currPos + 1)%52;
//                        }

                        if(!lefflag)
                        {
                            //1 1+4
                            turnKind = turnTest(currPos,(currPos + leftStep)%52);

                            if(changeturn)
                            {
                                if(emptyspace)
                                {
                                    if(turnRightTest(currPos,(currPos+leftStep)%52)){
                                        turnKind = RIGHT;
                                    }
                                }
                            }
                            if(turnlef)
                            {
                                if(turnRightTest(currPos,(currPos + leftStep) %52)){
                                    turnKind = RIGHT;
                                }
                            }

                            if(turnrig)
                            {
                                if(turnLeftTest(currPos,(currPos + leftStep)%52))
                                {
                                    turnKind = LEFT;
                                }
                            }


                            //需要左转
                            if(turnKind == LEFT && !turnlef)
                            {
                                currStep = getLeftCorner(currPos,(currPos + leftStep) % 52) - currPos;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);
                                leftStep -= (currStep - 1);
                                currPos = (currPos + currStep - 1)%52;

                                currStep = leftStep;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                queue.enqueue(action);
                                leftStep -= leftStep;
                                currPos = (currPos + leftStep)%52;
                                turnlef = true;
                            }
                            //需要右转
                            else if(turnKind == RIGHT && !turnrig)
                            {
                                currStep = getRightCorner(currPos,(currPos + leftStep) % 52) - currPos;
                                if(currStep < 0) currStep += 52;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                queue.enqueue(action);
                                leftStep -= currStep;
                                currPos = (currPos + currStep)%52;

                                currStep = leftStep;
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                queue.enqueue(action);
                                leftStep -= leftStep;
                                currPos = (currPos + leftStep)%52;
                                turnrig = true;
                            }
                            else
                            {
                                if(leftStep != 0)
                                {
                                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                    queue.enqueue(action);
                                }
                                if(isRightCorner((currPos + leftStep)%52))
                                {
                                    action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                    queue.enqueue(action);
                                }
                                currPos = (currPos + leftStep)%52;
                                leftStep -= leftStep;
                            }
                        }



                        for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                        {
                            //坠落，回到停机坪
                            player[pair.playerId].chesslist[pair.chessId].setFallen();
                            action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                            queue.enqueue(action);
                        }

                        // 4 5
                        turnKind = turnTest(currPos,(currPos + 1)%52);//LEFT
                        if(turnKind == LEFT)
                        {
                            if(lefflag)
                            {
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                                leftStep -= 1;
                                currPos = (currPos + 1)%52;
                            }
                            else if(turnlef)
                            {
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                                leftStep -= 1;
                                currPos = (currPos + 1)%52;
                            }
                            else
                            {
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                                leftStep -= 1;
                                currPos = (currPos + 1)%52;
                            }
                        }
                        else
                        {
                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                            queue.enqueue(action);
                            if(isRightCorner(currPos + 1))
                            {
                                action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                queue.enqueue(action);
                            }
                            leftStep -= 1;
                            currPos = (currPos + 1)%52;
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
                        boolean flag = false;

                        //先特判一下是不是可以导致飞步的跳步；
                        if(chess.getPos() == chess.getPreFlyingPoint())
                        {
                            flag = true;//可以导致飞步的跳步

                            //先清除以前的棋盘
                            chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                            chessboard[chess.getPos()].clearIndexList();

                            //再移动
                            leftStep = 4;
                            currPos = chess.getPos();
                            chess.setPos((chess.getPos() + 4)%52);

                            if(!chess.eatTest(chessboard[chess.getPos()]))
                            {
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                            }

                            //可以和自己人合体
                            //记得更新自己的棋子和棋盘棋子
                            if(chess.mergeTest(chessboard[chess.getPos()]))
                            {
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //插入自己的棋子列表
                                    chess.insertToIndexList(pair);
                                    //记得隐藏其他的棋子
                                    player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                                    action = new Action(pair.playerId,pair.chessId,Action.HIDE);
                                    queue.enqueue(action);
                                }
                                //更新棋盘
                                chessboard[chess.getPos()] = new Chess(chess);
                            }
                            //可以吃掉其他玩家
                            //记得更新别的玩家的棋子，自己的棋子，以及棋盘
                            else if(chess.eatTest(chessboard[chess.getPos()]))
                            {
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex, Action.TURNLEFT);
                                queue.enqueue(action);
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //坠落，回到停机坪
                                    player[pair.playerId].chesslist[pair.chessId].setFallen();
                                    action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                                    queue.enqueue(action);
                                }
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
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

                            boolean isAttack = false;

                            if(chess.attackTest(chessboard[attackpos]))
                            {
                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,2);
                                queue.enqueue(action);

                                //记得修改玩家的棋子和棋盘
                                for(Pair pair:chessboard[attackpos].getIndexlist())
                                {
                                    //坠落，回到停机坪
                                    player[pair.playerId].chesslist[pair.chessId].setFallen();
                                    action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                                    queue.enqueue(action);
                                }

                                //清空中间的棋盘
                                chessboard[attackpos].setStatus(Chess.STATUS_EMPTY);
                                chessboard[attackpos].clearIndexList();

                                Chess testchess = new Chess(chess);
                                testchess.setPos(testchess.getFlyingPoint());
                                if(!testchess.eatTest(chessboard[testchess.getPos()]))
                                {
                                    //踢完人继续走
                                    action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                                    queue.enqueue(action);
                                }
                                isAttack = true;
                            }
                            else
                            {
                                Chess testchess = new Chess(chess);
                                testchess.setPos(testchess.getFlyingPoint());
                                if(!testchess.eatTest(chessboard[testchess.getPos()]))
                                {
                                    //直接飞过对面
                                    action = new Action(playerid,chessindex,Action.QUICK_MOVE,6);
                                    queue.enqueue(action);
                                }
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
                                    player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                                    action = new Action(pair.playerId,pair.chessId,Action.HIDE);
                                    queue.enqueue(action);
                                }
                            }
                            //吃掉
                            else if(chess.eatTest(chessboard[chess.getPos()]))
                            {
                                if(isAttack)
                                {
                                    //踢完人继续走
                                    action = new Action(playerid,chessindex,Action.QUICK_MOVE,2);
                                    queue.enqueue(action);
                                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                    queue.enqueue(action);
                                }
                                else
                                {
                                    //直接飞过对面
                                    action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                                    queue.enqueue(action);
                                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                    queue.enqueue(action);
                                }
                                for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                {
                                    //坠落，回到停机坪
                                    player[pair.playerId].chesslist[pair.chessId].setFallen();
                                    action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                                    queue.enqueue(action);
                                }
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                            }

                            //右转
                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                            queue.enqueue(action);



                            //更新棋盘
                            chessboard[chess.getPos()] = new Chess(chess);
                            //更新自己的棋子
                            player[playerid].setChess(chessindex,chess);

                            if(!flag)
                            {
                                action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                queue.enqueue(action);
                                action = new Action(playerid,chessindex,Action.TURNLEFT);
                                queue.enqueue(action);


                                //直接删除棋盘，因为还要继续跳
                                chessboard[chess.getPos()].setStatus(Chess.STATUS_EMPTY);
                                chessboard[chess.getPos()].clearIndexList();


                                //在走四步
                                //会有人吗？
                                //会是自己人吗，还是其他人

                                currPos = chess.getPos();
                                leftStep = 4;
                                chess.setPos((chess.getPos() + 4)%52);

                                if(!chess.eatTest(chessboard[chess.getPos()]))
                                {
                                    action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                                    queue.enqueue(action);
                                    action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                    queue.enqueue(action);
                                }

                                //合体
                                if(chess.mergeTest(chessboard[chess.getPos()]))
                                {
                                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                    {
                                        //插入自己的棋子列表
                                        chess.insertToIndexList(pair);
                                        //记得隐藏其他的棋子
                                        player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                                        action = new Action(pair.playerId,pair.chessId,Action.HIDE);
                                        queue.enqueue(action);
                                    }
                                }
                                //吃掉
                                else if(chess.eatTest(chessboard[chess.getPos()]))
                                {
                                    action = new Action(playerid,chessindex,Action.QUICK_MOVE,2);
                                    queue.enqueue(action);
                                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                    queue.enqueue(action);
                                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                    {
                                        //坠落，回到停机坪
                                        player[pair.playerId].chesslist[pair.chessId].setFallen();
                                        action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                                        queue.enqueue(action);
                                    }
                                    action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                    queue.enqueue(action);
                                    action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                    queue.enqueue(action);
                                }

                                //更新棋盘
                                chessboard[chess.getPos()] = new Chess(chess);
                                //更新自己的棋子
                                player[playerid].setChess(chessindex,chess);
                            }
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
                                currPos = chess.getPos();
                                leftStep = 4;
                                chess.setPos((chess.getPos() + 4)%52);

                                if(!chess.eatTest(chessboard[chess.getPos()]))
                                {
//                                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.QUICK_MOVE,4);
//                                    queue.enqueue(action);

                                    turnKind = turnTest(currPos,(currPos + leftStep) %52);
                                    if(turnKind == LEFT)
                                    {
                                        //quickmove很烦啊，还要判断下是不是偶数，不是偶数需要分解
                                        if((getLeftCorner(currPos,currPos + leftStep) - currPos)%2 == 0)
                                        {
                                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,getLeftCorner(currPos,currPos + leftStep) - currPos);
                                            queue.enqueue(action);
                                        }
                                        else
                                        {
                                            if(getLeftCorner(currPos,currPos + leftStep) - currPos -1 != 0)
                                            {
                                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,getLeftCorner(currPos,currPos + leftStep) - currPos -1);
                                                queue.enqueue(action);
                                            }
                                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                            queue.enqueue(action);
                                        }
                                        leftStep -= (getLeftCorner(currPos,currPos + leftStep) - currPos - 1);
                                        action = new Action(playerid,chessindex,Action.TURNLEFT);
                                        queue.enqueue(action);
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                        queue.enqueue(action);
                                        leftStep = 0;
                                        currPos = (currPos + 4)%52;
                                        if(isRightCorner(currPos))
                                        {
                                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                            queue.enqueue(action);
                                        }
                                    }
                                    else if(turnKind == RIGHT)
                                    {
                                        currStep = getRightCorner(currPos,(currPos + leftStep)%52) - currPos;
                                        if(currStep < 0) currStep += 52;

                                        if(currStep%2 == 0)
                                        {
                                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,currStep);
                                            queue.enqueue(action);
                                        }
                                        else
                                        {
                                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,currStep - 1);
                                            if(currStep - 1 != 0) queue.enqueue(action);
                                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                            queue.enqueue(action);
                                        }
                                        leftStep -= currStep;

                                        action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                        queue.enqueue(action);
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                        queue.enqueue(action);
                                        leftStep -= leftStep;
                                        currPos = (currPos + 4)%52;

                                    }
                                    else
                                    {
                                        //移动
                                        action = new Action(playerid,chessindex,Action.QUICK_MOVE,4);
                                        queue.enqueue(action);
                                        if(isRightCorner((currPos + leftStep)%52))
                                        {
                                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                            queue.enqueue(action);
                                        }
                                        leftStep -= leftStep;
                                        currPos = (currPos + 4)%52;
                                    }
                                }

                                //合体
                                if(chess.mergeTest(chessboard[chess.getPos()]))
                                {
                                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                    {
                                        //插入自己的棋子列表
                                        chess.insertToIndexList(pair);
                                        //记得隐藏其他的棋子
                                        player[pair.playerId].chesslist[pair.chessId].setStatus(Chess.STATUS_HIDING);
                                        action = new Action(pair.playerId,pair.chessId,Action.HIDE);
                                        queue.enqueue(action);
                                    }
                                }
                                //吃掉
                                else if(chess.eatTest(chessboard[chess.getPos()]))
                                {
//                                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.QUICK_MOVE,2);
//                                    queue.enqueue(action);
//                                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,1);
//                                    queue.enqueue(action);

                                    leftStep = 3;
                                    turnKind = turnTest(currPos,(currPos + leftStep)%52);

                                    //需要左转
                                    if(turnKind == LEFT)
                                    {
                                        currStep = getLeftCorner(currPos,(currPos + leftStep)%52) - currPos;
                                        //fucking code，又要分解一波，心态是如何炸的
                                        if(currStep % 2 == 0)
                                        {
                                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,currStep);
                                            queue.enqueue(action);
                                        }
                                        else
                                        {
                                            if(currStep - 1 != 0)
                                            {
                                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,currStep - 1);
                                                queue.enqueue(action);
                                            }
                                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                            queue.enqueue(action);
                                        }

                                        action = new Action(playerid,chessindex,Action.TURNLEFT);
                                        queue.enqueue(action);

                                        leftStep -= (currStep - 1 );
                                        currPos = (currPos + currStep - 1)%52;

                                        currStep = leftStep;
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,leftStep);
                                        queue.enqueue(action);
                                        leftStep -= leftStep;
                                        currPos = (currPos + currStep)%52;
                                    }
                                    else if(turnKind == RIGHT)
                                    {
                                        currStep = getRightCorner(currPos,(currPos + leftStep)%52) - currPos;
                                        if(currStep < 0) currStep += 52;
                                        if(currStep %2 == 0)
                                        {
                                            action = new Action(playerid,chessindex,Action.QUICK_MOVE,currStep);
                                            queue.enqueue(action);
                                        }
                                        else
                                        {
                                            if(currStep - 1 != 0)
                                            {
                                                action = new Action(playerid,chessindex,Action.QUICK_MOVE,currStep - 1);
                                                queue.enqueue(action);
                                            }
                                            action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                            queue.enqueue(action);
                                        }

                                        currPos = (currPos + currStep) %52;
                                        leftStep -= currStep;

                                        action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                        queue.enqueue(action);
                                        currStep = leftStep;
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                        queue.enqueue(action);
                                        leftStep -= leftStep;
                                        currPos = (currPos + currStep)%52;
                                    }
                                    else
                                    {
                                        action = new Action(playerid,chessindex,Action.QUICK_MOVE,2);
                                        queue.enqueue(action);
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                        queue.enqueue(action);
                                        currPos = (currPos + 3)%52;
                                        leftStep -= 3;
                                        if(isRightCorner(currPos))
                                        {
                                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                            queue.enqueue(action);
                                        }
                                    }

                                    for(Pair pair:chessboard[chess.getPos()].getIndexlist())
                                    {
                                        //坠落，回到停机坪
                                        player[pair.playerId].chesslist[pair.chessId].setFallen();
                                        action = new Action(pair.playerId,pair.chessId,Action.FALLEN);
                                        queue.enqueue(action);
                                    }
//                                    action = new FlyChess.Action(playerid,chessindex,FlyChess.Action.NORMAL_MOVE,1);
//                                    queue.enqueue(action);

                                    leftStep = 1;
                                    turnKind = turnTest(currPos,(currPos + leftStep)%52) - currPos;
                                    if(turnKind == LEFT)
                                    {
                                        currStep = getLeftCorner(currPos,(currPos + leftStep)%52) - currPos;
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                        if(currStep != 0) queue.enqueue(action);

                                        leftStep -= (currStep -1);
                                        currPos = (currPos + currStep -1)%52;
                                        action = new Action(playerid,chessindex,Action.TURNLEFT);
                                        queue.enqueue(action);

                                        currStep = leftStep;
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                        if(currStep != 0) queue.enqueue(action);

                                        currPos = (currPos + currStep)%52;
                                        leftStep -= currStep;
                                    }
                                    else if(turnKind == RIGHT)
                                    {
                                        currStep = getRightCorner(currPos,(currPos + leftStep)%52) - currPos;
                                        if(currStep < 0) currStep += 52;
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                        if(currStep != 0) queue.enqueue(action);

                                        currPos = (currPos + currStep)%52;
                                        leftStep -= currStep;

                                        currStep = leftStep;
                                        action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                        queue.enqueue(action);

                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,currStep);
                                        if(currStep != 0) queue.enqueue(action);

                                        currPos = (currPos + currStep)%52;
                                        leftStep -= currStep;
                                    }
                                    else
                                    {
                                        action = new Action(playerid,chessindex,Action.NORMAL_MOVE,1);
                                        queue.enqueue(action);
                                        currPos = (currPos + 1)%52;
                                        if(isRightCorner(currPos))
                                        {
                                            action = new Action(playerid,chessindex,Action.TURNRIGHT);
                                            queue.enqueue(action);
                                        }
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
                    if(dice != 6) nextTurn();

                    return queue;
                }
            }
            else
            {
                if(dice != 6) nextTurn();
                return queue;
            }
        }
    }

    //    public void run() {
//
//        FlyChess.Queue<FlyChess.Action> actions;
//        String str ="";
//
//
//        while(!isGameOver())
//        {
//            //如果是联机模型，则向所有玩家发送现在轮到谁了
//            //send_Current_Turn_To_AllPlayer
//
//            //不断扫描是不是有人扔了骰子
//            while (waitDice()) try {
//                UISimulator.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            //不断扫描是不是有人选择了棋子
//            while (waitChoice()) try {
//                UISimulator.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//            //后面的服务器模型时会用到
//    }
    public static void main(String[] args) throws InterruptedException {

        //三个玩家必须要初始化为红，黄,蓝
        GameManager gameManager_three = new GameManager(new AutoAI(Chess.RED),new AutoAI(Chess.YELLOW),new AutoAI(Chess.BLUE));

        //两个玩家必须要初始化为红，蓝
        GameManager gameManager_two = new GameManager(new AutoAI(Chess.RED),new AutoAI(Chess.BLUE));

        String str;

        if(gameManager_three.getTurn() == Chess.RED) str = "红色玩家";
        else if(gameManager_three.getTurn() == Chess.YELLOW) str = "黄色玩家";
        else if(gameManager_three.getTurn() == Chess.BLUE) str = "蓝色玩家";
        else str = "绿色玩家";

        System.out.println("现在轮到" + str);
        System.out.println("throwing a dice...");

        int dice1 =2;
        int choice1 = 0;

        Chess chess = new Chess(new Pair(Chess.RED,0),Chess.RED);
        chess.setStatus(Chess.STATUS_FLYING);
        chess.setPos(45);
        gameManager_three.chessboard[45] = new Chess(chess);
        gameManager_three.player[Chess.RED].chesslist[0] = new Chess(chess);

        chess = new Chess(new Pair(Chess.YELLOW,0),Chess.YELLOW);
        chess.setStatus(Chess.STATUS_FLYING);
        chess.setPos(47);
        gameManager_three.chessboard[47] = new Chess(chess);
        gameManager_three.player[Chess.YELLOW].chesslist[0] = new Chess(chess);

//        chess = new FlyChess.Chess(new FlyChess.Pair(FlyChess.Chess.BLUE,1),FlyChess.Chess.BLUE);
//        chess.setStatus(FlyChess.Chess.STATUS_FLYING);
//        chess.setPos(18);
//        gameManager_three.chessboard[18] = new FlyChess.Chess(chess);
//        gameManager_three.player[FlyChess.Chess.BLUE].chesslist[1] = new FlyChess.Chess(chess);
//
//        chess = new FlyChess.Chess(new FlyChess.Pair(FlyChess.Chess.BLUE,0),FlyChess.Chess.BLUE);
//        chess.setStatus(FlyChess.Chess.STATUS_FLYING);
//        chess.setPos(64);
//        gameManager_three.chessboard[64] = new FlyChess.Chess(chess);
//        gameManager_three.player[FlyChess.Chess.BLUE].chesslist[0] = new FlyChess.Chess(chess);




//        chess = new FlyChess.Chess(new FlyChess.Pair(FlyChess.Chess.YELLOW,2),FlyChess.Chess.YELLOW);
//        chess.setStatus(FlyChess.Chess.STATUS_FLYING);
//        chess.setPos(34);
//        gameManager_three.chessboard[34] = new FlyChess.Chess(chess);
//        gameManager_three.player[FlyChess.Chess.YELLOW].chesslist[2] = new FlyChess.Chess(chess);

        System.out.println("dice: " + dice1);

        gameManager_three.setDice(dice1);

        System.out.println("selecting a choice...");

        gameManager_three.setChoice(choice1);

        System.out.println("choice: " + choice1);

        Queue<Action> actions1 = gameManager_three.actionlist();
        for(Action action:actions1)
        {
            System.out.println(action);
        }

//

//        while(!gameManager_three.isGameOver())
//        {
//            if(gameManager_three.getTurn() == FlyChess.Chess.RED) str = "红色玩家";
//            else if(gameManager_three.getTurn() == FlyChess.Chess.YELLOW) str = "黄色玩家";
//            else if(gameManager_three.getTurn() == FlyChess.Chess.BLUE) str = "蓝色玩家";
//            else str = "绿色玩家";
//
//            System.out.println("现在轮到" + str);
//
//            System.out.println("throwing a dice...");
//            Thread.sleep(2);
//
//            int dice = ((int)(Math.random()*1000000))%6 + 1;
//
//            System.out.println("dice: " + dice);
//
//            gameManager_three.setDice(dice);
//
//            System.out.println("selecting a choice...");
//
//            Thread.sleep(1);
//
//            int choice = gameManager_three.getAIChoice();
//
//            gameManager_three.setChoice(choice);
//
//            System.out.println("choice: " + choice);
//
//
//            FlyChess.Queue<FlyChess.Action> actions = gameManager_three.actionlist();
//
//            for(FlyChess.Action action:actions)
//            {
//                System.out.println(action);
//            }
//            Thread.sleep(1);
//        }
//
//        System.out.println("/****************************************************************/");
//
//
//
//        while(!gameManager_two.isGameOver())
//        {
//            if(gameManager_two.getTurn() == FlyChess.Chess.RED) str = "红色玩家";
//            else if(gameManager_two.getTurn() == FlyChess.Chess.YELLOW) str = "黄色玩家";
//            else if(gameManager_two.getTurn() == FlyChess.Chess.BLUE) str = "蓝色玩家";
//            else str = "绿色玩家";
//
//            System.out.println("现在轮到" + str);
//
//            System.out.println("throwing a dice...");
//            Thread.sleep(2);
//
//            int dice = ((int)(Math.random()*1000000))%6 + 1;
//
//            System.out.println("dice: " + dice);
//
//            gameManager_two.setDice(dice);
//
//            System.out.println("selecting a choice...");
//
//            Thread.sleep(1);
//
//            int choice = gameManager_two.getAIChoice();
//
//            gameManager_two.setChoice(choice);
//
//            System.out.println("choice: " + choice);
//
//
//            FlyChess.Queue<FlyChess.Action> actions = gameManager_two.actionlist();
//
//            for(FlyChess.Action action:actions)
//            {
//                System.out.println(action);
//            }
//            Thread.sleep(1);
//        }
    }
}

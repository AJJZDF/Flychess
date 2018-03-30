/**
 * Created by gitfan on 3/26/18.
 */
public class AutoAI extends BasicAI {

    public AutoAI(int color){
        super(BasicAI.AI,color);
    }
    private static int score(Chess chess,int dice,Chess chessboard[])
    {
        if (chess.sprint())
        {
            if(chess.testGoal(dice)) return 10000;
            else if(54 + chess.getColor()*5 == chess.getPos() && dice != 6) return 500;
            else if(54 + chess.getColor()*5 == chess.getPos() && dice == 6) return 0;
            else return 50;
        }
        else
        {
            if(chess.entry())
            {
                if (dice == 6) return 10000;
                else if(dice == 3) return 4000;
                else return 5000;
            }
            else if(chess.presprint(dice)) return 5000;
            else if(chess.eatTest(chessboard[(chess.getPos() + dice)%52])){
                return (chessboard[(chess.getPos() + dice)%52].getIndexlist().size()+chess.getIndexlist().size())*1500;
            }
            else if(chess.mergeTest(chessboard[(chess.getPos() + dice)%52])){
                return (chessboard[(chess.getPos() + dice)%52].getIndexlist().size()+chess.getIndexlist().size())*1000;
            }
            else
            {
                Chess tmp = new Chess(chess);
                tmp.setPos((chess.getPos() + dice)%52);
                if(tmp.isSuperLucky()) return 3000;
                else if(tmp.isLucky()) return 2000;
                else
                {
                    if(chess.getColor() == Chess.RED) return 2000-(51-chess.getPos()-dice)*40;
                    else if(chess.getColor() == Chess.YELLOW)
                    {
                        if(chess.getPos() + dice >= 14 && chess.getPos() + dice <=51) return (64-chess.getPos()-dice)*40;
                        else return (11 - (chess.getPos()+dice)%52)*40;
                    }
                    else if(chess.getColor() == Chess.BLUE)
                    {
                        if(chess.getPos() + dice >= 27 && chess.getPos() + dice <=51) return (77-chess.getPos()-dice)*40;
                        else return (24 - (chess.getPos()+dice)%52)*40;
                    }
                    else
                    {
                        if(chess.getPos() + dice >= 40 && chess.getPos() + dice <=51) return (90-chess.getPos()-dice)*40;
                        else return (37 - (chess.getPos()+dice)%52)*40;
                    }
                }
            }
        }
    }
    private Queue<Integer> getAvailableMove()
    {
        Queue<Integer> queue = new Queue<>();
        for(int i = 0;i < 4;i++)
        {
            if(chesslist[i].canMove()) queue.enqueue(i);
        }
        return queue;
    }
    public int choice(int dice,Chess chessboard[])
    {
        Queue<Integer> queue = getAvailableMove();
        if(queue.isEmpty())
        {
            if(dice <= 4) return  -1;
            for (int i = 0; i < 4 ;i++){
                if(chesslist[i].getStatus() == Chess.STATUS_AIRPORT) return i;
            }
        }
        else
        {
            int choice = queue.dequeue();
            int maxval = score(chesslist[choice],dice,chessboard);
            while(!queue.isEmpty()){
                int curr = queue.dequeue();
                int currval = score(chesslist[curr],dice,chessboard);
                if(maxval < currval){
                    choice = curr;
                    maxval = currval;
                }
            }
            if(dice == 5 || dice == 6)
            {
                if(maxval < 1000)
                {
                    for (int i = 0; i < 4 ;i++){
                        if(chesslist[i].getStatus() == Chess.STATUS_AIRPORT) return i;
                    }
                }
            }
            return choice;
        }
        return -1;
    }
}

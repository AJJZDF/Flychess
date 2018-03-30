/**
 * Created by gitfan on 3/29/18.
 */
public class PlayerAI extends BasicAI {

    public PlayerAI(int color)
    {
        super(BasicAI.PEOPLE,color);
    }

    private Queue<Integer> getAvailableMove()
    {
        Queue<Integer> queue = new Queue<>();
        for(int i = 0;i < 4;i++)
        {
            if(chesslist[i].canMove())
            {
                queue.enqueue(i);
            }
        }
        return queue;
    }
    public Queue<Integer> choice(int dice,Chess chessboard[])
    {
        Queue<Integer> queue = getAvailableMove();
        Queue<Integer> choose = new Queue<>();
        if(queue.isEmpty())
        {
            if(dice <= 4) return  null;
            for (int i = 0; i < 4 ;i++)
            {
                if(chesslist[i].getStatus() == Chess.STATUS_AIRPORT)
                {
                    choose.enqueue(i);
                }
            }
            return choose;
        }
        else
        {
            while(!queue.isEmpty())
            {
                int val = queue.dequeue();
                choose.enqueue(val);
            }
            if(dice >= 5)
            {
                for (int i = 0; i < 4 ;i++)
                {
                    if(chesslist[i].getStatus() == Chess.STATUS_AIRPORT)
                    {
                        choose.enqueue(i);
                    }
                }
            }

            return choose;
        }
    }

}

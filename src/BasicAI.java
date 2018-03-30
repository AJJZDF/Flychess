/**
 * Created by gitfan on 3/26/18.
 */
public class BasicAI {

    public int kind,color;
    public static int PEOPLE = 1,AI = 2;
    public Chess chesslist[];
    //color 和 turn 一一对应
    public BasicAI(int kind,int color){
        setKind(kind);
        setTurn(color);
        chesslist = new Chess[4];
        for(int i = 0;i < 4 ; i++){
            chesslist[i] = new Chess(new Pair(color,i),color);
        }
    }
    public void setTurn(int turn)
    {
        if(turn < 0 || turn >= 4){
            System.out.print("unexpected value in BasicAi,setTurn(int turn)");
            System.exit(0);
        }
        this.color = turn;
    }
    public void setKind(int kind){
        if(kind != PEOPLE && kind != AI){
            System.out.print("unexpected kind in BasicAi,setKind(int kind)");
            System.exit(0);
        }
        this.kind = kind;
    }
    public int getKind()
    {
        return this.kind;
    }
    private static boolean illegalIndex(int idx)
    {
        if(idx < 0 || idx >= 4)
        {
            return true;
        }
        return false;
    }
    //设置棋子
    public void setChess(int index,Chess chess)
    {
        if(illegalIndex(index)){
            System.out.print("index out of range in BasicAI: setChess(int index,Chess chess)");
            System.exit(0);
        }
        if(chess == null){
            System.out.print("chess in null in BasicAI:setChess(int index,Chess chess)");
            System.exit(0);
        }
        this.chesslist[index] = chess;
    }
    public Chess getChess(int index)
    {
        if(illegalIndex(index)){
            System.out.print("index out of range in BasicAI: getChess(int index,Chess chess)");
            System.exit(0);
        }
        return this.chesslist[index];
    }
    public boolean isMyturn(int turn)
    {
        return (this.color == turn);
    }

    public boolean isFinish()
    {
        for(int i = 0; i < 4; i++){
            if(chesslist[i].getStatus() != Chess.STATUS_FINISH) return false;
        }
        return true;
    }
}

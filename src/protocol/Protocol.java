//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package protocol;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class Protocol implements Serializable {
    public static final int MSG_TYPE_LOGI_MSG = 2;
    public static final int MSG_TYPE_LOGI_UNITY_UPDATE_UI = 3;
    private int msg_type;
    private MsgLogic _msgLogic;
    private MsgUnity _msgUnity;

    public Protocol(int type) {
        assert type >= 0 && type < 4;

        this.msg_type = type;
    }

    public int getMsg_type() {
        return this.msg_type;
    }


    public MsgLogic get_msgLogic() {
        return this._msgLogic;
    }

    public MsgUnity get_msgUnity() {
        return this._msgUnity;
    }

    public void set_msgLogic(int user_id, MsgLogic ml) {
        this._msgLogic = ml;
        _msgLogic.setUserID(user_id);
    }

    public void set_msgLogic(int user_id, MsgLogic ml, int data) {
        this._msgLogic = ml;
        _msgLogic.setUserID(user_id);
        if (this._msgLogic.getType() == 7) {
            this._msgLogic.setXP(data);
        }

    }

    public void set_msgLogic(int user_id, MsgLogic ml, boolean assess) {
        this._msgLogic = ml;
        _msgLogic.setUserID(user_id);
        if (this._msgLogic.getType() == 10 || this._msgLogic.getType() == 12) {
            this._msgLogic.setAssess(assess);
        }

    }

    public void set_msgLogic(int user_id, MsgLogic ml, String msgPass) {
        this._msgLogic = ml;
        _msgLogic.setUserID(user_id);
        if (this._msgLogic.getType() == 4 || this._msgLogic.getType() == 6 || this._msgLogic.getType() == 11) {
            this._msgLogic.setRoomName(msgPass);
        }
    }

    public void set_msgLogic(int user_id, MsgLogic ml, String str1, String str2) {
        this._msgLogic = ml;
        _msgLogic.setUserID(user_id);
        if (this._msgLogic.getType() == 2) {
            this._msgLogic.setRoomName(str1);
            this._msgLogic.setUsr(str2);
        } else if (this._msgLogic.getType() == 8) {
            this._msgLogic.setRoomName(str1);
            this._msgLogic.setChairState(str2);
        } else if (this._msgLogic.getType() == 9) {
            this._msgLogic.setRoomName(str1);
            this._msgLogic.setUsr(str2);
        }

    }

    public void set_msgLogic(int user_id, MsgLogic ml, Vector<String> vector) {
        this._msgLogic = ml;
        _msgLogic.setUserID(user_id);
        if (this._msgLogic.getType() == 3) {
            this._msgLogic.setOnlineRooms(vector);
        } else if (this._msgLogic.getType() == 5) {
            this._msgLogic.setRoomStatus(vector);
        }

    }

    public void set_msgUnity(int user_id, MsgUnity mu) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
    }

    public void set_msgUnity(int user_id, MsgUnity mu, String msgPass) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if (this._msgUnity.getType() == 7 || this._msgUnity.getType() == 8 || this._msgUnity.getType() == MsgUnity.EXIT_GAME) {
            this._msgUnity.setRoomname(msgPass);
        }
        else if(this._msgUnity.getType() == MsgUnity.SHOW_RANKING_LIST)
        {
            this._msgUnity.set_ranking_list(msgPass);
        }
    }
    public void set_msgUnity(int user_id, MsgUnity mu, String filename, String history) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if(this._msgUnity.getType() == MsgUnity.REPLAY_GAME)
        {
            _msgUnity.set_filename(filename);
            _msgUnity.set_history(history);
        }
    }

    public void set_msgUnity(int user_id, MsgUnity mu, String msgPass, float force) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if (this._msgUnity.getType() == 4) {
            this._msgUnity.setRoomname(msgPass);
            this._msgUnity.setForce(force);
        }
    }

    public void set_msgUnity(int user_id, MsgUnity mu, float force) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if (this._msgUnity.getType() == 9) {
            this._msgUnity.setForce(force);
        }

    }

    public void set_msgUnity(int user_id, MsgUnity mu, int num) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if (this._msgUnity.getType() == 3 || this._msgUnity.getType() == 10 || this._msgUnity.getType() == MsgUnity.AUTO_THROW_DICE) {
            this._msgUnity.setDice(num);
        }
    }

    public void set_msgUnity(int user_id, MsgUnity mu, String msgPass, int num) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if (this._msgUnity.getType() == 5) {
            this._msgUnity.setRoomname(msgPass);
            this._msgUnity.setDice(num);
        }

    }

    public void set_msgUnity(int user_id, MsgUnity mu, String msgPass, int turn, int choice) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if (this._msgUnity.getType() == 6) {
            this._msgUnity.setRoomname(msgPass);
            this._msgUnity.setTurn(turn);
            this._msgUnity.setChoice(choice);
        }

    }

    public void set_msgUnity(int user_id, MsgUnity mu, Vector<String> msgVectorPass) {
        this._msgUnity = mu;
        this._msgUnity.setUserID(user_id);
        if (this._msgUnity.getType() == 1) {
            this._msgUnity.setActionList(msgVectorPass);
        } else if (this._msgUnity.getType() == 2) {
            this._msgUnity.setChangeNameInUi(msgVectorPass);
        }

    }

    public String toString() {
        switch(this.msg_type) {
        case 2:
            return this._msgLogic.toString();
        case 3:
            return this._msgUnity.toString();
        default:
            return "[ MsgNull ]";
        }
    }

    public static void socketSerilize(Socket socket, Protocol msg) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(msg);
            objectOutputStream.flush();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public static Protocol socketUnSerilize(Socket socket) {
        Protocol rb = null;

        try {
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            rb = (Protocol)objectInputStream.readObject();
            return rb;
        } catch (ClassNotFoundException var4) {
            System.out.println("err 收到客户端的请求不能发序列化到Protocal");
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return rb;
    }

    public static void main(String[] args) {
    }
}

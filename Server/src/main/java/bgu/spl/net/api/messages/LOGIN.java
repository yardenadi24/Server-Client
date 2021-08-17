package bgu.spl.net.api.messages;

public class LOGIN implements Message {
    short op;
    String username;
    String pass;

    public LOGIN(short _op,String _user,String _pass){
        op=_op;
        username=_user;
        pass=_pass;
    }
    @Override
    public short GetOPcode() {
        return op;
    }
    public String GetUsername() {
        return username;
    }
    public String GetPass() {
        return pass;
    }
}

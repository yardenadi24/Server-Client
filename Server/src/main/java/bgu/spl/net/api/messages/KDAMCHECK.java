package bgu.spl.net.api.messages;

public class KDAMCHECK implements Message{
    short op;
    short num;

    public KDAMCHECK(short _op,short _num){
        op=_op;
        num=_num;
    }

    public short getNumOf(){
        return num;
    }

    @Override
    public short GetOPcode() {
        return op;
    }
}

package bgu.spl.net.api.messages;

public class COURSESTAT implements Message{
    short op;
    short num;

    public COURSESTAT(short _op,short _num){
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

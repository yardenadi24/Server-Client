package bgu.spl.net.api.messages;

public class ERR implements Message {
    short op;
    short messageOp;

    public ERR(short _op,short _messageOp){
        op=_op;
        messageOp=_messageOp;
    }
    @Override
    public short GetOPcode() {
        return op;
    }
    public short GetMessageOp() {
        return messageOp;
    }
}

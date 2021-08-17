package bgu.spl.net.api.messages;

public class ACK implements Message {
    short op;
    short messageOp;
    String replay;

    public ACK(short _op,short _messageOp,String _replay){
        op=_op;
        messageOp=_messageOp;
        replay=_replay;
    }

    @Override
    public short GetOPcode() {
        return op;
    }
    public short GetMessageOp() {
        return messageOp;
    }
    public String GetReplay() {
        return replay;
    }
}

package bgu.spl.net.api.messages;

public class MYCOURSES implements Message{
    private short opcode;

    public MYCOURSES(short _opcode){
        opcode=_opcode;
    }
    @Override
    public short GetOPcode() {
        return opcode;
    }
}

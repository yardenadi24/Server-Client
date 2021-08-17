package bgu.spl.net.api.messages;

public class LOGOUT implements Message{
    private short opcode;

    public LOGOUT(short _opcode){
        opcode=_opcode;
    }

    @Override
    public short GetOPcode(){
        return opcode;
    }
}

package bgu.spl.net.api.messages;

public class STUDENTSTAT implements Message {
    private short opcode;
    private String student;

    public STUDENTSTAT(short _opcode,String _student){
        opcode=_opcode;
        student=_student;
    }
    @Override
    public short GetOPcode() {
        return opcode;
    }
    public String GetStudName(){return student;}
}


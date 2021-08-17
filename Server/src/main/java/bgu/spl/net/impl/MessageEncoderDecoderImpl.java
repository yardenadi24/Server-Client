package bgu.spl.net.impl;
import java.nio.charset.StandardCharsets;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.messages.*;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder<T> {
    //--fields--//
    private short operation;
    private ArrayList<String> data;
    byte[] bytes;
    int len;
    int pos;

    public MessageEncoderDecoderImpl(){
        bytes = new byte[1<<10];
        len=0;
        pos=0;
        operation=-1;
        data = new ArrayList<String>();
    }

    @Override
    public T decodeNextByte(byte nextByte) {
        //checks if we already know which operation if no so we didnt got 2 bytes yet
        if(operation<0){
            //if right now we have 2 bytes get operation
            if(len==1){
                pushByte(nextByte);
                operation = byteToShort(bytes);
                //operations 4 and 11 dose not require more data so check is that is the case act
                if(operation == 4 || operation == 11){
                    switch(operation){ case 4: return LOGOUT(); case 11: return MYCOURSES();}
                }
                //else, just push the byte
                return null;
            }
            //if len is not 2 we cant take operation , push byte
            pushByte(nextByte);
            return null;
        }
        //in those cases we already know the operation
        if(operation==1||operation==2||operation==3){
            if(nextByte=='\0'){
                popString();
                if(data.size()==2){
                    //we have now the username and password
                    switch (operation){
                        case 1:return ADMINREG();
                        case 2:return STUDENTREG();
                        case 3:return LOGIN();
                    }

                }
            }
            pushByte(nextByte);
            return null;
        }
        if(operation==5||operation==6||operation==7||operation==9||operation==10){
            if (len>=3){
                pushByte(nextByte);
                switch (operation){
                    case 5: return COURSEREG();
                    case 6: return KDAMCHECK();
                    case 7: return COURSESTAT();
                    case 9: return ISREGISTER();
                    case 10:return UNREGISTER();
                }
            }
            pushByte(nextByte);
            return null;
        }

        if(operation==8){
            if(nextByte=='\0'){
                popString();
                return STUDENTSTAT();
            }
            pushByte(nextByte);
            return null;
        }
        return null;
    }

    private void popString(){
        String result = new String(bytes,pos,len,StandardCharsets.UTF_8);
        pos=len;
        data.add(result);
    }

    private void pushByte(byte nextByte){
        if(len >= bytes.length){
            bytes = Arrays.copyOf(bytes,bytes.length*2);
        }
        bytes[len]=nextByte;
        len++;
    }

    private short byteToShort(byte[] byteArr){
        short result = (short)((byteArr[pos]& 0xff)<<8);
        result += (short)(byteArr[pos+1] & 0xff);
        pos=pos+2;
        return result;
    }



    /////-------decode-------//////
    //4//--------------------------
    private T LOGOUT(){
    short res = operation;
    clean();
    return (T)new LOGOUT(res);
    }
    //11//
    private T MYCOURSES(){
        short res = operation;
        clean();
    return (T)new MYCOURSES(res);
    }
    //-8//-------------------------
    private T STUDENTSTAT(){
        short res = operation;
        String studName = data.get(0);
        clean();
        return (T)new STUDENTSTAT(res,studName);
    }
    //-1-//------------------------
    private T ADMINREG(){
        short res = operation;
        String user = data.get(0);
        String pass = data.get(1);
        clean();
        return (T)new ADMINREG(res,user,pass);
    }
    //-2-//
    private T STUDENTREG(){
        short res = operation;
        String user = data.get(0);
        String pass = data.get(1);
        clean();
        return (T)new STUDENTREG(res,user,pass);
    }
    //-3-//
    private T LOGIN(){
        short res = operation;
        String user = data.get(0);
        String pass = data.get(1);
        clean();
        return (T)new LOGIN(res,user,pass);
    }
    //--5--//-----------------------
    private T COURSEREG(){
        short res = operation;
        short num = byteToShort(bytes);
        clean();
        return (T)new COURSEREG(res,num);
    }
    //--6--//
    private T KDAMCHECK(){
        short res = operation;
        short num  = byteToShort(bytes);
        clean();
        return (T)new KDAMCHECK(res,num);
    }
    //--7--//
    private T COURSESTAT(){
        short res = operation;
        short num = byteToShort(bytes);
        clean();
        return (T)new COURSESTAT(res,num);
    }
    //--9--//
    private T ISREGISTER(){
        short res = operation;
        short num = byteToShort(bytes);
        clean();
        return (T)new ISREGISTERED(res,num);
    }
    //--10--//
    private T UNREGISTER(){
        short res = operation;
        short num = byteToShort(bytes);
        clean();
        return (T)new UNREGISTER(res,num);
    }
    //---------------------------------

    private void clean(){
        bytes = new byte[1<<10];
        len=0;
        pos=0;
        operation=-1;
        data = new ArrayList<String>();
    }


    @Override
    public byte[] encode(T message) {
        byte[] res ;
        int len1 = 0;
        int pos1 =0;
        Message temp = (Message) message;
        short op = temp.GetOPcode();
        if(op==12){
            ACK tempAc = (ACK)temp;
            shortToBytes(tempAc.GetOPcode(),0,bytes);
            shortToBytes(tempAc.GetMessageOp(),2,bytes);
            len1=len1+4;
            pos1 = pos1+4;
            String answer = tempAc.GetReplay();
            byte[] append;
            if(!answer.equals("")){
                append = (answer+"\0").getBytes(StandardCharsets.UTF_8);
                for(byte i:append){

                    if(len1 >= bytes.length){
                        bytes = Arrays.copyOf(bytes,bytes.length*2);
                    }
                    bytes[pos1]=i;
                    pos1++;
                    len1++;
                }
            }

            res=Arrays.copyOf(bytes,len1);
            clean();
            return res;
        }
        if(op==13){
            ERR tempEr = (ERR) temp;
            shortToBytes(tempEr.GetOPcode(),0,bytes);
            shortToBytes(tempEr.GetMessageOp(),2,bytes);
            len1=len1+4;
            pos1 = pos1+4;
            res=Arrays.copyOf(bytes,len1);
            clean();
            return res;
        }
        return new byte[0];
    }

    private void shortToBytes(short num,int start,byte[] bytes1){
        bytes1[start] = (byte)((num>>8)&0xFF);
        bytes1[start+1]= (byte)(num & 0xFF);
    }
    private void shortToBytes(short num,int start){
        bytes[start] = (byte)((num>>8)&0xFF);
        bytes[start+1]= (byte)(num & 0xFF);
    }


}
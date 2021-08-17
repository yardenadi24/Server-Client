package bgu.spl.net.api;

import bgu.spl.net.Course;
import bgu.spl.net.Database;
import bgu.spl.net.User;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.messages.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class MessagingProtocolImp implements MessagingProtocol<Message> {
    boolean Terminate=false;
    Database data=Database.getInstance();
    String username = null;
    char type = '0';
    short errorOp = 13;
    short ackOp = 12;
    boolean isLogged = false;

    @Override
    public Message process(Message msg) {
        String replaymassage;
        Message replay=null;
        short opcode= msg.GetOPcode();

        //--Adminreg--//
        if (opcode==1){
            ADMINREG temp = (ADMINREG) msg;
            if ( username!=null||userIsExist(temp.GetUsername())){
                replay = new ERR(errorOp,opcode);
            }
            else {
                User newUser=new User('A',temp.GetUsername(),temp.GetPass());
                data.getUserMap().put(temp.GetUsername(),newUser);
                replaymassage="";
                replay=new ACK(ackOp,opcode,replaymassage);
            }
        }

        if(opcode==2){///- Studentreg
            STUDENTREG temp = (STUDENTREG) msg;
            if (username!=null||userIsExist(temp.GetUsername())){
                replay = new ERR(errorOp,opcode);
            }
            else {
                User newUser=new User('S',temp.GetUsername(),temp.GetPass());
                data.getUserMap().put(temp.GetUsername(),newUser);
                replaymassage="";
                replay=new ACK(ackOp,opcode,replaymassage);
            }
        }

        if(opcode==3){//-Login
            LOGIN temp = (LOGIN) msg;
            if (username!=null){
                replay = new ERR(errorOp,opcode);
            }
            else{
                if (!userIsExist(temp.GetUsername())||!data.getUserMap().get(temp.GetUsername()).getPassword().equals(temp.GetPass())||isLog(temp.GetUsername())) {
                    replay = new ERR(errorOp, opcode);
                }
                    else{
                        username = temp.GetUsername();
                        type=data.getUserMap().get(username).getType();
                        data.getUserMap().get(username).setLog(true);
                        isLogged = true;
                        replaymassage = "";
                        replay = new ACK(ackOp,temp.GetOPcode(),replaymassage);
                    }
                }
            }



        if(opcode==4){///- Logout
            LOGOUT temp = (LOGOUT) msg;
            if (!isLogged||!isLog(username)) {
                replay = new ERR(errorOp,opcode);
            }
            else {
                data.getUserMap().get(username).setLog(false);
                isLogged=false;
                replaymassage = "";
                replay = new ACK(ackOp, temp.GetOPcode(), replaymassage);
            }
        }

        if(opcode==5){/// -Courseereg
            COURSEREG temp = (COURSEREG) msg;
            int number = temp.getNumOf();
            if (!isLogged||!isLog(username)||getType()=='A') {
                replay = new ERR(errorOp,opcode);
            }
            else if(!courseIsExist(temp.getNumOf())){
                replay = new ERR(errorOp,opcode);
            }
            else if(!isRegisterToKdamCourse(number,username)){
                replay = new ERR(errorOp,opcode);
            }else if(data.getUserMap().get(username).getNumCourses().contains(number)){
                replay = new ERR(errorOp,opcode);
            }else if(data.getCourseMap().get(number).getAvialable()<=0){
                replay = new ERR(errorOp,opcode);
            }
            else{
                addCourse(username,temp.getNumOf());
                replaymassage = "";
                replay = new ACK(ackOp, temp.GetOPcode(), replaymassage);
            }
        }

        if(opcode==6){// - Kdamcheck
            KDAMCHECK temp = (KDAMCHECK) msg;
            if(!courseIsExist(temp.getNumOf())||!isLogged){
                replay = new ERR(errorOp,opcode);
            }
            else{
                int coursenum= temp.getNumOf();
                List<Integer> kdam =data.getCourseMap().get(coursenum).GetKdam();
                Collections.sort(kdam, Comparator.comparingInt(o -> data.getCourseMap().get(o).getCourseid()));
                String s="[";
                for(Integer i: kdam){
                    s=s+i.toString().trim()+",";
                }
                if (s.length() > 1) {
                    s=s.substring(0,s.length()-1);
                }
                s=s+"]";
                replaymassage = s;
                replay = new ACK(ackOp, temp.GetOPcode(), replaymassage);
            }
        }

        if(opcode==7){//-Coursestat
            COURSESTAT temp = (COURSESTAT) msg;
            if(!courseIsExist(temp.getNumOf()) ||!isLog(username)|| (!isLogged) || getType()=='S'){
                replay= new ERR(errorOp,opcode);
            }
            else {
                Integer coursenum=(int)temp.getNumOf();
                Course course=data.getCourseMap().get(coursenum);
                String coursename=course.GetName();
                Integer capacity=course.GetCapacity();
                Integer currentTaken=course.getAvialable();
                List<String> student=course.getStudents();
                student.sort(String.CASE_INSENSITIVE_ORDER);
                String s="[";
                for(String stud:student){
                    s=s+stud.trim()+",";
                }
                if (s.length() > 1) {
                    s=s.substring(0,s.length()-1);
                }
                s=s+"]";

                replaymassage="("+ coursenum.toString()+") "+coursename+"\0"+currentTaken.toString()+"/"+capacity.toString()+"\0"+s;
                replay = new ACK(ackOp, temp.GetOPcode(), replaymassage);
            }

        }
        if(opcode==8){//- Studentstat
            STUDENTSTAT temp=(STUDENTSTAT)msg;
            String StudentName= temp.GetStudName();
            if(!userIsExist(StudentName) ||!isLog(username)|| (!isLogged) || getType()=='S'){
                replay= new ERR(errorOp,opcode);
            }
            else{
                List<Integer>courses=data.getUserMap().get(StudentName).getNumCourses();
                Collections.sort(courses, Comparator.comparingInt(o -> data.getCourseMap().get(o).getCourseid()));
                String s="[";
                for(Integer i: courses){
                    s=s+i.toString().trim()+",";
                }
                if (s.length() > 1) {
                    s=s.substring(0,s.length()-1);
                }
                s=s+"]";
                replaymassage=StudentName.trim()+"\0"+s;
                replay=new ACK(ackOp, temp.GetOPcode(), replaymassage);
            }
        }
        if(opcode==9) { //-isregister
            ISREGISTERED temp= (ISREGISTERED)msg;
            int coursenum=temp.getNumOf();
            if ( (!isLogged)|| getType() == 'A') {
                replay= new ERR(errorOp,opcode);
            }
            else{
                boolean Isregister=data.getUserMap().get(username).getNumCourses().contains(coursenum);
                if(Isregister)
                    replaymassage="REGISTERED";
                else
                    replaymassage="NOT REGISTERED";
                replay=new ACK(ackOp, temp.GetOPcode(), replaymassage.trim());
            }

        }
        if(opcode==10) {// - unregister
            UNREGISTER temp= (UNREGISTER)msg;
            int coursenum=temp.getNumOf();
            if (!isLogged ||!courseIsExist(coursenum)  || getType() == 'A') {
                replay= new ERR(errorOp,opcode);
            }
            else{
                boolean Isregister=data.getUserMap().get(username).getNumCourses().contains(coursenum);
                if(!Isregister){
                    replay= new ERR(errorOp,opcode);}
                else{
                    removeCourse(username,coursenum);
                    replaymassage="";
                    replay=new ACK(ackOp, temp.GetOPcode(), replaymassage);
                }
            }
        }
        if(opcode==11) {  // - Mycourses
            MYCOURSES temp=(MYCOURSES)msg;
            if(!isLogged || getType()=='A'){
                replay= new ERR(errorOp,opcode);}
            else{
                List<Integer>courses=data.getUserMap().get(username).getNumCourses();
                Collections.sort(courses, Comparator.comparingInt(o -> data.getCourseMap().get(o).getCourseid()));
                String s="[";
                for(Integer i: courses){
                    s=s+i.toString().trim()+",";
                }
                if (s.length() > 1) {
                    s=s.substring(0,s.length()-1);
                }
                s=s+"]";
                replaymassage=s;
                replay=new ACK(ackOp, temp.GetOPcode(), replaymassage.trim());
            }
        }
        return replay;
    }

    @Override
    public boolean shouldTerminate() {
        return Terminate;
    }
    public boolean userIsExist(String username){
        return (data.getUserMap().containsKey(username));
    }
    public boolean isLog(String username){
        return data.getUserMap().get(username).isLog();
    }
    public char getType(){return type;}
    public boolean courseIsExist(int coursenum){
        return data.getCourseMap().containsKey(coursenum);
    }

    public boolean isRegisterToKdamCourse(int coursenum , String username){
        List<Integer> kdam =data.getCourseMap().get(coursenum).GetKdam();
        User student=data.getUserMap().get(username);
        for(Integer i : kdam){
            if(!student.getNumCourses().contains(i)) {
                return false;
            }
        }
        return true;
    }
    public void addCourse(String username,int coursenum){
        String coursename=data.getCourseMap().get(coursenum).GetName();
        data.getUserMap().get(username).addCourse(coursenum,coursename);
        data.getCourseMap().get(coursenum).addStudent(username);
    }
    public void removeCourse(String username, int coursenum){
        String coursename=data.getCourseMap().get(coursenum).GetName();
        data.getUserMap().get(username).removeCourse(coursenum,coursename);
        data.getCourseMap().get(coursenum).removeStudent(username);
    }
}

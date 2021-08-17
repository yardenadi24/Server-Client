package bgu.spl.net;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.LinkedList;
import java.util.List;

public class User {
    //--fields--//
    private char type;
    private Boolean isLog;
    private String UserName;
    private String Password;
    private List<String> Courses;
    private List<Integer> numCourses;

    //--constructor--//
    public User(char _type,String _Username,String _Password){
        type = _type;
        UserName = _Username;
        Password = _Password;
        isLog = false;
        Courses = new LinkedList<>();
        numCourses=new LinkedList<>();
    }
    public Boolean isLog(){return isLog;}
    public String getPassword(){return Password; }
    public String getUserName(){return UserName; }

    public void setLog(Boolean log) {
        isLog = log;
    }
    public char getType(){return type; }
    public void addCourse(int coursenum,String coursename){
        Courses.add(coursename);
        numCourses.add(coursenum);
    }
    public void removeCourse(int coursenum,String coursename){
        if(numCourses.contains(coursenum)) {
           int index= Courses.indexOf(coursename);
           Courses.remove(index);
           index=numCourses.indexOf(coursenum);
            numCourses.remove(index);
        }
    }
    public List<String> getCourses(){return Courses;}
    public List<Integer> getNumCourses(){return numCourses;}
}

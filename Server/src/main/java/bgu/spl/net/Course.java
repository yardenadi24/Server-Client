package bgu.spl.net;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Course {

    //--fields--//
    private String Name;
    private int Num;
    private List<Integer> Kdam;
    private int Avialable;
    private int Capacity;
    private List<String> students;
    private int courseid;
    //--constructor--//
    public Course(String _name, int _num,ArrayList<Integer> _kdams,int _capa,int _courseid){
        Name = _name;
        Num = _num;
        Kdam = _kdams;
        Capacity = _capa;
        Avialable = Capacity;
        students=new LinkedList<>();
        courseid=_courseid;
    }

    //--Getters--//
    public String  GetName(){return Name;}
    public int GetNum(){return Num;}
    public List<Integer> GetKdam(){return Kdam;}
    public int GetCapacity(){return Capacity;}
    public int getAvialable(){return Avialable;}

    //--Setters--//
    public void addStudent(String username){
        students.add(username);
        Avialable--;
    }
    public void removeStudent(String username){
        students.remove(username);
        Avialable++;
    }
    public List<String> getStudents(){return students;}
    public int getCourseid(){return courseid;}
}

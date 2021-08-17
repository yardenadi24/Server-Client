package bgu.spl.net;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
	//--fields--//
	ConcurrentHashMap<Integer,Course> MapCourses;
	ConcurrentHashMap<String,User> MapUsers;

	//to prevent user from creating new Database
	private Database() {
		MapCourses = new ConcurrentHashMap<>();
		MapUsers = new ConcurrentHashMap<>();
		this.initialize("./Courses.txt");
	}

	//--Getters--//
	public Map<String,User> getUserMap(){
		return MapUsers;
	}
	public Map<Integer,Course> getCourseMap(){ return MapCourses;}
	public String getUserPassword(String UserName){
		return MapUsers.get(UserName).getPassword();
	}

	/**
	 * Retrieves the single instance of this class.
	 */

	//--singleton holder class--//
	private static class SingletonHolder{
		private static Database instance = new Database();

	}

	public static Database getInstance() {
		Database singleton = SingletonHolder.instance;
		return singleton;
	}
	
	/**
	 * loades the courses from the file path specified 
	 * into the Database, returns true if successful.
	 */
	public boolean initialize(String coursesFilePath) {
	try{
		File Data = new File(coursesFilePath);
		Scanner scan = new Scanner(Data);
		int counter = 0;
		String courseName;
		int numOfcourse;
		int numOfStudents;
		ArrayList<Integer> Kdam = new ArrayList<Integer>();
		int i=0;


		//--scan lines while each line represents a course--//
		while(scan.hasNextLine()){
			i++;
			String StreamData = scan.nextLine();
			String[] Split = StreamData.split("\\|");

			for(String piece: Split) {

				Kdam = new ArrayList<Integer>();

				if (Split[2].length() > 2) {
					String[] kdamArray = Split[2].substring(1, Split[2].length() - 1).split(",");
					for (String course : kdamArray) {
						Kdam.add(Integer.parseInt(course));
					}
				}
			}
				 courseName = Split[1];
				 numOfcourse = Integer.parseInt(Split[0]);
				 numOfStudents = Integer.parseInt(Split[3]);
				Course courseToAdd = new Course(courseName,numOfcourse,Kdam,numOfStudents,i);
				MapCourses.put(courseToAdd.GetNum(),courseToAdd);

			counter++;
		}

	}catch (FileNotFoundException e){e.printStackTrace();}
		return false;
	}


}

package ch.epfl.sweng.studdybuddy;

/**
 * Class representing courses
 */
import java.util.UUID;
public class Course
{
    private String courseName;
    private ID<Course> courseID; //mustn't be final because it will be reset when we read from the DB #kiru

    public Course() {}

    public Course(String courseName)
    {
        this.courseName = courseName;
        courseID = new ID<Course>(courseName);
    }

    public Course(Course sourceCourse)
    {
        this.courseName = sourceCourse.getCourseName();
        this.courseID = sourceCourse.getCourseID();
    }

    public String getCourseName()
    {
        return  courseName;
    }

    public void setCourseName(String name) { courseName = name; }

    public ID<Course> getCourseID()
    {
        return courseID;
    }

    public void setCourseID(ID<Course> courseID)
    {
        this.courseID = courseID;
    }


}

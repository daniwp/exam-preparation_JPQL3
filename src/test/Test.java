package test;

import entity.Student;
import facade.DBFacade;
import javax.persistence.Persistence;

public class Test {
    public static void main(String[] args) {
        DBFacade dbf = new DBFacade(Persistence.createEntityManagerFactory("jpqlDemoPU", null));
        System.out.println(dbf.getStudentSPSum(3));
        System.out.println(dbf.getTotalStudentsSPScore());
        //System.out.println(dbf.getStudentsWithHighestSPScore());
        for (Student s : dbf.getStudentsWithHighestSPScore()) {
            System.out.println(s.getFirstname() + " " + s.getLastname());
        }
        for (Student s : dbf.getStudentsWithLowestSPScore()) {
            System.out.println(s.getFirstname() + " " + s.getLastname());
        }
    }
}

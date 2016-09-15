package facade;

import entity.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class DBFacade {

    EntityManagerFactory emf;

    public DBFacade(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Student> getAllStudents() {
        EntityManager em = getEntityManager();

        try {
            return em.createNamedQuery("Student.findAll").getResultList();
        } finally {
            em.close();
        }
    }

    public List<Student> getStudentsWithFirstname(String fname) {
        EntityManager em = getEntityManager();

        try {
            return em.createQuery("SELECT s FROM Student s WHERE s.firstname LIKE :fname", Student.class).setParameter("fname", fname).getResultList();
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    public List<Student> getStudentsWithLastname(String lname) {
        EntityManager em = getEntityManager();

        try {
            return em.createQuery("SELECT s FROM Student s WHERE s.firstname LIKE :lname", Student.class).setParameter("lname", lname).getResultList();
        } finally {
            em.close();
        }
    }

    public int getStudentSPSum(int id) {
        EntityManager em = getEntityManager();

        try {
            return ((Long) em.createQuery("SELECT SUM(st.score) FROM Studypoint st WHERE st.student.id LIKE :studentid").setParameter("studentid", id).getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public int getTotalStudentsSPScore() {
        EntityManager em = getEntityManager();

        try {
            return em.createQuery("SELECT MAX((SELECT SUM(s.score) FROM Studypoint s)) FROM Student s", BigDecimal.class).getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

    public List<Student> getStudentsWithHighestSPScore() {
        EntityManager em = getEntityManager();
        List<Student> students = new ArrayList();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "select MAX(total), sID FROM student,(select SUM(s.`SCORE`) as total, s.`STUDENT_ID` AS sID from studypoint s Group by s.STUDENT_ID ORDER By `SCORE` DESC) g").getResultList();
            for (Object[] row : rows) {
                students.add(em.find(Student.class, row[1]));
            }
            return students;
        } finally {
            em.close();
        }
    }

    public List<Student> getStudentsWithLowestSPScore() {
        EntityManager em = getEntityManager();
        List<Student> students = new ArrayList();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "select MIN(total), sID FROM student,(select SUM(s.`SCORE`) as total, s.`STUDENT_ID` AS sID from studypoint s Group by s.STUDENT_ID ORDER By `SCORE` DESC) g").getResultList();
            for (Object[] row : rows) {
                students.add(em.find(Student.class, row[1]));
            }
            return students;
        } finally {
            em.close();
        }
    }

    public <T> List<T> createEntity(T... entities) {
        EntityManager em = getEntityManager();
        List<T> updatedEntities = new ArrayList();
        try {
            em.getTransaction().begin();
            for (T entity : entities) {
                em.persist(entity);
                em.flush();
                updatedEntities.add(entity);
            }
            return updatedEntities;
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    public <T> List<T> updateEntity(T... entities) {
        EntityManager em = getEntityManager();
        List<T> updatedEntities = new ArrayList();
        try {
            em.getTransaction().begin();
            for (T entity : entities) {
                em.merge(entity);
                em.flush();
                updatedEntities.add(entity);
            }
            return updatedEntities;
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    public <T> void deleteEntities(T... entities) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (T entity : entities) {
                em.remove(entity);
                em.flush();
            }
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }
}

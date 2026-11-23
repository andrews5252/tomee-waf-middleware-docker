package hu.rob.jpa;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SzerverNaplo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String szerverNev;
    private String esemeny;

    public SzerverNaplo() {}

    public SzerverNaplo(String szerverNev, String esemeny) {
        this.szerverNev = szerverNev;
        this.esemeny = esemeny;
    }

    @Override
    public String toString() {
        return "SzerverNaplo [id=" + id + ", szerverNev=" + szerverNev + ", esemeny=" + esemeny + "]";
    }
}
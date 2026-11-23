package hu.rob.jpa;


import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class NaploKezeloBean implements NaploKezeloRemote {

    @PersistenceContext(unitName = "rob-unit")
    private EntityManager em;

    @Override
    public void mentes(String szerverNev, String esemeny) {
        SzerverNaplo ujBejegyzes = new SzerverNaplo(szerverNev, esemeny);
        em.persist(ujBejegyzes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SzerverNaplo> osszesLekerdezese() {
        return em.createQuery("SELECT n FROM SzerverNaplo n").getResultList();
    }
}
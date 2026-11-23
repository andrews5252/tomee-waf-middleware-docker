
package hu.rob.jpa;

import java.util.List;
import javax.ejb.Remote;

@Remote
public interface NaploKezeloRemote {
    void mentes(String szerverNev, String esemeny);
    List<SzerverNaplo> osszesLekerdezese();
}

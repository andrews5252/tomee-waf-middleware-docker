package hu.rob.jpa;


import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "FeladatQueue")
})
public class RobQueueMdb implements MessageListener {

    @EJB
    private NaploKezeloRemote naploKezelo;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String szoveg = ((TextMessage) message).getText();
                System.out.println(">>> [MDB QUEUE FELDOLGOZÓ] Feladat elvégezve: " + szoveg);

                // Mentés a MySQL adatbázisba a robJPA szolgáltatáson keresztül
                naploKezelo.mentes("SRV-TOMEE-MDB", szoveg);
                System.out.println(">>> [MDB -> JPA] Sikeresen mentve a MySQL adatbázisba!");
           
               	naploKezelo.mentes("SRV-TOMEE-MDB", szoveg);
            	System.out.println(">>> [MDB -> JPA] Sikeresen mentve a MySQL adatbázisba!");

            
            }
        } catch (Exception e) {
            System.err.println(">>> [MDB HIBA] Nem sikerült menteni a JPA rétegbe!");
            e.printStackTrace();
        }
    }
}

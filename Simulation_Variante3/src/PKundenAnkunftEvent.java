import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class PKundenAnkunftEvent extends Event<KundeEntity> {
	
	private final int MAXIMALE_LAENGE = 7;
    private Schalter_Model meinModel;
    

    public PKundenAnkunftEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        meinModel = (Schalter_Model) owner;

    }

    public void eventRoutine(KundeEntity kunde) {

        // cast notwendig
        //KundeEntity kunde =  who;

    	if(meinModel.prioritaetkundenReiheQueue.length() >= MAXIMALE_LAENGE) {
    		meinModel.verlorenePrioritaetsKunden.update();
    	} else {
    		// Kunde in Warteschlange
            meinModel.prioritaetkundenReiheQueue.insert(kunde);
            sendTraceNote("Laenge der Prioritaetskundenreihe: " + 
                meinModel.prioritaetkundenReiheQueue.length());
    	}
    	
        // Schalter frei?
        if (!meinModel.freierPrioritaetsSchalterQueue.isEmpty()) {
            // Schalter frei, von entsprechender WS holen
            SchalterEntity schalter = meinModel.freierPrioritaetsSchalterQueue.first();
            // extra Entfernen von WS notwendig
            meinModel.freierPrioritaetsSchalterQueue.remove(schalter);
            
            // Schalter in entsprechende WS um Referenz nicht zu verlieren
            meinModel.besetzterPrioritaetsSchalterQueue.insert(schalter);
            
            // Kunden aus Kundenreihe um am Schalter bedient zu werden
            // -> Referenz auf Kunden bereits vorhanden - kein first() n�tig!
            meinModel.prioritaetkundenReiheQueue.remove(kunde);
            
            // Bedienungsende Ereignis erzeugen
            BedienEndeEvent bedienEnde = 
                new BedienEndeEvent (meinModel, "Prioritaetbedienung Ende", true, true);
            // eintragen in Ereignisliste
            bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
        }
    }
}

import desmoj.core.simulator.*;

public class KundenAnkunftEvent extends Event<KundeEntity> {
	
	private final int MAXIMALE_LAENGE = 7;
    private Schalter_Model meinModel;
    

    public KundenAnkunftEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        meinModel = (Schalter_Model) owner;

    }

    public void eventRoutine(KundeEntity kunde) {

        // cast notwendig
        //KundeEntity kunde =  who;

    	if(meinModel.kundenReiheQueue.length() >= MAXIMALE_LAENGE) {
    		meinModel.verloreneKunden.update();
    	} else {
    		// Kunde in Warteschlange
            meinModel.kundenReiheQueue.insert(kunde);
            sendTraceNote("Laenge der Kundenreihe: " + 
                meinModel.kundenReiheQueue.length());
    	}
    	
        // Schalter frei?
        if (!meinModel.freieSchalterQueue.isEmpty()) {
            // Schalter frei, von entsprechender WS holen
            SchalterEntity schalter = meinModel.freieSchalterQueue.first();
            // extra Entfernen von WS notwendig
            meinModel.freieSchalterQueue.remove(schalter);
            
            // Schalter in entsprechende WS um Referenz nicht zu verlieren
            meinModel.besetzteSchalterQueue.insert(schalter);
            
            // Kunden aus Kundenreihe um am Schalter bedient zu werden
            // -> Referenz auf Kunden bereits vorhanden - kein first() n�tig!
            meinModel.kundenReiheQueue.remove(kunde);
            
            // Bedienungsende Ereignis erzeugen
            BedienEndeEvent bedienEnde = 
                new BedienEndeEvent (meinModel, "Bedienung Ende", true, false);
            // eintragen in Ereignisliste
            bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
        }
    }
}

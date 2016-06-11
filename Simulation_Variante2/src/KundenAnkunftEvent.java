import desmoj.core.simulator.*;

public class KundenAnkunftEvent extends Event<KundeEntity> {
	
	private final int MAXIMALE_LAENGE = 10;
    private Schalter_Model meinModel;
    

    public KundenAnkunftEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        meinModel = (Schalter_Model) owner;

    }

    public void eventRoutine(KundeEntity kunde) {
   
        // cast notwendig
        //KundeEntity kunde =  who;
    	int minIndex = -1;
    	int minLength = Integer.MAX_VALUE;
    	for(int i = 0; i < meinModel.ANZAHL_WS; i++) {
    		//System.out.println(minLength + "-" + meinModel.kundenReiheQueue.get(i).length());
    		if(minLength > meinModel.kundenReiheQueue.get(i).length()){
    			minIndex = i;
    			minLength = meinModel.kundenReiheQueue.get(i).length();
    		}
    	}

    	if(meinModel.kundenReiheQueue.get(minIndex).length() >= MAXIMALE_LAENGE) {
    		meinModel.verloreneKunden.update();
    	} else {
    		// Kunde in Warteschlange
            meinModel.kundenReiheQueue.get(minIndex).insert(kunde);
            sendTraceNote("Laenge der Kundenreihe: " + 
                meinModel.kundenReiheQueue.get(minIndex).length());
    	}
    	
        // Schalter frei?
        if (!meinModel.freieSchalterQueue.get(minIndex).isEmpty()) {
            // Schalter frei, von entsprechender WS holen
            SchalterEntity schalter = meinModel.freieSchalterQueue.get(minIndex).first();
            // extra Entfernen von WS notwendig
            meinModel.freieSchalterQueue.get(minIndex).remove(schalter);
            
            // Schalter in entsprechende WS um Referenz nicht zu verlieren
            meinModel.besetzteSchalterQueue.get(minIndex).insert(schalter);
            
            // Kunden aus Kundenreihe um am Schalter bedient zu werden
            // -> Referenz auf Kunden bereits vorhanden - kein first() n�tig!
            meinModel.kundenReiheQueue.get(minIndex).remove(kunde);
            
            // Bedienungsende Ereignis erzeugen
            BedienEndeEvent bedienEnde = 
                new BedienEndeEvent (meinModel, "Bedienung Ende", minIndex, true);
            // eintragen in Ereignisliste
            bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
        }
    }
}

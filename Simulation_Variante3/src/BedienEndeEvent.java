import desmoj.core.simulator.*;

// stellt das Ende eine Bedienvorgangs am Schlater dar
public class BedienEndeEvent extends Event<KundeEntity> {

    // nuetzliche Referenz auf entsprechendes Modell
    private Schalter_Model meinModel;
    private boolean prioritaet;

    // Konstruktor
  	// Par 1: Modellzugehoerigkeit
	  // Par 2: Name des Ereignisses
	  // Par 3: show in trace?
    public BedienEndeEvent(Model owner, String name, boolean showInTrace, boolean prioritaet) {
        super(owner, name, showInTrace);

        meinModel = (Schalter_Model) owner;
        this.prioritaet = prioritaet;
    }
    
    // Beschreibung der Aktionen, die den Kunden nach Beendigung der
    // Bedienung am Schalter betreffen
 
    public void eventRoutine(KundeEntity kunde) {
        
    	if(prioritaet) {
    		if(!meinModel.prioritaetkundenReiheQueue.isEmpty()){
    			// Kunde vorhanden, aus Kundenreihe entfernen
                KundeEntity naechsterKunde = 
                	meinModel.prioritaetkundenReiheQueue.first();
                meinModel.prioritaetkundenReiheQueue.remove(naechsterKunde);
                
                // Bedienungsende Ereignis erzeugen
                BedienEndeEvent bedienEnde = 
                    new BedienEndeEvent (meinModel, "Bedienung Ende", true, true);
                // eintragen in Ereignisliste
                bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
    		}
    		// wartet ein weiterer Kunde auf Bedienung?
    		else if (!meinModel.kundenReiheQueue.isEmpty()) {
                // Kunde vorhanden, aus Kundenreihe entfernen
                KundeEntity naechsterKunde = 
                	meinModel.kundenReiheQueue.first();
                meinModel.kundenReiheQueue.remove(naechsterKunde);
                
                // Bedienungsende Ereignis erzeugen
                BedienEndeEvent bedienEnde = 
                    new BedienEndeEvent (meinModel, "Bedienung Ende", false, true);
                // eintragen in Ereignisliste
                bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
            }
            else {
                // kein Kunde wartet
                // Schalter frei -> aus entsprechender WS
                SchalterEntity schalter = meinModel.besetzterPrioritaetsSchalterQueue.first();
                meinModel.besetzterPrioritaetsSchalterQueue.remove(schalter);
                
                // ... in entsprechende WS
                meinModel.freierPrioritaetsSchalterQueue.insert(schalter);
            }
    	} else {
    		// wartet ein weiterer Kunde auf Bedienung?
            if (!meinModel.kundenReiheQueue.isEmpty()) {
                // Kunde vorhanden, aus Kundenreihe entfernen
                KundeEntity naechsterKunde = 
                	meinModel.kundenReiheQueue.first();
                meinModel.kundenReiheQueue.remove(naechsterKunde);
                
                // Bedienungsende Ereignis erzeugen
                BedienEndeEvent bedienEnde = 
                    new BedienEndeEvent (meinModel, "Bedienung Ende", false, true);
                // eintragen in Ereignisliste
                bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
            }
            else {
                // kein Kunde wartet
                // Schalter frei -> aus entsprechender WS
                SchalterEntity schalter = meinModel.besetzteSchalterQueue.first();
                meinModel.besetzteSchalterQueue.remove(schalter);
                
                // ... in entsprechende WS
                meinModel.freieSchalterQueue.insert(schalter);
            }
    	}

        
    }
}

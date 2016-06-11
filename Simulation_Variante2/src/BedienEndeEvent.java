import desmoj.core.simulator.*;

// stellt das Ende eine Bedienvorgangs am Schlater dar
public class BedienEndeEvent extends Event<KundeEntity> {

    // nuetzliche Referenz auf entsprechendes Modell
    private Schalter_Model meinModel;
    private int schalterNum;

    // Konstruktor
  	// Par 1: Modellzugehoerigkeit
	  // Par 2: Name des Ereignisses
	  // Par 3: show in trace?
    public BedienEndeEvent(Model owner, String name, int num, boolean showInTrace) {
        super(owner, name, showInTrace);
        meinModel = (Schalter_Model) owner;
        schalterNum = num;
    }
    
    // Beschreibung der Aktionen, die den Kunden nach Beendigung der
    // Bedienung am Schalter betreffen
 
    public void eventRoutine(KundeEntity kunde) {
        
        // cast notwendig
        //KundeEntity kunde = (KundeEntity) who;
        
        // in diesem einfachen Fall keinerlei Aktionen fuer Kunde notwendig 
        // -> automatisch zum garbage collector

        // wartet ein weiterer Kunde auf Bedienung?
        if (!meinModel.kundenReiheQueue.get(schalterNum).isEmpty()) {
            // Kunde vorhanden, aus Kundenreihe entfernen
            KundeEntity naechsterKunde = 
            	meinModel.kundenReiheQueue.get(schalterNum).first();
            meinModel.kundenReiheQueue.get(schalterNum).remove(naechsterKunde);
            
            // Bedienungsende Ereignis erzeugen
            BedienEndeEvent bedienEnde = 
                new BedienEndeEvent (meinModel, "Bedienung Ende", schalterNum, true);
            // eintragen in Ereignisliste
            bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
        }
        else {
            // kein Kunde wartet
            // Schalter frei -> aus entsprechender WS
            SchalterEntity schalter = meinModel.besetzteSchalterQueue.get(schalterNum).first();
            meinModel.besetzteSchalterQueue.get(schalterNum).remove(schalter);
            
            // ... in entsprechende WS
            meinModel.freieSchalterQueue.get(schalterNum).insert(schalter);
        }
    }
}

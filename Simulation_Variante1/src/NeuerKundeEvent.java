import desmoj.core.simulator.*;

public class NeuerKundeEvent extends ExternalEvent {

    private Schalter_Model meinModel;

    public NeuerKundeEvent (Model owner, String name, boolean showInTrace) {
	   super(owner, name, showInTrace);

	   meinModel = (Schalter_Model) owner;
    }
    
    public void eventRoutine() {
	
        // neuen Kunden erzeugen
        KundeEntity kunde = new KundeEntity (meinModel, "Kunde", true); 
        
        // neues KundenAnkunfts-Ereignis erzeugen
        KundenAnkunftEvent kundenAnkunft =
            new KundenAnkunftEvent (meinModel, "Kundenankunft", true);

        // dieses aktivieren
        kundenAnkunft.schedule(kunde, new TimeSpan(0.0));

        // neues Ereignis fuer naechsten Kunden erzeugen eintragen
        NeuerKundeEvent neuerKunde = 
            new NeuerKundeEvent(meinModel, "Kundenkreation", true);
        neuerKunde.schedule (new TimeSpan(meinModel.getKundenAnkunftsZeit()));

    }
}

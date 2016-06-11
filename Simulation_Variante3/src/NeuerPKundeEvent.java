import desmoj.core.simulator.*;

public class NeuerPKundeEvent extends ExternalEvent {

    private Schalter_Model meinModel;

    public NeuerPKundeEvent (Model owner, String name, boolean showInTrace) {
	   super(owner, name, showInTrace);

	   meinModel = (Schalter_Model) owner;
    }
    
    public void eventRoutine() {
	
        // neuen Kunden erzeugen
        KundeEntity kunde = new KundeEntity (meinModel, "Kunde", true);

        // neues KundenAnkunfts-Ereignis erzeugen
        PKundenAnkunftEvent kundenAnkunft =
            new PKundenAnkunftEvent (meinModel, "Kundenankunft", true);

        // dieses aktivieren
        kundenAnkunft.schedule(kunde, new TimeSpan(0.0));

        // neues Ereignis fuer naechsten Kunden erzeugen eintragen
        NeuerPKundeEvent neuerKunde = 
            new NeuerPKundeEvent(meinModel, "Kundenkreation", true);
        neuerKunde.schedule (new TimeSpan(meinModel.getPrioritaetsKundenAnkunftsZeit()));

    }
}

import desmoj.core.simulator.*;
import desmoj.core.statistic.Count;
import desmoj.core.dist.*;

public class Schalter_Model extends Model {
	
	private final double ANKUNFTSZEIT_DURCHSCHNITT= 2;
	private final double PRIORITAET_ANKUNFTSZEIT_DURCHSCHNITT = 10;
	private final double BEDIENZEIT_UNTERGRENZE = 2;
	private final double BEDIENZEIT_OBERGRENZE = 10;
	private final double ANZAHL_SCHALTER = 1;
	
	public Count verloreneKunden;
	public Count verlorenePrioritaetsKunden;
	
	private ContDistExponential kundenAnkunftsZeit;
	private ContDistExponential prioritaetsKundenAnkunftsZeit;

    public double getKundenAnkunftsZeit() {
    	// sample(): Returns the next negative exponential pseudo random number.
    	System.out.println(kundenAnkunftsZeit.sample());
	   return kundenAnkunftsZeit.sample();
    }

    public double getPrioritaetsKundenAnkunftsZeit() {
		// sample(): Returns the next negative exponential pseudo random number.
		return prioritaetsKundenAnkunftsZeit.sample();
	}
    
	private ContDistUniform bedienZeit;

    public double getBedienZeit() {
    	// sample(): Returns the next negative exponential pseudo random number.
	   return bedienZeit.sample();
    }

    protected Queue<KundeEntity> kundenReiheQueue;

	protected Queue<SchalterEntity> freieSchalterQueue;
	
	protected Queue<SchalterEntity> besetzteSchalterQueue;

    public Schalter_Model(Model owner, String name, boolean showInReport, 
                            boolean showIntrace) {
    	super(owner, name, showInReport, showIntrace);
    }

    public String description() {
    	return "Schalter1_e Modell (Ereignis orientiert):" +
               "simuliert einen Bankschalter, wo ankommende Kunden zuerst in einer "+
               "Warteschlange eingereiht werden. Wenn der Schalter frei ist, "+
               "werden sie bedient.";
    }	

    public void doInitialSchedules() {
        // erstes Kundenkreation-Ereignis erzeugen 
        NeuerKundeEvent ersterKunde =
		new NeuerKundeEvent(this, "Kundenkreation", true);
         // erstes Kundenkreations-Ereignis in Ereignisliste eintragen
         ersterKunde.schedule(new TimeSpan(getKundenAnkunftsZeit()));
         
         // erstes PKundenkreation-Ereignis erzeugen
  		NeuerPKundeEvent ersterPKunde = new NeuerPKundeEvent(this,
  				"PKundenkreation", true);
  		// erstes PKundenkreations-Ereignis in Ereignisliste eintragen
  		ersterPKunde.schedule(new TimeSpan(getPrioritaetsKundenAnkunftsZeit()));		
     
    }

    public void init() {

    	kundenAnkunftsZeit = 
            new ContDistExponential(this, "Ankunftszeitintervall",ANKUNFTSZEIT_DURCHSCHNITT, true,true);
    	kundenAnkunftsZeit.setNonNegative(true);
    	prioritaetsKundenAnkunftsZeit = new ContDistExponential(this,
				"Ankunftszeitintervall Priorit�t",
				PRIORITAET_ANKUNFTSZEIT_DURCHSCHNITT, true, true);
		prioritaetsKundenAnkunftsZeit.setNonNegative(true);

    	bedienZeit = 
            new ContDistUniform(this, "Bedienzeiten", BEDIENZEIT_UNTERGRENZE, BEDIENZEIT_OBERGRENZE, true, true);	
       	kundenReiheQueue = new Queue<KundeEntity>(this, "Kunden-Warteschlange",true, true);	
    	freieSchalterQueue = new Queue<SchalterEntity>(this, "freie Schalter WS",true, true);
    	
    	verloreneKunden = new Count(this, "Verlorene Kunden", true, true);
		verlorenePrioritaetsKunden = new Count(this, "Verlorene Priorit�tskunden", true, true);

    	SchalterEntity schalter;
    	for (int i = 1; i<=ANZAHL_SCHALTER; i++){
    		schalter = new SchalterEntity(this, "Fahrkartenschalter", true);
    		freieSchalterQueue.insert(schalter);
    	} 
        besetzteSchalterQueue = new Queue<SchalterEntity>(this, "besetzte Schalter WS", true, true);
    }

    public static void main(java.lang.String[] args) {

    	Experiment schalterExperiment = 
            new Experiment("Variante1-ereignis");
        Schalter_Model sch_e_model = 
            new Schalter_Model(null, "Schalter Modell", true, true);  
        
        sch_e_model.connectToExperiment(schalterExperiment);

        schalterExperiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
        schalterExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));
        schalterExperiment.stop(new TimeInstant(600));

        schalterExperiment.start(); 
        schalterExperiment.report();
        schalterExperiment.finish();
	
    }
}

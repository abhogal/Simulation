import java.util.ArrayList;
import java.util.List;

import desmoj.core.dist.ContDistNormal;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Count;

public class Schalter_Model extends Model {
	
	private final double ANKUNFTSZEIT_DURCHSCHNITT= 1;
	private final double ANKUNFTSZEIT_ABWEICHUNG = 	0.35;
	private final double BEDIENZEIT_UNTERGRENZE = 2;
	private final double BEDIENZEIT_OBERGRENZE = 15;
	public final int ANZAHL_WS = 2;
	
	public Count verloreneKunden;
	private ContDistNormal kundenAnkunftsZeit;

    public double getKundenAnkunftsZeit() {
    	// sample(): Returns the next negative exponential pseudo random number.
	   return kundenAnkunftsZeit.sample();
    }

	private ContDistUniform bedienZeit;

    public double getBedienZeit() {
    	// sample(): Returns the next negative exponential pseudo random number.
	   return bedienZeit.sample();
    }

    protected List<Queue<KundeEntity>> kundenReiheQueue;

	protected List<Queue<SchalterEntity>> freieSchalterQueue;
	
	protected List<Queue<SchalterEntity>> besetzteSchalterQueue;

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
    }

    public void init() {

    	kundenAnkunftsZeit = 
            new ContDistNormal(this, "Ankunftszeitintervall",ANKUNFTSZEIT_DURCHSCHNITT,ANKUNFTSZEIT_ABWEICHUNG,true,true);
    	kundenAnkunftsZeit.setNonNegative(true);
    	//kundenAnkunftsZeit.setSeed(1234567890);
        bedienZeit = 
            new ContDistUniform(this, "Bedienzeiten", BEDIENZEIT_UNTERGRENZE, BEDIENZEIT_OBERGRENZE, true, true);	
       	
        kundenReiheQueue = new ArrayList<Queue<KundeEntity>>();
       	for(int i=0; i < ANZAHL_WS; i++) 
           	kundenReiheQueue.add(new Queue<KundeEntity>(this, "Kunden-Warteschlange"+i,true, true));
       	
       	freieSchalterQueue = new ArrayList<Queue<SchalterEntity>>();
       	for(int i=0; i < ANZAHL_WS; i++) 
       		freieSchalterQueue.add(new Queue<SchalterEntity>(this, "freie Schalter WS"+i,true, true));
       	
    	verloreneKunden = new Count(this, "Verlorene Kunden", true, true);
    	
       	SchalterEntity schalter;
    	for (int i = 0; i < ANZAHL_WS; i++){
    		schalter = new SchalterEntity(this, "Fahrkartenschalter"+i, true);
    		System.out.println(i);
    		freieSchalterQueue.get(i).insert(schalter);
    	} 
    	
        besetzteSchalterQueue = new ArrayList<Queue<SchalterEntity>>();
       	for(int i=0; i < ANZAHL_WS; i++) 
       		besetzteSchalterQueue.add(new Queue<SchalterEntity>(this, "besetzte Schalter WS"+i, true, true));
    }

    public static void main(java.lang.String[] args) {

    	Experiment schalterExperiment = 
            new Experiment("Variante2-ereignis");
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

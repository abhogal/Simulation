import desmoj.core.simulator.*;
import desmoj.core.dist.*;

/*
 main-Klasse vom einfachen Schalter-Modell (abgeleitet von 
 desmoj.core.simulator.Model) - stellt die notwendige Infrastruktur zur Verfuegung
*/

public class Schalter_Model extends Model {
	
	/**
	* Zufallszahlengenerator fuer Kundenankuenfte
	*/
	private ContDistExponential kundenAnkunftsZeit;

    /**
    * liefert eine Zufallszahl fuer Kundenankunftszeit
     */
    public double getKundenAnkunftsZeit() {
    	// sample(): Returns the next negative exponential pseudo random number.
	   return kundenAnkunftsZeit.sample();
    }

	
    // Zufallszahlengenerator zur Ermittlung der Bedienzeit am Schalter
	private ContDistUniform bedienZeit;

    /**
    * liefert eine Zufallszahl fuer Bedienzeit
    */
    public double getBedienZeit() {
    	// sample(): Returns the next negative exponential pseudo random number.
	   return bedienZeit.sample();
    }
    
	/**
    * Warteschlange fuer wartende Kunden
    * jeder Kunde kommt zuerst hier hinein
    *
    * liefert elementare Statistik
    */
    protected Queue<KundeEntity> kundenReiheQueue;


	/**
    * Warteschlange fuer freie Schalter
    * -> elementare Statistik erhaeltlich
    * -> mehrere Schalter koennen verwaltet werden
	*/
	protected Queue<SchalterEntity> freieSchalterQueue;
	
	/**
    * Warteschlagen fuer besetzte Schalter
    * -> Referenzen auf besetzte Schalter gehen sonst verloren
    *    (beliebige andere Datenstruktur verwendbar, 
    *    jedoch Nutzen wie z.B. elementare Statistik)
    * -> zusaetzliche elementare Statistik moeglich
	*/
	protected Queue<SchalterEntity> besetzteSchalterQueue;

    /**
     * Konstruktor
    */
    public Schalter_Model(Model owner, String name, boolean showInReport, 
                            boolean showIntrace) {
    	super(owner, name, showInReport, showIntrace);
    }


    /**
     * Kurzbeschreibung des Modells
     */
    public String description() {
    	return "Schalter1_e Modell (Ereignis orientiert):" +
               "simuliert einen Bankschalter, wo ankommende Kunden zuerst in einer "+
               "Warteschlange eingereiht werden. Wenn der Schalter frei ist, "+
               "werden sie bedient.";
    }	


    /**
    * ersten Kunden erzeugen und in Ereignisliste eintragen
    * -> erste Kundenankunft
     */
    public void doInitialSchedules() {

        // erstes Kundenkreation-Ereignis erzeugen 
        //NeuerKundeEvent ersterKunde =
			//new NeuerKundeEvent(this, "Kundenkreation", true);

         // erstes Kundenkreations-Ereignis in Ereignisliste eintragen
         //ersterKunde.schedule(new TimeSpan(getKundenAnkunftsZeit()));
    }


    /**
    * Initialisierung der benutzten DESMO-J Infrastruktur
     */
    public void init() {
		
    	// Generator fuer Ankunftszeiten initialisieren
    	// Par 1: Modellzugehoerigkeit
    	// Par 2: Name des Generators
    	// Par 3: mittlere Zeitdauer in Minuten zwischen Kundenankuenften
    	// Par 4: show in report?
    	// Par 5: show in trace?
    	/**
    	ContDistExponential(Model owner, java.lang.String name, double mean, 
    	boolean showInReport, boolean showInTrace)
		Constructs a simple negative-exponentially distributed pseudo random 
		generator with the given value as mean of the distribution.
    	 */
    	kundenAnkunftsZeit = 
            new ContDistExponential(this, "Ankunftszeitintervall", 3.0, true, true);
    	
    	// negative Ankunftszeitintervalle sind nicht moeglich, 
    	// jedoch liefert Exponentialverteilung auch negative Werte, daher
    	kundenAnkunftsZeit.setNonNegative(true);
    	
    	//kundenAnkunftsZeit.setSeed(1234567890);

    	// Generator fuer Bedienzeiten initialisieren
    	// Par 1: Modellzugehoerigkeit
    	// Par 2: Name des Generators
    	// Par 3: minimale Bedienzeit in Minuten
    	// Par 4: maximale Bedienzeit in Minuten
    	// Par 5: show in report?
    	// Par 6: show in trace?
    	/**
    	 ContDistUniform(Model owner, java.lang.String name, double lowerBorder, 
    	 double upperBorder, boolean showInReport, boolean showInTrace)
		 Creates a stream of pseudo random numbers following a uniform distribution 
		 between the lower and the upper value parameter.
    	 */
        bedienZeit = 
            new ContDistUniform(this, "Bedienzeiten", 0.5, 10.0, true, true);	

    	// Warteschlange fuer Kunden initialisieren
    	// Par 1: Modellzugehoerigkeit
    	// Par 2: Name der Warteschlange
    	// Par 3: show in report?
    	// Par 4: show in trace?
        /**
         Queue(Model owner, java.lang.String name, boolean showInReport, boolean 
         showInTrace)
		 Constructs a simple priority and FIFO based waiting-queue for entities 
		 with a maximum capacity of 2,147,483,647 waiting entities, which should 
		 serve as an approximation of infinite queues sufficiently well for most 
		 purposes.Note that since SimProcesses are derived from Entities, they can 
		 be queued inside this queue, too.
         */
       	kundenReiheQueue = new Queue<KundeEntity>(this, "Kunden-Warteschlange",true, true);	
	
    	// Warteschlange fuer freie Schalter initialisieren
    	// Par 1: Modellzugehoerigkeit
    	// Par 2: Name der Warteschlange
    	// Par 3: show in report?
    	// Par 4: show in trace?
    	freieSchalterQueue = new Queue<SchalterEntity>(this, "freie Schalter WS",true, true);
	
        // den Schalter in freieSchalterQueue einfuegen
        // Hinweis: dies geschieht nicht in doInitialSchedules(), da keine
        //   Ereignisse erzeugt werden
    	SchalterEntity schalter;

        
        // Schalter erzeugen
    	// Par 1: Modellzugehoerigkeit
    	// Par 2: Name der Entitaet
    	// Par 3: show in trace?
    	for (int i = 1; i<=1; i++){
    	schalter = new SchalterEntity(this, "Bankschalter", true);
        // Schalter einfuegen
        freieSchalterQueue.insert(schalter);
    	} 
        
    	// Warteschlange fuer besetzte Schalter initialisieren
    	// Par 1: Modellzugehoerigkeit
    	// Par 2: Name der Warteschlange
    	// Par 3: show in report?
    	// Par 4: show in trace?
        besetzteSchalterQueue = new Queue<SchalterEntity>(this, "besetzte Schalter WS", true, true);
    }

    // Hauptmethode, zustaendig fuer
    // - Experiment instanziieren
    // - Modell instanziieren
    // - Modell mit Experiment verbinden
    //   - Einstellungen fuer Simulation und Ergebnisberichte
    //   - Simulation starten
    //   - Kriterium fuer Simulationsende aufstellen
    //   - Reports initialisieren
    //   - aufraeumen, abschliessen   
    public static void main(java.lang.String[] args) {

    	// neues Experiment erzeugen
    	// ATTENTION!
    	// Use as experiment name a OS filename compatible string!!
    	// Otherwise your simulation will crash!!
    	
    	/********************************************************************** //
    	Experiment is the class that provides the infrastructure for running the 
    	simulation of a model. It contains all data structures necessary to simulate 
    	the model and takes care of all necessary output. To actually run an 
    	experiment, a new instance of the experiment class and a new instance of 
    	the desired model have to be created. To link both instances, call the 
    	connectToExperiment(Experiment e) method of the model instance and pass 
    	the new experiment as a parameter.
    	//************************************************************************/
    	Experiment schalterExperiment = 
            new Experiment("Schalter1-ereignis");
    	
    	/********************************************************************** //
    	The model is supposed to carry references to all static modelcomponents 
    	associated to a model. These are modelcomponents like distributions and 
    	statistic counters. Note that placing all essential components inside the 
    	model class allows you to use object orientation to reuse this model to 
    	implement different versions of it using subclassing for changes necessary 
    	for using other modelling paradigms. This way models can be easily 
    	reimplemented following the process-, event-, transaction- or activity- 
    	oriented paradigm. Note that transaction- and activity-oriented modelling 
    	requires the higher process synchronization mechanisms (Res, Bin, WaitQueue,
    	CondQueue) by Soenke Claassen.
    	
    	public Model(Model owner,
             java.lang.String name,
             boolean showInReport,
             boolean showInTrace)
        name - java.lang.String : The name of this model
		owner - Model : The main model this model is associated to
		showInTrace - boolean : Flag for showing this model in trace-files. Set it 
		to true if model should show up in trace, false if model should not be 
		shown in trace.
		Schalter1_e_model ist eine Ableitung von Modell Klasse aus Desmo-j,
		benutzt genau dieselben Parameter im Konstruktor.
    	//************************************************************************/
        // neues Modell erzeugen
        // Par 1: null markiert main model, sonst Mastermodell angeben
        Schalter_Model sch_e_model = 
            new Schalter_Model(null, "Schalter Modell", true, true);  

        // Modell mit Experiment verbinden
        sch_e_model.connectToExperiment(schalterExperiment);

        // Intervall fuer trace/debug
    	/********************************************************************** //
        tracePeriod(TimeInstant startTime, TimeInstant stopTime)
        Switches the trace output on for the given period of simulation time.
        Dasselbe fuer debug
        //***********************************************************************/
        schalterExperiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
        schalterExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));

        // Ende der Simulation setzen
        // -> hier 4 Stunden (= 240 min)
    	/********************************************************************** //
        // Stops the simulation at the given point of simulation time.
      	//***********************************************************************/

        schalterExperiment.stop(new TimeInstant(240));

        // Experiment zur Zeit 0.0 starten
        schalterExperiment.start(); 

        // -> Simulation laeuft bis Abbruchkriterium erreicht ist
        // -> danach geht es hier weiter

        // Report generieren
        schalterExperiment.report();

        // Ausgabekanaele schliessen, allfaellige threads beenden
        schalterExperiment.finish();
	
    }
}

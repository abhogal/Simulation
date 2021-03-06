import java.util.ArrayList;
import java.util.List;

import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Count;

public class Schalter_Model extends Model {
	public static int indexSeed;

	private final double ANKUNFTSZEIT_DURCHSCHNITT = 2.5;
	private final double PRIORITAET_ANKUNFTSZEIT_DURCHSCHNITT = 15;
	private final double BEDIENZEIT_UNTERGRENZE = 1;
	private final double BEDIENZEIT_OBERGRENZE = 10;
	public final double ANZAHL_SCHALTER = 3;
	public final boolean chooseRandom = true;

	public Count verloreneKunden;
	public Count verlorenePrioritaetsKunden;

	public ContDistUniform chooseRandomQueue;

	private ContDistExponential kundenAnkunftsZeit;
	private ContDistExponential prioritaetsKundenAnkunftsZeit;

	public double getKundenAnkunftsZeit() {
		// sample(): Returns the next negative exponential pseudo random number.
		return kundenAnkunftsZeit.sample();
	}

	public double getPrioritaetsKundenAnkunftsZeit() {
		// sample(): Returns the next negative exponential pseudo random number.
		return prioritaetsKundenAnkunftsZeit.sample();
	}

	public int getRandomChosenQueue() {
		return (int) Math.round(chooseRandomQueue.sample());
	}

	private ContDistUniform bedienZeit;

	public double getBedienZeit() {
		// sample(): Returns the next negative exponential pseudo random number.
		return bedienZeit.sample();
	}

	protected List<Queue<KundeEntity>> kundenReiheQueue;

	protected List<Queue<SchalterEntity>> freieSchalterQueue;

	protected List<Queue<SchalterEntity>> besetzteSchalterQueue;

	public Schalter_Model(Model owner, String name, boolean showInReport, boolean showIntrace) {
		super(owner, name, showInReport, showIntrace);
	}

	public String description() {
		return "Schalter1_e Modell (Ereignis orientiert):"
				+ "simuliert einen Bankschalter, wo ankommende Kunden zuerst in einer "
				+ "Warteschlange eingereiht werden. Wenn der Schalter frei ist, " + "werden sie bedient.";
	}

	public void doInitialSchedules() {
		// erstes Kundenkreation-Ereignis erzeugen
		NeuerKundeEvent ersterKunde = new NeuerKundeEvent(this, "Kundenkreation", true);
		// erstes Kundenkreations-Ereignis in Ereignisliste eintragen
		ersterKunde.schedule(new TimeSpan(getKundenAnkunftsZeit()));

		// erstes PKundenkreation-Ereignis erzeugen
		NeuerPKundeEvent ersterPKunde = new NeuerPKundeEvent(this, "PKundenkreation", true);
		// erstes PKundenkreations-Ereignis in Ereignisliste eintragen
		ersterPKunde.schedule(new TimeSpan(getPrioritaetsKundenAnkunftsZeit()));
	}

	public void init() {
		long[][] seeds = { { 32631223, 308350985 }, { 481811168, 714137498 }, { 522744327, 960131716 },
				{ 655598835, 1172262326 }, { 398372211, 706471700 }, { 566847377, 364107117 },
				{ 400435359, 1487381098 }, { 755282992, 744526911 }, { 523268031, 765872899 }, { 263903991, 940928692 },
				{ 498560370, 974313881 }, { 1243097480, 646913845 }, { 729795274, 1198314105 },
				{ 806644965, 820215278 }, { 137664561, 706309210 }, { 774778690, 628180345 }, { 351632898, 1501071287 },
				{ 456908879, 1584047182 }, { 1236491406, 950508842 }, { 187432650, 764916242 },
				{ 562167668, 1418784708 }, { 366482647, 280102874 }, { 688371624, 816625263 }, { 541584468, 583280731 },
				{ 500906045, 871155063 }, { 323486099, 969177230 }, { 699274041, 510235710 }, { 443489363, 272204465 },
				{ 683599024, 813280615 }, { 305875062, 825263472 }, { 707531826, 337421994 }, { 874437008, 299932215 },
				{ 458112689, 701844041 }, { 582282017, 782457776 }, { 133301897, 844770671 }, { 736334688, 942023214 },
				{ 933842573, 906848466 }, { 491763196, 281233840 }, { 571954414, 332372385 }, { 1598660090, 530849318 },
				{ 541753914, 536032397 }, { 389219901, 226492659 }, { 614177043, 963284961 }, { 266776345, 393358117 },
				{ 824569639, 859793491 }, { 102208405, 795265237 }, { 813859297, 729450448 }, { 603351359, 554174228 },
				{ 506467888, 640775923 }, { 587145611, 561753555 } };
		
		kundenAnkunftsZeit = new ContDistExponential(this, "Ankunftszeitintervall", ANKUNFTSZEIT_DURCHSCHNITT, true,
				true);
		kundenAnkunftsZeit.setNonNegative(true);
		prioritaetsKundenAnkunftsZeit = new ContDistExponential(this, "Ankunftszeitintervall Priorität",
				PRIORITAET_ANKUNFTSZEIT_DURCHSCHNITT, true, true);
		prioritaetsKundenAnkunftsZeit.setNonNegative(true);
		chooseRandomQueue = new ContDistUniform(this, "RandomQueueWahl", 0, ANZAHL_SCHALTER - 1, true, true);

		kundenAnkunftsZeit.setSeed(seeds[indexSeed][0]);
		prioritaetsKundenAnkunftsZeit.setSeed(seeds[indexSeed][1]);

		bedienZeit = new ContDistUniform(this, "Bedienzeiten", BEDIENZEIT_UNTERGRENZE, BEDIENZEIT_OBERGRENZE, true,
				true);

		kundenReiheQueue = new ArrayList<Queue<KundeEntity>>();
		for (int i = 0; i < ANZAHL_SCHALTER; i++)
			kundenReiheQueue.add(new Queue<KundeEntity>(this, "Kunden-Warteschlange" + i, true, true));

		freieSchalterQueue = new ArrayList<Queue<SchalterEntity>>();
		for (int i = 0; i < ANZAHL_SCHALTER; i++)
			freieSchalterQueue.add(new Queue<SchalterEntity>(this, "freie Schalter WS" + i, true, true));

		verloreneKunden = new Count(this, "Verlorene Kunden", true, true);
		verlorenePrioritaetsKunden = new Count(this, "Verlorene Prioritätskunden", true, true);

		SchalterEntity schalter;
		for (int i = 0; i < ANZAHL_SCHALTER; i++) {
			schalter = new SchalterEntity(this, "Fahrkartenschalter" + i, true);
			System.out.println(i);
			freieSchalterQueue.get(i).insert(schalter);
		}

		besetzteSchalterQueue = new ArrayList<Queue<SchalterEntity>>();
		for (int i = 0; i < ANZAHL_SCHALTER; i++)
			besetzteSchalterQueue.add(new Queue<SchalterEntity>(this, "besetzte Schalter WS" + i, true, true));
	}

	public static void main(java.lang.String[] args) {
		indexSeed = 0;

		for (indexSeed = 0; indexSeed < 50; indexSeed++) {
			Experiment schalterExperiment = new Experiment("Variante2-ereignis" + indexSeed);
			Schalter_Model sch_e_model = new Schalter_Model(null, "Schalter Modell", true, true);

			sch_e_model.connectToExperiment(schalterExperiment);

			schalterExperiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
			schalterExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));
			schalterExperiment.stop(new TimeInstant(600));

			schalterExperiment.start();
			schalterExperiment.report();
			schalterExperiment.finish();
		}

	}
}

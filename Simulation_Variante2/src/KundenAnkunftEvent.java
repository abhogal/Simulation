import desmoj.core.simulator.*;

public class KundenAnkunftEvent extends Event<KundeEntity> {

	private final int MAXIMALE_LAENGE = 10;
	private Schalter_Model meinModel;

	public KundenAnkunftEvent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);

		meinModel = (Schalter_Model) owner;

	}

	public void eventRoutine(KundeEntity kunde) {
		boolean notAllMax = false;
		int index = -1;

		for (int i = 0; i < meinModel.kundenReiheQueue.size(); i++) {
			if (meinModel.kundenReiheQueue.get(i).length() < MAXIMALE_LAENGE)
				notAllMax = true;
			System.out.println("kundenreihelänge"+i+": "+meinModel.kundenReiheQueue.get(i).length());
		}
		System.out.println(notAllMax);
		if (notAllMax) {
			System.out.println("hier");
			if (meinModel.chooseRandom) {
				do {
					index = meinModel.getRandomChosenQueue();
					System.out.println("hierhier");

				} while (meinModel.kundenReiheQueue.get(index).length() >= MAXIMALE_LAENGE);
				
				meinModel.kundenReiheQueue.get(index).insert(kunde);
				sendTraceNote("Laenge der Kundenreihe: "
						+ meinModel.kundenReiheQueue.get(index).length());
			} else {
				// cast notwendig
				// KundeEntity kunde = who;
				index = -1;
				int minLength = Integer.MAX_VALUE;
				for (int i = 0; i < meinModel.ANZAHL_SCHALTER; i++) {
					// System.out.println(minLength + "-" +
					// meinModel.kundenReiheQueue.get(i).length());
					if (minLength > meinModel.kundenReiheQueue.get(i).length()) {
						index = i;
						minLength = meinModel.kundenReiheQueue.get(i).length();
					}
				}
				// Kunde in Warteschlange
				meinModel.kundenReiheQueue.get(index).insert(kunde);
				sendTraceNote("Laenge der Kundenreihe: "
						+ meinModel.kundenReiheQueue.get(index).length());
			}
			// Schalter frei?
			if (!meinModel.freieSchalterQueue.get(index).isEmpty()) {
				// Schalter frei, von entsprechender WS holen
				SchalterEntity schalter = meinModel.freieSchalterQueue.get(index)
						.first();
				// extra Entfernen von WS notwendig
				meinModel.freieSchalterQueue.get(index).remove(schalter);

				// Schalter in entsprechende WS um Referenz nicht zu verlieren
				meinModel.besetzteSchalterQueue.get(index).insert(schalter);

				// Kunden aus Kundenreihe um am Schalter bedient zu werden
				// -> Referenz auf Kunden bereits vorhanden - kein first() nï¿½tig!
				meinModel.kundenReiheQueue.get(index).remove(kunde);

				// Bedienungsende Ereignis erzeugen
				BedienEndeEvent bedienEnde = new BedienEndeEvent(meinModel,
						"Bedienung Ende", index, true);
				// eintragen in Ereignisliste
				bedienEnde.schedule(kunde, new TimeSpan(meinModel.getBedienZeit()));
			}
		} else {
			meinModel.verloreneKunden.update();
		}
		
	}
}

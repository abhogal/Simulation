import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Run {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String outputFile = "C:\\Users\\Amrit\\Documents\\GitHub\\Simulation\\Simulation_Variante1\\Var1_4Schalter.csv";
		int i = 0;
		String appendText = "";
		while (i < 50) {
			String filename = "C:\\Users\\Amrit\\Documents\\GitHub\\Simulation\\Simulation_Variante1\\Variante1-ereignis"
					+ i + "_report.html";
			BufferedReader br = new BufferedReader(new FileReader(filename));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				String everything = sb.toString();

				String[] verloreneKunden = everything.split("Verlorene Kunden");
				String[] help = verloreneKunden[1].split("</td><td>");
				String anzahlVerloreneKunden = help[3];

				String[] verlorenePrioritätskunden = everything
						.split("Verlorene Prioritätskunden");
				help = verlorenePrioritätskunden[1].split("</td><td>");
				String anzahlPVerloreneKunden = help[3];

				String[] kundenWS = everything.split("Kunden-Warteschlange");
				help = kundenWS[1].split("</td><td>");
				String bedienteKunden = help[3];
				String qMax = help[5];
				String qNow = help[6];
				String qAvg = help[7].replace(".", ",");
				String waitMax = help[9].replace(".", ",");
				String waitAvg = help[10].replace(".", ",");

				if (i == 0) {
					appendText += "Verlorene K;";
					appendText += "Verlorene PK;";
					appendText += "Bediente;";
					appendText += "Qmax;";
					appendText += "Qnow;";
					appendText += "Qavg;";
					appendText += "WaitMax;";
					appendText += "WaitAvg;";
					appendText += "\n";
				}

				appendText += anzahlVerloreneKunden + ";";
				appendText += anzahlPVerloreneKunden + ";";
				appendText += bedienteKunden + ";";
				appendText += qMax + ";";
				appendText += qNow + ";";
				appendText += qAvg + ";";
				appendText += waitMax + ";";
				appendText += waitAvg + ";";
				appendText += "\n";

			} finally {
				br.close();
			}
			i++;
		}

		FileWriter writer = new FileWriter(outputFile);
		writer.append(appendText);
		writer.flush();
		writer.close();

	}

}

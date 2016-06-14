import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Run {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String outputFile = "C:\\Users\\Amrit\\Documents\\GitHub\\Simulation\\Simulation_Variante2\\Var2_3SchalterT.csv";
		int i = 0;
		String appendText = "";
		while (i < 50) {
			String filename = "C:\\Users\\Amrit\\Documents\\GitHub\\Simulation\\Simulation_Variante2\\Variante2-ereignis"
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

				String[] kundenWS = everything.split("Kunden-Warteschlange0");
				help = kundenWS[1].split("</td><td>");
				String bedienteKunden = help[3];
				String qMax = help[5];
				String qNow = help[6];
				String qAvg = help[7].replace(".", ",");
				String waitMax = help[9].replace(".", ",");
				String waitAvg = help[10].replace(".", ",");
				
				String[] pKundenWS = everything.split("Kunden-Warteschlange1");
				help = pKundenWS[1].split("</td><td>");
				String pbedienteKunden = help[3];
				String pqMax = help[5];
				String pqNow = help[6];
				String pqAvg = help[7].replace(".", ",");
				String pwaitMax = help[9].replace(".", ",");
				String pwaitAvg = help[10].replace(".", ",");

				String[] zweiKundenWS = everything.split("Kunden-Warteschlange2");
				help = zweiKundenWS[1].split("</td><td>");
				String zweibedienteKunden = help[3];
				String zweiqMax = help[5];
				String zweiqNow = help[6];
				String zweiqAvg = help[7].replace(".", ",");
				String zweiwaitMax = help[9].replace(".", ",");
				String zweiwaitAvg = help[10].replace(".", ",");

				
				if (i == 0) {
					appendText += "Verlorene K;";
					appendText += "Verlorene PK;";
					appendText += "0Bediente;";
					appendText += "0Qmax;";
					appendText += "0Qnow;";
					appendText += "0Qavg;";
					appendText += "0WaitMax;";
					appendText += "0WaitAvg;";
					appendText += "1Bediente;";
					appendText += "1Qmax;";
					appendText += "1Qnow;";
					appendText += "1Qavg;";
					appendText += "1WaitMax;";
					appendText += "1WaitAvg;";
					appendText += "2Bediente;";
					appendText += "2Qmax;";
					appendText += "2Qnow;";
					appendText += "2Qavg;";
					appendText += "2WaitMax;";
					appendText += "2WaitAvg;";
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
				appendText += pbedienteKunden + ";";
				appendText += pqMax + ";";
				appendText += pqNow + ";";
				appendText += pqAvg + ";";
				appendText += pwaitMax + ";";
				appendText += pwaitAvg + ";";
				appendText += zweibedienteKunden + ";";
				appendText += zweiqMax + ";";
				appendText += zweiqNow + ";";
				appendText += zweiqAvg + ";";
				appendText += zweiwaitMax + ";";
				appendText += zweiwaitAvg + ";";
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

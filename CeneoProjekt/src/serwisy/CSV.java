package serwisy;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import obiekty.Komentarz;

/**
 *	Klasa zapisująca dane do CSV. 
 */
public class CSV {

	/**
	 * Zapis do pliku .csv
	 * @param komentarze Zapisywane komentarze
	 * @throws FileNotFoundException Nie znaleziono pliku.
	 */
	public static void zapiszCSV(List<Komentarz> komentarze) throws FileNotFoundException {
		String sciezka = System.getProperty("user.dir");
		PrintWriter zapis = new PrintWriter(sciezka + "\\csv.txt");

		for (Komentarz komentarz : komentarze) {
			String zalety = String.join(", ", komentarz.getZalety());
			String wady = String.join(", ", komentarz.getWady());
			zapis.println(komentarz.getId() + ", " + komentarz.getAutor() + ", " + komentarz.getLiczbaGwiazdek() + ", "
					+ komentarz.getLiczbaPozytywnychOpinii() + ", " + komentarz.getLiczbaOpinii() + ", "
					+ komentarz.getData() + ", " + (komentarz.getPolecany() ? "tak" : "nie") + ", " + wady + ", "
					+ zalety + ", " + komentarz.getPodsumowanie());
		}
		zapis.close();
	}
}

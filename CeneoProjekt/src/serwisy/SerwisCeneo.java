package serwisy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.ProgressBar;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Serwis dla Ceneo.
 */
public class SerwisCeneo implements Serializable {

	/**
	 * Id produktu.
	 */
	private String idProduktu = "";

	/**
	 * Pobrana strona zawierająca produkt.
	 */
	private Document stronaZProduktem;

	/**
	 * Pobrane strony zawierające komentarze.
	 */
	private List<Document> stronyZKomentarzami;

	/**
	 * Pasek postępu.
	 */
	private ProgressBar progressBar;

	/**
	 * Pobranie strony z produktem.
	 * 
	 * @param idProduktu
	 *            Id produktu.
	 * @throws HttpStatusException
	 *             Błąd http - brak produktu na ceneo.
	 * @throws IOException
	 *             Inny błąd.
	 */
	public void pobierzStroneProduktu(Integer idProduktu) throws HttpStatusException, IOException {
		setStronaZProduktem(Jsoup.connect("http://www.ceneo.pl/" + idProduktu + "#tab=spec").get());
	}

	/**
	 * Pobranie stron z komentarzami.
	 * 
	 * @param idProduktu
	 *            Id produktu.
	 * @throws HttpStatusException
	 *             Błąd http - brak produktu na ceneo.
	 * @throws IOException Inny błąd.
	 */
	public void pobierzStronyZKomentarzami(Integer idProduktu) throws HttpStatusException, IOException {
		Document strona = null;
		strona = Jsoup.connect("http://www.ceneo.pl/" + idProduktu + "#tab=reviews_scroll")
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0").get();
		stronyZKomentarzami = new ArrayList<>();
		getStronyZKomentarzami().add(strona);
		Integer liczbaWszystkichKomentarzy = 0;
		Element elementSpanZLiczaKomentarzy = strona.select("span[itemprop=reviewCount]").first();
		liczbaWszystkichKomentarzy = Integer.parseInt(elementSpanZLiczaKomentarzy.text());
		Integer liczbaStronZKomentarzami = (int) Math.ceil(liczbaWszystkichKomentarzy / 10.0);
		progressBar.setSelection(1 * 100 / liczbaStronZKomentarzami);

		for (int i = 2; i <= liczbaStronZKomentarzami; i++) {
			strona = Jsoup.connect("http://www.ceneo.pl/" + idProduktu + "/opinie-" + i).get();
			getStronyZKomentarzami().add(strona);
			progressBar.setSelection(i * 100 / liczbaStronZKomentarzami);
		}
	}

	public Document getStronaZProduktem() {
		return stronaZProduktem;
	}

	public void setStronaZProduktem(Document stronaZProduktem) {
		this.stronaZProduktem = stronaZProduktem;
	}

	public List<Document> getStronyZKomentarzami() {
		return stronyZKomentarzami;
	}

	public void setStronyZKomentarzami(List<Document> stronyZKomentarzami) {
		this.stronyZKomentarzami = stronyZKomentarzami;
	}

	public String getIdProduktu() {
		return idProduktu;
	}

	public void setIdProduktu(String idProduktu) {
		this.idProduktu = idProduktu;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
}

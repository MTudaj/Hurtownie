package serwisy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.ProgressBar;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import obiekty.Komentarz;
import obiekty.Produkt;

/**
 * Klasa wydobywająca komentarze ze stron HTML.
 */
public class Parser {

	/**
	 * Pasek postępu.
	 */
	private ProgressBar progressBar;

	/**
	 * Wczytaj produkt za strony.
	 * 
	 * @param stronaProduktu
	 *            Strona z produktem.
	 * @param idProduktu
	 *            Id produktu.
	 * @return Produkt
	 */
	public Produkt parsujProduktZCeneo(Document stronaProduktu, Integer idProduktu) {
		Produkt produkt = null;
		Element nazwaProduktuElement = stronaProduktu.getElementsByClass("product-name").first();
		String productFullName = nazwaProduktuElement.text();
		Element kategoriaElements = stronaProduktu.getElementsByClass("breadcrumb").last()
				.select("span[itemprop=title]").first();
		String nazwaKategorii = kategoriaElements.text();
		produkt = new Produkt();
		produkt.setId(idProduktu);
		produkt.setMarka(productFullName.substring(0, productFullName.indexOf(' ')));
		produkt.setModel(productFullName.substring(productFullName.indexOf(' '), productFullName.length()));
		produkt.setRodzaj(nazwaKategorii);
		return produkt;
	}

	/**
	 * Wczytaj komentarze za strony Ceneo.
	 * 
	 * @param stronyKomentarzy
	 *            Strony z komentarzami.
	 * @return Komentarze.
	 * @throws ParseException
	 *             Błąd przetwarzania komentarzy.
	 */
	public List<Komentarz> parsujKomentarzeZCeneo(List<Document> stronyKomentarzy) throws ParseException {
		List<Komentarz> komentarze = new ArrayList<>();
		for (Document doc : stronyKomentarzy) {
			Elements komentarzElements = doc.getElementsByClass("product-review");
			for (Element komentarzElement : komentarzElements) {
				Komentarz komentarz = new Komentarz();
				String id = komentarzElement.getElementsByClass("js_product-review-comments").attr("id");
				Integer idKomentarza = Integer.parseInt(id.substring(id.lastIndexOf("-") + 1, id.length()));
				komentarz.setId(idKomentarza);
				komentarz.setAutor(komentarzElement.getElementsByClass("product-reviewer").text());
				komentarz.setPodsumowanie(komentarzElement.getElementsByClass("product-review-body").text());
				SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				komentarz.setData(
						parserSDF.parse(komentarzElement.select(".review-time time").first().attr("datetime")));
				komentarz.setPolecany(
						komentarzElement.getElementsByClass("product-recommended").text().equals("Polecam"));
				String[] gwiazdki = komentarzElement.getElementsByClass("review-score-count").text().split("/");
				komentarz.setLiczbaGwiazdek(Double.parseDouble(gwiazdki[0].replace(",", ".")));
				komentarz.setLiczbaOpinii(
						Integer.parseInt(komentarzElement.getElementById("votes-" + idKomentarza).text()));
				komentarz.setLiczbaPozytywnychOpinii(
						Integer.parseInt(komentarzElement.getElementById("votes-yes-" + idKomentarza).text()));

				List<String> zalety = new ArrayList<>();
				Elements zaletyElements = komentarzElement.select(".pros-cell li");
				for (Element zaletyElement : zaletyElements) {
					zalety.add(zaletyElement.text());
				}
				komentarz.setZalety(zalety);

				List<String> wady = new ArrayList<>();
				Elements wadyElements = komentarzElement.select(".cons-cell li");
				for (Element wadaElement : wadyElements) {
					wady.add(wadaElement.text());
				}
				komentarz.setWady(wady);
				komentarze.add(komentarz);
				progressBar.setSelection(stronyKomentarzy.indexOf(doc) * 100 / stronyKomentarzy.size());
			}
		}
		return komentarze;
	}

	/**
	 * Wczytaj komentarze za strony Morele.
	 * 
	 * @param stronyKomentarzy
	 *            Strony z komentarzami.
	 * @return Komentarze.
	 * @throws ParseException
	 *             Błąd przetwarzania komentarzy.
	 */
	public static List<Komentarz> parsujKomentarzeZMorele(List<Document> stronyKomentarzy) throws ParseException {
		List<Komentarz> komentarze = new ArrayList<>();
		if (!stronyKomentarzy.isEmpty()) {
			Elements lewaCzesc = stronyKomentarzy.get(0).select("ul.left-comment.review");
			Elements prawaCzesc = stronyKomentarzy.get(0).select("ul.right-comment.review");

			for (int i = 0; i < lewaCzesc.size(); i++) {
				Komentarz komentarz = new Komentarz();
				Element obecnaLewa = lewaCzesc.get(i);
				{
					String id = obecnaLewa.select("li.comment-header > h2").attr("id");
					Integer idKomentarza = Integer.parseInt(id.substring(id.lastIndexOf("-") + 1, id.length()) + "009");
					komentarz.setId(idKomentarza);
					komentarz.setPodsumowanie(obecnaLewa.getElementsByClass("rcomment").text());
					komentarz.setZalety(Arrays.asList(obecnaLewa.select("li.good > p").text()));
					komentarz.setWady(Arrays.asList(obecnaLewa.select("li.bad > p").text()));
				}
				Element obecnaPrawa = prawaCzesc.get(i);
				{
					komentarz.setAutor(obecnaPrawa.select("li.author > strong").text());
					SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					komentarz.setData(parserSDF.parse(obecnaPrawa.getElementsByClass("date").text()));
					String gwiazdki = obecnaPrawa.select("li.rate > img").attr("src");
					gwiazdki = gwiazdki.substring(gwiazdki.lastIndexOf("_") + 1, gwiazdki.length());
					gwiazdki = gwiazdki.substring(0, gwiazdki.indexOf('.'));
					Integer liczbaGwiazdek = Integer.parseInt(gwiazdki);
					komentarz.setLiczbaGwiazdek(liczbaGwiazdek / 2.0);
					komentarz.setLiczbaOpinii(
							Integer.parseInt(obecnaPrawa.select("li.rate-count > strong:first-child").text()));
					Double pozytywneOpinie = Double
							.parseDouble(obecnaPrawa.select("li.rate-count > strong:last-child").text()) / 100
							* komentarz.getLiczbaOpinii();
					komentarz.setLiczbaPozytywnychOpinii(pozytywneOpinie.intValue());
				}
				komentarze.add(komentarz);
			}
		}
		return komentarze;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
}

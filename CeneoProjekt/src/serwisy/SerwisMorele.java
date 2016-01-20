package serwisy;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Serwis dla Morele.
 */
public class SerwisMorele {

	/**
	 * Pobrana strona zawierająca produkt.
	 */
	private Document stronaZProduktem;

	/**
	 * Pobrane strony zawierające komentarze.
	 */
	private List<Document> stronyZKomentarzami;

	/**
	 * Główny link.
	 */
	private static String glownyLink = "http://www.ceneo.pl/";

	/**
	 * Link do komentarzy
	 */
	private static String fragmentZLinkiemDoKomentarzy = "#tab=click";

	/**
	 * Pobranie stron z komentarzami.
	 * @param idProduktu Id produktu.
	 */
	public void pobierzStronyZKomentarzami(Integer idProduktu) {
		stronyZKomentarzami = new ArrayList<>();

		Document doc = null;

		try {
			WebDriver driver = new FirefoxDriver();
			driver.get(glownyLink + idProduktu + fragmentZLinkiemDoKomentarzy);

			List<WebElement> pokazProdukty = driver.findElements(By.className("remaining-offers-trigger"));
			if (!pokazProdukty.isEmpty()) {
				pokazProdukty.get(0).click();
			}

			WebElement linkDoMorele = driver.findElement(By.xpath("//img[contains(@alt,'morele.net')]/parent::a"));
			driver.navigate().to(linkDoMorele.getAttribute("href"));
			WebElement zakladkaZKomentarzami = driver.findElement(By.id("li4"));
			zakladkaZKomentarzami.click();
			List<WebElement> przyciskPokazWszystkieKomentarze = driver.findElements(By.id("wwrozt"));
			if (!przyciskPokazWszystkieKomentarze.isEmpty()) {
				przyciskPokazWszystkieKomentarze.get(0).click();
			}
			String strona = driver.getPageSource();
			driver.quit();

			doc = Jsoup.parse(strona);
			stronyZKomentarzami.add(doc);

		} catch (WebDriverException e) {
			e.printStackTrace();
			System.out.println("Brak produktu");
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
}
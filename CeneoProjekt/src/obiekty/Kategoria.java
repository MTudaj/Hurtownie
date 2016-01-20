package obiekty;

import java.util.List;

import java.util.ArrayList;

/**
 * Klasa opisuje obiekt Kategoria potrzeby do sortowania produktów przy
 * wyświetlaniu.
 */
public class Kategoria {

	/**
	 * Nazwa kategorii.
	 */
	private String kategoria = "";

	/**
	 * Lista produktów należących do kategorii.
	 */
	private List<Produkt> produkty = new ArrayList<>();

	public String getKategoria() {
		return kategoria;
	}

	public void setKategoria(String kategoria) {
		this.kategoria = kategoria;
	}

	public List<Produkt> getProdukty() {
		return produkty;
	}

	public void setProdukty(List<Produkt> produkty) {
		this.produkty = produkty;
	}

}

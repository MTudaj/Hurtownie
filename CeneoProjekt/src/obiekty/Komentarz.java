package obiekty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Klasa opisuje encje Komentarz.
 */
public class Komentarz {

	/**
	 * Id komentarza.
	 */
	private Integer id;

	/**
	 * Treść komentarza.
	 */
	private String podsumowanie;

	/**
	 * Liczba gwiazdek przyznana komentarzowi.
	 */
	private Double liczbaGwiazdek;

	/**
	 * Autor komentarza.
	 */
	private String autor;

	/**
	 * Data wystawienia komentarza.
	 */
	private Date data;

	/**
	 * Zalety produktu.
	 */
	private List<String> zalety;

	/**
	 * Wady produktu.
	 */
	private List<String> wady;

	/**
	 * Czy produkt jest polecany przez autora komentarza.
	 */
	private Boolean polecany;

	/**
	 * Liczba pozytywnych ocen komentarza.
	 */
	private Integer liczbaPozytywnychOpinii;

	/**
	 * Liczba ocen dla komentarza.
	 */
	private Integer liczbaOpinii;

	/**
	 * Tworzenie komentarza.
	 */
	public Komentarz() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<String> getZalety() {
		return zalety;
	}

	public void setZalety(List<String> zalety) {
		this.zalety = zalety;
	}

	public List<String> getWady() {
		return wady;
	}

	public void setWady(List<String> wady) {
		this.wady = wady;
	}

	public String getPodsumowanie() {
		return podsumowanie;
	}

	public void setPodsumowanie(String podsumowanie) {
		this.podsumowanie = podsumowanie;
	}

	public Double getLiczbaGwiazdek() {
		return liczbaGwiazdek;
	}

	public void setLiczbaGwiazdek(Double liczbaGwiazdek) {
		this.liczbaGwiazdek = liczbaGwiazdek;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Boolean getPolecany() {
		return polecany;
	}

	public void setPolecany(Boolean polecany) {
		this.polecany = polecany;
	}

	public Integer getLiczbaPozytywnychOpinii() {
		return liczbaPozytywnychOpinii;
	}

	public void setLiczbaPozytywnychOpinii(Integer liczbaPozytywnychOpinii) {
		this.liczbaPozytywnychOpinii = liczbaPozytywnychOpinii;
	}

	public Integer getLiczbaOpinii() {
		return liczbaOpinii;
	}

	public void setLiczbaOpinii(Integer liczbaOpinii) {
		this.liczbaOpinii = liczbaOpinii;
	}

	/**
	 * Porównywanie komentarzy
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Komentarz) {
			Komentarz komentarzObj = (Komentarz) obj;
			if (id.equals(komentarzObj.getId())) {
				return true;
			}
			return false;
		}
		return false;
	}
}

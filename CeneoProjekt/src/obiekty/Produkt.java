package obiekty;

/**
 * Klasa opisuje encje Produkt.
 */
public class Produkt {

	/**
	 * Id produktu.
	 */
	private Integer id;

	/**
	 * Rodzaj produktu.
	 */
	private String rodzaj;

	/**
	 * Marka produktu.
	 */
	private String marka;

	/**
	 * Model produktu.
	 */
	private String model;

	/**
	 * Dodatkowe uwagi do produktu.
	 */
	private String dodatkoweUwagi;

	/**
	 * Kategoria do ktrórej należy produkt.
	 */
	private Kategoria parent;

	/**
	 * Tworzenie produktu.
	 */
	public Produkt() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRodzaj() {
		return rodzaj;
	}

	public void setRodzaj(String rodzaj) {
		this.rodzaj = rodzaj;
	}

	public String getMarka() {
		return marka;
	}

	public void setMarka(String marka) {
		this.marka = marka;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDodatkoweUwagi() {
		return dodatkoweUwagi;
	}

	public void setDodatkoweUwagi(String dodatkoweUwagi) {
		this.dodatkoweUwagi = dodatkoweUwagi;
	}

	/**
	 * Porównywanie produktów.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Produkt) {
			Produkt produktObj = (Produkt) obj;

			if (id.equals(produktObj.getId())) {
				return true;
			}
			return false;
		}
		return false;
	}

	public Kategoria getParent() {
		return parent;
	}

	public void setParent(Kategoria parent) {
		this.parent = parent;
	}
}

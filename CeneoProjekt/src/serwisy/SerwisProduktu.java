package serwisy;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import baza.MySqlConnection;
import obiekty.Produkt;

/**
 * Serwis dla produktów - służy do zapisu i odczytu produktów z bazy danych.
 */
public class SerwisProduktu implements Serializable {

	/**
	 * Zapis produktu do bazy.
	 * 
	 * @param produkt
	 *            Zapisywany produkt.
	 */
	public static void zapiszProdukt(Produkt produkt) {
		Connection con = null;
		try {
			String insertSql = "INSERT INTO `produkt` (`id`, `dodatkoweUwagi`, `marka`, `model`, `rodzaj`)"
					+ " VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " + "dodatkoweUwagi = VALUES(dodatkoweUwagi),"
					+ "marka = VALUES(marka)," + "model = VALUES(model)," + "rodzaj = VALUES(rodzaj);";

			con = MySqlConnection.createConnection();

			PreparedStatement preparedStatement = con.prepareStatement(insertSql);
			preparedStatement.setInt(1, produkt.getId());
			preparedStatement.setString(2, produkt.getDodatkoweUwagi());
			preparedStatement.setString(3, produkt.getMarka());
			preparedStatement.setString(4, produkt.getModel());
			preparedStatement.setString(5, produkt.getRodzaj());

			preparedStatement.executeUpdate();

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pobieranie produkt z bazy.
	 * 
	 * @param idProduktu
	 *            Id prduktu.
	 * @return Produkt.
	 */
	public static Produkt pobierzProduktZBazy(Integer idProduktu) {
		Produkt produkt = null;
		Connection con = null;
		try {
			con = MySqlConnection.createConnection();

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM produkt WHERE id=" + idProduktu);

			while (rs.next()) {
				produkt = new Produkt();
				Integer id = rs.getInt("id");
				String marka = rs.getString("marka");
				String model = rs.getString("model");
				String rodzaj = rs.getString("rodzaj");
				String dodatkoweUwagi = rs.getString("dodatkoweUwagi");
				produkt.setId(id);
				produkt.setMarka(marka);
				produkt.setModel(model);
				produkt.setRodzaj(rodzaj);
				produkt.setDodatkoweUwagi(dodatkoweUwagi);
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return produkt;
	}

	/**
	 * Pobieranie wszystkich produktów z bazy.
	 * 
	 * @return Produkty
	 */
	public static List<Produkt> pobierzWszystkieProduktyZBazy() {
		List<Produkt> produkty = new ArrayList<>();
		Connection con = null;
		try {
			con = MySqlConnection.createConnection();

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM produkt");

			while (rs.next()) {
				Produkt produkt = new Produkt();
				Integer id = rs.getInt("id");
				String marka = rs.getString("marka");
				String model = rs.getString("model");
				String rodzaj = rs.getString("rodzaj");
				String dodatkoweUwagi = rs.getString("dodatkoweUwagi");
				produkt.setId(id);
				produkt.setMarka(marka);
				produkt.setModel(model);
				produkt.setRodzaj(rodzaj);
				produkt.setDodatkoweUwagi(dodatkoweUwagi);

				produkty.add(produkt);
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return produkty;
	}

	/**
	 * Usuwanie wszystkich produktów z bazy.
	 */
	public static void usunWszystkieProduktyZBazy() {
		Connection con = null;
		try {
			con = MySqlConnection.createConnection();

			Statement stmt = con.createStatement();
			int rs = stmt.executeUpdate("DELETE FROM produkt");

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

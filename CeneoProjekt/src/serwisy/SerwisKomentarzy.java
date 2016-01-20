package serwisy;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import baza.MySqlConnection;
import obiekty.Komentarz;
import obiekty.Produkt;

/**
 * Serwis dla komentarzy - służy do zapisu i odczytu komentarzy z bazy danych.
 */
public class SerwisKomentarzy {

	/**
	 * Zapis komentarza do bazy.
	 * 
	 * @param produkt
	 *            Produkt zapierający zapisywane komentarze.
	 * @param komentarze
	 *            Zapisywane komentarze.
	 */
	public static void zapiszKomentarze(Produkt produkt, List<Komentarz> komentarze) {
		Connection con = null;
		try {
			String insertSql = "INSERT INTO `komentarz` (`id`, `Produkt_id`, `autor`, `data`,"
					+ " `liczbaGwiazdek`, `liczbaOpinii`, `liczbaPozytywnychOpinii`, `podsumowanie`, `polecany`) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

			con = MySqlConnection.createConnection();

			for (Komentarz komentarz : komentarze) {
				PreparedStatement preparedStatement = con.prepareStatement(insertSql);
				preparedStatement.setInt(1, komentarz.getId());
				preparedStatement.setInt(2, produkt.getId());
				preparedStatement.setString(3, komentarz.getAutor());
				preparedStatement.setDate(4, new java.sql.Date(komentarz.getData().getTime()));
				preparedStatement.setDouble(5, komentarz.getLiczbaGwiazdek());
				preparedStatement.setDouble(6, komentarz.getLiczbaOpinii());
				preparedStatement.setDouble(7, komentarz.getLiczbaPozytywnychOpinii());
				preparedStatement.setString(8, komentarz.getPodsumowanie());
				if (komentarz.getPolecany() != null) {
					preparedStatement.setBoolean(9, komentarz.getPolecany());
				} else {
					preparedStatement.setNull(9, java.sql.Types.BOOLEAN);
				}

				preparedStatement.executeUpdate();

				zapiszZalety(con, komentarz);
				zapiszWady(con, komentarz);
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Zapis wad dla komentarza
	 * 
	 * @param con
	 *            Połączenie z bazą danych.
	 * @param komentarz
	 *            Zapisywany komentarz.
	 */
	private static void zapiszWady(Connection con, Komentarz komentarz) {
		try {
			String insertSql = "INSERT INTO `komentarz_wady` (`Komentarz_id`, `wady`) VALUES (?, ?);";

			for (String wada : komentarz.getWady()) {
				PreparedStatement preparedStatement = con.prepareStatement(insertSql);
				preparedStatement.setInt(1, komentarz.getId());
				preparedStatement.setString(2, wada);

				preparedStatement.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Zapis zalet dla komentarza
	 * 
	 * @param con
	 *            Połączenie z bazą danych.
	 * @param komentarz
	 *            Zapisywany komentarz.
	 */
	private static void zapiszZalety(Connection con, Komentarz komentarz) {
		try {
			String insertSql = "INSERT INTO `komentarz_zalety` (`Komentarz_id`, `zalety`) VALUES (?, ?);";

			for (String zaleta : komentarz.getZalety()) {
				PreparedStatement preparedStatement = con.prepareStatement(insertSql);
				preparedStatement.setInt(1, komentarz.getId());
				preparedStatement.setString(2, zaleta);
				preparedStatement.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pobieranie komentarzy z bazy dla produktu o podanym id.
	 * 
	 * @param idProduktu
	 *            Id produktu.
	 * @return Komentarze
	 */
	public static List<Komentarz> pobierzKomentarzeZBazy(Integer idProduktu) {
		List<Komentarz> komentarze = new ArrayList<>();
		Connection con = null;
		try {
			String selectSql = "SELECT * FROM `komentarz` WHERE `Produkt_id`=" + idProduktu;
			con = MySqlConnection.createConnection();

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectSql);

			while (rs.next()) {
				Komentarz komentarz = new Komentarz();
				Integer id = rs.getInt("id");
				String autor = rs.getString("autor");
				String podsumowanie = rs.getString("podsumowanie");
				Double liczbaGwiazdek = rs.getDouble("liczbaGwiazdek");
				Integer liczbaOpinii = rs.getInt("liczbaOpinii");
				Integer liczbaPozytywnychOpinii = rs.getInt("liczbaPozytywnychOpinii");
				Boolean polecany = rs.getBoolean("polecany");
				Date data = rs.getDate("data");

				komentarz.setId(id);
				komentarz.setAutor(autor);
				komentarz.setData(data);
				komentarz.setPodsumowanie(podsumowanie);
				komentarz.setPolecany(polecany);
				komentarz.setLiczbaGwiazdek(liczbaGwiazdek);
				komentarz.setLiczbaOpinii(liczbaOpinii);
				komentarz.setLiczbaPozytywnychOpinii(liczbaPozytywnychOpinii);
				komentarz.setZalety(pobierzZalety(con, id));
				komentarz.setWady(pobierzWady(con, id));

				komentarze.add(komentarz);
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return komentarze;
	}

	/**
	 * Pobieranie wad dla podanego komentarza.
	 * 
	 * @param con
	 *            Połączenie z bazą danych.
	 * @param idKomentarza
	 *            Id komentarza
	 * @return Lista wad.
	 */
	private static List<String> pobierzWady(Connection con, Integer idKomentarza) {
		List<String> wady = new ArrayList<>();
		try {
			String selectSql = "SELECT * FROM `komentarz_wady` WHERE `Komentarz_id`=" + idKomentarza;

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectSql);

			while (rs.next()) {
				String wada = rs.getString("wady");
				wady.add(wada);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return wady;
	}

	/**
	 * Pobieranie zalet dla podanego komentarza.
	 * 
	 * @param con
	 *            Połączenie z bazą danych.
	 * @param idKomentarza
	 *            Id komentarza.
	 * @return Lista zalet.
	 */
	private static List<String> pobierzZalety(Connection con, Integer idKomentarza) {
		List<String> zalety = new ArrayList<>();
		try {
			String selectSql = "SELECT * FROM `komentarz_zalety` WHERE `Komentarz_id`=" + idKomentarza;

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectSql);

			while (rs.next()) {
				String zaleta = rs.getString("zalety");
				zalety.add(zaleta);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return zalety;
	}

	/**
	 * Usuwanie wszystkich komentarzy z bazy.
	 */
	public static void usunWszystkieKomentarzeZBazy() {
		Connection con = null;
		try {
			con = MySqlConnection.createConnection();

			Statement stmt = con.createStatement();
			int rs = stmt.executeUpdate("DELETE FROM `komentarz_wady`");
			rs = stmt.executeUpdate("DELETE FROM `komentarz_zalety`");
			rs = stmt.executeUpdate("DELETE FROM `komentarz`");

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

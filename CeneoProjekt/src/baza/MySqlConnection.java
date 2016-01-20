package baza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Klasa tworzy połączenie z bazą danych.
 */
public class MySqlConnection {
	
	/**
	 * Tworzy połączenie.
	 * @return Stworzone połączenie z bazą.
	 * @throws SQLException Błąd SQL.
	 * @throws InstantiationException Błąd inicjalizacji.
	 * @throws IllegalAccessException Błąd dostępu.
	 * @throws ClassNotFoundException Nie znaleziono klasy.
	 */
	public static Connection createConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ceneo?useUnicode=true&characterEncoding=utf-8",
                "ceneo",
                "ceneo");
		return con;
	}
}

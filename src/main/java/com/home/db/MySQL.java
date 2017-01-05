package com.home.db;

import com.home.pokemon.Pokemon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 05.01.2017.
 */
public class MySQL {
    private Statement stm;
    private String db_name;

    public MySQL(String db_name) {
        //Class.forName("com.mysql.jdbc.Driver");
        this.db_name = db_name;
        try {
            String url = "jdbc:mysql://localhost:3306?autoReconnect=true&useSSL=false";
            Connection conn = DriverManager.getConnection(url, "root", "toor");
            stm = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isExist() {
        ResultSet rs = null;
        try {
            rs = stm.executeQuery("SHOW DATABASES;");
            List<String> dbases = new ArrayList<String>();
            while (rs.next())
                dbases.add(rs.getString("Database"));
            if (dbases.contains(db_name.toLowerCase()))
                return true;
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    public void createDB(String tableName) {
        try {
            stm.executeUpdate("DROP DATABASE IF EXISTS " + db_name + ";");
            stm.executeUpdate("CREATE DATABASE " + db_name + ";");
            stm.executeUpdate(
                    "CREATE TABLE " + db_name + "." + tableName + " (" +
                        "id INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                        "name VARCHAR(30) NOT NULL," +
                        "attack INT UNSIGNED NOT NULL DEFAULT '0'," +
                        "defense INT UNSIGNED NOT NULL DEFAULT '0'," +
                        "stamina INT UNSIGNED NOT NULL DEFAULT '0'," +
                        "cp_gain DECIMAL(5,2) UNSIGNED NOT NULL DEFAULT '0.0'," +
                        "max_cp INT UNSIGNED NOT NULL DEFAULT '0'," +
                        "PRIMARY KEY(id)" +
                    ");"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addPokemon(Pokemon p, String tableName) {
        if (!this.isExist()) {
            System.out.println("DB is absent! Smth goes wrong...");
            return;
        }
        else try {
            stm.executeQuery("USE " + db_name + ";");
            stm.executeUpdate(
                    "INSERT INTO " + tableName + " (name, attack, defense, stamina, cp_gain, max_cp) " +
                    "VALUES (" +
                            "'" + p.name + "', " +
                            Integer.toString(p.attack) + ", " +
                            Integer.toString(p.defense) + ", " +
                            Integer.toString(p.stamina) + ", " +
                            Float.toString(p.cpGain) + ", " +
                            Integer.toString(p.maxCp) +
                    ");"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] Args) {
        MySQL db = new MySQL("new_db");
        db.createDB("tbl");
        Pokemon p = new Pokemon("http://www.pokemongodb.net/p/venusaur.html");
        db.addPokemon(p, "tbl");
        p = new Pokemon("http://www.pokemongodb.net/p/bulbasaur.html");
        db.addPokemon(p, "tbl");

        System.out.println(db.isExist());
    }
}
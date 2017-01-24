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
    private final String
        tableName = "poks",
        dbName = "pokegodb";
    private Statement stm;

    public MySQL() {
        //Class.forName("com.mysql.jdbc.Driver");
        try {
            String url = "jdbc:mysql://localhost:3306?autoReconnect=true&useSSL=false";
            Connection conn = DriverManager.getConnection(url, "root", "toor");
            stm = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ResultSet getPokemon(String pokName) {
        ResultSet foo = null;

        try {
            foo =  stm.executeQuery("SELECT * FROM " + dbName + "." + tableName + " WHERE name='" + pokName + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foo;
    }
    public boolean isExist() {
        ResultSet rs = null;
        try {
            rs = stm.executeQuery("SHOW DATABASES;");
            List<String> dbases = new ArrayList<String>();
            while (rs.next())
                dbases.add(rs.getString("Database"));
            if (dbases.contains(dbName.toLowerCase()))
                return true;
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    public void createDB() {
        try {
            stm.executeUpdate("DROP DATABASE IF EXISTS " + dbName + ";");
            stm.executeUpdate("CREATE DATABASE " + dbName + ";");
            stm.executeUpdate(
                    "CREATE TABLE " + dbName + "." + tableName + " (" +
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
    public void addPokemon(Pokemon p) {
        /*if (!this.isExist()) {
            System.out.println("DB is absent! Smth goes wrong...");
            return;
        }
        else */try {
            stm.executeQuery("USE " + dbName + ";");
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
    public static void main(String[] Args) { // class test
        MySQL db = new MySQL();
        //db.createDB();
        Pokemon p = new Pokemon("http://www.pokemongodb.net/p/venusaur.html");
        db.addPokemon(p);
        p = new Pokemon("http://www.pokemongodb.net/p/bulbasaur.html");
        db.addPokemon(p);

        System.out.println(db.isExist());
    }
}
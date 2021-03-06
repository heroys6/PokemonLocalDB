package com.home.db;

import com.home.pokemon.Pokemon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

import static com.home.db.Constants.*;

/**
 * Created by User on 05.01.2017.
 */
public class MySQL extends DB {

    public MySQL(String url, String uname, String pswd) {
        try {
            Connection conn = DriverManager.getConnection(url + dbUrlTail, uname, pswd);

            stm = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
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

    @Override
    public ResultSet getPokemon(String pokName) {
        ResultSet foo = null;

        try {
            foo =  stm.executeQuery("SELECT * FROM " + dbName + "." + tableName + " WHERE name='" + pokName + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foo;
    }

    @Override
    public void addPokemon(Pokemon p) {
        try {
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
}
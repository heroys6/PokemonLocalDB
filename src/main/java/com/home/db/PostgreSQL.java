package com.home.db;

import com.home.pokemon.Pokemon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.home.db.Constants.*;

/**
 * Created by User on 25.01.2017.
 */
public class PostgreSQL extends DB {

    private String uname, pswd, url;
    Connection conn;

    public PostgreSQL(String url, String uname, String pswd) {
        this.uname = uname;
        this.pswd = pswd;
        this.url = url;

        try {
            // Connect to common db
            conn = DriverManager.getConnection(url + dbName + dbUrlTail, uname, pswd);

            stm = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createDB() {
        try {
            // Close target db connection
            stm.close();
            conn.close();
            // Create common db connection
            conn = DriverManager.getConnection(url + dbUrlTail, uname, pswd);
            stm = conn.createStatement();
            // Trunk target db
            stm.executeUpdate("DROP DATABASE IF EXISTS " + dbName + ";");
            stm.executeUpdate("CREATE DATABASE " + dbName + ";");
            // Reconnect to target db
            stm.close();
            conn.close();
            conn = DriverManager.getConnection(url + dbName + dbUrlTail, uname, pswd);
            stm = conn.createStatement();
            stm.executeUpdate(
                    "CREATE TABLE " + tableName + " (" +
                            "id SERIAL," +
                            "name VARCHAR(30) NOT NULL," +
                            "attack INT NOT NULL DEFAULT 0," +
                            "defense INT NOT NULL DEFAULT 0," +
                            "stamina INT NOT NULL DEFAULT 0," +
                            "cp_gain DECIMAL(5,2) NOT NULL DEFAULT 0.0," +
                            "max_cp INT NOT NULL DEFAULT 0," +
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
            foo =  stm.executeQuery("SELECT * FROM " + tableName + " WHERE name='" + pokName + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foo;
    }

    @Override
    public void addPokemon(Pokemon p) {
        try {
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

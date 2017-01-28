package com.home.db;

import com.home.pokemon.Pokemon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.home.db.Constants.tableName;

/**
 * Created by User on 28.01.2017.
 */
public class HerokuPostgreSQL extends DB{
    public HerokuPostgreSQL(Connection conn) {
        try {
            stm = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createDB() {
        try {
            stm.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
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

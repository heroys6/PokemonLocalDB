package com.home.db;

import com.home.pokemon.Pokemon;

import java.sql.*;

/**
 * Created by User on 25.01.2017.
 */
public abstract class DB {
    protected Statement stm;

    public abstract void createDB();
    public abstract ResultSet getPokemon(String pokName);
    public abstract void addPokemon(Pokemon p);
}

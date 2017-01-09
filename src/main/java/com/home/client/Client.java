package com.home.client;

import com.home.db.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by User on 05.01.2017.
 */
public class Client {
    private static void compare() {
        Scanner keyboard = new Scanner(System.in);
        String input = "";
        List<ResultSet> Poks = new ArrayList<>();

/*
         Get pokemons names from user
*/
        while (!input.equals("no")) {
            System.out.print("Add another pokemon?(yes/no): ");
            input = keyboard.nextLine();
            if (input.equals("no"))
                continue;
            else if (input.equals("yes")) {
                System.out.print("Enter the pokemon name: ");
                String name = keyboard.nextLine();

                MySQL pokeStorage = new MySQL("pokemonstorage");

                ResultSet ans = pokeStorage.getPokemon(name, "pokemons");
                try {
                    if (ans.next()) {
                        //ans.previous();
                        Poks.add(ans);
                        System.out.println(name + " added to comparison");
                    }
                    else
                        System.out.println("No pokemons with the same name in db");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else
                System.out.println("Bad input. Please try again...");
        }
        if (Poks.size() < 2) {
            System.out.println("Needs at least 2 pokemons for comparison\nExiting...");
            return;
        }

/*
        Get info from db and write in table view
*/
        String[] colNames = {
                "name",
                "attack",
                "defense",
                "stamina",
                "max_cp",
                "cp_gain"
        };
        // Calculate max length of table cell
        int maxCellLen = 7; // "defense" length

        for (ResultSet rs : Poks)
            try {
                int temp = rs.getString("name").length();
                if (temp > maxCellLen) maxCellLen = temp;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        // Print table first row
        System.out.print("+");
        for (int i = 0; i < Poks.size() + 1; i++) {
            for (int j = 0; j < maxCellLen + 2; j++)
                System.out.print("-");
            System.out.print("+");
        }
        System.out.println();
        // Print main rows using colNames
        for (String colName : colNames) {
            System.out.printf("| %-" + Integer.toString(maxCellLen) + "s |", colName); // Left align
            for (int i = 0; i < Poks.size(); i++) {
                try {
                    if (colName.equals("cp_gain")) // Float precision setup
                        System.out.printf(" %" + Integer.toString(maxCellLen) + "s |",
                                String.format("%.1f", Float.parseFloat(Poks.get(i).getString(colName)))
                        );
                    else // Right align
                        System.out.printf(" %" + Integer.toString(maxCellLen) + "s |", Poks.get(i).getString(colName));
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println();
        }
        // Print last row(same as first)
        System.out.print("+");
        for (int i = 0; i < Poks.size() + 1; i++) {
            for (int j = 0; j < maxCellLen + 2; j++)
                System.out.print("-");
            System.out.print("+");
        }
        System.out.println();
    }
    private static void help() {
        System.out.println("List of available commands:");
        String[][] commands = {
                {"compare", "Compare a few pokemons"},
                {"help", "Show this page"},
                {"exit", "Terminate the program"}
        };

        int maxLen = 0;

        for (String[] s : commands)
            if (s[0].length() > maxLen) maxLen = s[0].length();

        for (String[] s : commands) {
            System.out.printf("%-" + Integer.toString(maxLen) + "s - %s\n", s[0], s[1]);
        }
    }
    public static void main(String[] Args) {
        System.out.println("PokemonLocalDB Client\nType 'help' for more info\n");

        Scanner keyboard = new Scanner(System.in);
        String input = "";

        while (!input.equals("exit")) {
            System.out.print(" >> ");
            input = keyboard.nextLine();
            switch (input) {
                case "compare":
                    compare();
                    break;
                case "help":
                    help();
                    break;
                case "exit":
                    break;
                default:
                    System.out.println("Unrecognized command. Please try again");
            }
        }
    }
}

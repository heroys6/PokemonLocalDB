package com.home.parser;

import com.home.db.DB;
import com.home.db.MySQL;
import com.home.pokemon.Pokemon;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import static java.lang.Math.pow;

/**
 * Created by User on 03.01.2017.
 */
public abstract class PokemonParser {

    public static void parseToDB(String parseFrom, DB pokemonStorage) {
        long timeStart = 0, timeEnd = 0;

        timeStart = System.nanoTime();

        Document doc = null; // main page with links on all of the pokemons

        try { // /05/pokemon-go-pokedex.html /12/pokedex-generation-ii.html
            doc = Jsoup.connect(parseFrom).get();
        } catch (IOException e) {
            System.out.println("Error in start connection opening");
            e.printStackTrace();
        }

        ArrayList<String> pokedex = new ArrayList<String>();

        Elements tr = doc.getElementsByTag("tr"); // Get table rows with links on pokemons

        Pattern p;

        for (Element e : tr) {
            Elements numbers = e.getElementsByAttribute("data-sheets-value"); // Elem 0 contains pokemon number

            if (numbers.size() < 1)
                continue;

            String number = numbers.get(0).attr("data-sheets-value"); // String that includes pokemon number

            p = Pattern.compile("^\\{\"1\":2,\"2\":\"#\\d{1,3}\"\\}"); // Example: {"1":2,"2":"#101"}
            if (!p.matcher(number).matches())
                continue;

            Elements images = e.getElementsByAttribute("data-sheets-formula");
            Elements links = images.get(0).getElementsByClass("in-cell-link");
            String link = links.get(0).attr("href");

            pokedex.add(link);
        }

        System.out.println(pokedex.size() + " pokemons found on site");
        for (int i = 0; i < pokedex.size(); i++) {
            Pokemon pok = new Pokemon(pokedex.get(i));
            pokemonStorage.addPokemon(pok);
            System.out.println(pok.name + " was added");
        }

        timeEnd = System.nanoTime();
        System.out.printf("\nLocal db created in %.3f sec\n", (timeEnd - timeStart) / pow(10, 9));
    }

    public static void main(String[] Args) { // test
        DB db = new MySQL("jdbc:mysql://localhost:3306",
                "root",
                "toor");
        /*DB db = new PostgreSQL("jdbc:postgresql://localhost:5432/",
                "postgres",
                "toor");*/

        db.createDB();
        PokemonParser.parseToDB("http://www.pokemongodb.net/2016/05/pokemon-go-pokedex.html", db);
    }
}
/*System.out.println("");*/
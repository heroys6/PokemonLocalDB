package com.home;

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
public class PokemonLocalDB {
    public static void main(String[] Args) {
        long timeStart = 0, timeEnd = 0;

        timeStart = System.nanoTime();

        Document doc = null; // main page with links on all of the pokemons

        try {
            doc = Jsoup.connect("http://www.pokemongodb.net/2016/05/pokemon-go-pokedex.html").get();
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

            p = Pattern.compile("^[{]\"1\":2,\"2\":\"#\\d{1,3}\"[}]"); // Example: {"1":2,"2":"#101"}
            if (!p.matcher(number).matches())
                continue;

            Elements images = e.getElementsByAttribute("data-sheets-formula");
            Elements links = images.get(0).getElementsByClass("in-cell-link");
            String link = links.get(0).attr("href");

            pokedex.add(link);
        }

        System.out.println(pokedex.size() + " pokemons in pokedex");
        for (int i = 0; i < 20; i++) {
            Pokemon pok = new Pokemon(pokedex.get(i));
            System.out.println(
                    pok.name + ": " +
                    pok.stamina + " " +
                    pok.attack + " " +
                    pok.defense + " " +
                    pok.cpGain + " " +
                    pok.maxCp
            );
        }

        timeEnd = System.nanoTime();
        System.out.println("\nSite parsed in " + Double.toString((timeEnd - timeStart) / pow(10, 9)) + " sec");
    }
}
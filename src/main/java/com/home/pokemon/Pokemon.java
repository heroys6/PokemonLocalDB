package com.home.pokemon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 04.01.2017.
 */
public class Pokemon {
    public String name;
    public int stamina;
    public int attack;
    public int defense;
    public float cpGain;
    public int maxCp;

    public Pokemon(String url) {
        Document page = null; // page with pokemon's stats

        try {
            page = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println("Error loading: " + url);
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("(\\/)([a-z-]+)(\\.html)"); // Find name from url
        Matcher m = p.matcher(url);

        if (!m.find()) {
            System.out.println("Failed to find the name");
            return;
        }

        name = m.group(2);

        Elements tr = page.getElementsByTag("tr"); // Get table rows with stats

        p = Pattern.compile("^[{]\"1\":2,\"2\":\"([A-Za-z ]+):?\"[}]"); // Example: {"1":2,"2":"Stamina:"};

        for (Element e : tr) {
            Elements data_sh_val = e.getElementsByAttribute("data-sheets-value");
            if (data_sh_val.size() < 1)
                continue;

            String hiddenKey = data_sh_val.get(0).attr("data-sheets-value"); // Here is stat name

            m = p.matcher(hiddenKey);
            if (!m.find())
                continue;

            String key = m.group(1);

            if (    key.equals("Stamina") || // Fucking switch
                    key.equals("Attack")  ||
                    key.equals("Defense") ||
                    key.equals("CP Gain") ||
                    key.equals("Max CP")
                    ) {
                Element blockWithStat = e.getElementsByAttribute("data-sheets-formula").get(0);

                if (key.equals("Stamina")) {
                    int val = Integer.parseInt(blockWithStat.html());
                    stamina = val;
                }
                else if (key.equals("Attack")) {
                    int val = Integer.parseInt(blockWithStat.html());
                    attack = val;
                }
                else if (key.equals("Defense")) {
                    int val = Integer.parseInt(blockWithStat.html());
                    defense = val;
                }
                else if (key.equals("CP Gain")) {
                    float val = Float.parseFloat(blockWithStat.html());
                    cpGain = val;
                }
                else if (key.equals("Max CP")) {
                    int val = Integer.parseInt(blockWithStat.html());
                    maxCp = val;
                }
            }
        }
    }
}

package com.home.server;

import com.heroku.sdk.jdbc.DatabaseUrl;
import com.home.db.DB;
import com.home.db.HerokuPostgreSQL;
import com.home.parser.PokemonParser;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.home.db.Constants.parseFromUrl;
import static com.home.server.Constants.*;

/**
 * Created by User on 27.01.2017.
 */
public class TelegramBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        // Receive message
        Message receivedMes = update.getMessage();
        Pattern correctInp = Pattern.compile("([A-Za-z-]+) (v|V)(s|S) ([A-Za-z-]+)");
        Matcher m = null;
        String answer = "";

        // Check is blank
        if (receivedMes.hasText()) {
            // Input validation
            m = correctInp.matcher(receivedMes.getText());
            if (!m.find()) {
                answer = "I don't understand you( Please use following format:" +
                        "\nPokemon_name vs Pokemon_name\nwhere \"Pokemon_name\" - any existing pokemon, case insensitive";
            }
            else {
                // Get pokemon names & create list of compared pokemons
                String pok1 = m.group(1), pok2 = m.group(4);
                List<ResultSet> Poks = new ArrayList<>();

                // Get connection with db
                DB db = null;
                try {
                    db = new HerokuPostgreSQL(DatabaseUrl.extract().getConnection());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Find pokemons in db
                ResultSet ans1 = db.getPokemon(pok1)/*,
                        ans2 = db.getPokemon(pok2)*/;

                try {
                    if (ans1.next())
                        Poks.add(ans1);
                    else
                        answer += "In db there are no pokemons named \'" + pok1 + "\'\n";
                    /*if (ans2.next())
                        Poks.add(ans2);
                    else
                        answer += "In db there are no pokemons named \'" + pok2 + "\'\n";*/
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (Poks.size() == 1/*2*/) {
                    // Create output table

                    // Calculate max length of table cell
                    int maxCellLen = 7; // "defense" length

                    for (ResultSet rs : Poks)
                        try {
                            int temp = rs.getString("name").length();
                            if (temp > maxCellLen) maxCellLen = temp;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    // Fill table first row
                    answer += "\n-";
                    for (int i = 0; i < Poks.size() + 1; i++) {
                        for (int j = 0; j < maxCellLen + 2; j++)
                            answer += "-";
                        answer += "-";
                    }
                    answer += "\n";

                    String[] colNames = {
                            "name",
                            "attack",
                            "defense",
                            "stamina",
                            "max_cp",
                            "cp_gain"
                    };

                    // Print main rows using colNames
                    for (String colName : colNames) {
                        answer += String.format("| %-" + Integer.toString(maxCellLen) + "s |", colName); // Left align
                        for (int i = 0; i < Poks.size(); i++) {
                            try {
                                if (colName.equals("name")) {
                                    char[] lowerName = Poks.get(i).getString(colName).toCharArray();

                                    lowerName[0] = Character.toUpperCase(lowerName[0]);

                                    String newName = new String(lowerName);

                                    answer += String.format(" %" + Integer.toString(maxCellLen) + "s |", newName);
                                }
                                else if (colName.equals("cp_gain")) // Float precision setup
                                    answer += String.format(" %" + Integer.toString(maxCellLen) + "s |",
                                            String.format("%.1f", Float.parseFloat(Poks.get(i).getString(colName)))
                                    );
                                else // Right align
                                    answer +=  String.format(" %" + Integer.toString(maxCellLen) + "s |", Poks.get(i).getString(colName));
                            }
                            catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        answer += "\n";
                    }
                    // Print last row(same as first)
                    answer +=  String.format("-");
                    for (int i = 0; i < Poks.size() + 1; i++) {
                        for (int j = 0; j < maxCellLen + 2; j++)
                            answer +=  String.format("-");
                        answer +=  String.format("+");
                    }
                    answer += "\n";
                }
            }
            SendMessage sendMes = new SendMessage();
            sendMes.setChatId(receivedMes.getChatId().toString());
            sendMes.setText(answer);
            try {
                sendMessage(sendMes);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public static void main(String[] Args) {
        // Create remote db
        /*try {
            DB db = new HerokuPostgreSQL(DatabaseUrl.extract().getConnection());
            db.createDB();
            PokemonParser.parseToDB(parseFromUrl, db);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/

        // Start bot
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new TelegramBot());
            System.out.println("Runned...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

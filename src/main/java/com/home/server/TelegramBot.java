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
        String answer = "";

        // Check is blank
        if (receivedMes.hasText()) {
            // Input validation
            String[] pok_names = receivedMes.getText().split(" [v|V][s|S] ");
            if (pok_names.length > 0) {
                // Work with each pokemon
                for(String s : pok_names) {
                    // Open connection with db
                    DB db = null;
                    try {
                        db = new HerokuPostgreSQL(DatabaseUrl.extract().getConnection());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // Try to find pokemon
                    ResultSet pokeInfo = db.getPokemon(s.toLowerCase());

                    try {
                        if (!pokeInfo.next()) {
                            // If failed
                            answer += "In db there are no pokemons named \'" + s + "\'\n";
                        }
                        else {
                            // Pokemon exists -> Format its info table

                            // Normalize name(starts from uppercase)
                            char[] lowerName = s.toCharArray();
                            String normalName = null;

                            lowerName[0] = Character.toUpperCase(lowerName[0]);
                            normalName = new String(lowerName);

                            answer += (normalName + "\n");

                            // Stats
                            String stats = String.format(
                                    "Att: %3s Def: %3s Stam: %3s MaxCP: %4s GainCP: %-2.1f",
                                    pokeInfo.getString("attack"),
                                    pokeInfo.getString("defense"),
                                    pokeInfo.getString("stamina"),
                                    pokeInfo.getString("max_cp"),
                                    Float.parseFloat(pokeInfo.getString("cp_gain"))
                            );
                            answer += (stats + "\n");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (answer.equals(""))
            answer = "I don't understand you( Please use following format:" +
                    "\nPokemon_name vs Pokemon_name\nwhere \"Pokemon_name\" - any existing pokemon, case insensitive";

        // Send answer
        SendMessage sendMes = new SendMessage();

        sendMes.setChatId(receivedMes.getChatId().toString());
        sendMes.setText(answer);
        try {
            sendMessage(sendMes);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
        // Create remote db | Was run first time
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
            System.out.println("Bot is ready for work :D");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
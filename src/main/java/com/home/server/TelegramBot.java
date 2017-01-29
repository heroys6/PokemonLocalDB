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

    private String withACapitalLetter(String str) {
        char[] sequence = str.toCharArray();

        sequence[0] = Character.toUpperCase(sequence[0]);

        return new String(sequence);
    }

    private void workWithEachPokemon(List<String> pokNames, Message receivedMes) {
        for(String s : pokNames) {
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
            ResultSet pokeInfo = db.getPokemon(s);

            try {
                if (!pokeInfo.next()) {
                    // If failed
                    String ans = "In database there are no pokemons named \'" + withACapitalLetter(s) + "\'\n";
                    sendAnswer(receivedMes, ans);
                }
                else {
                    // Pokemon exists -> format answer message

                    String answer = "";

                    answer += withACapitalLetter(s) + "\n";

                    // Stats
                    String stats = String.format(
                            "Att: %3s Def: %3s Stam: %3s MaxCP: %4s GainCP: %2.1f",
                            pokeInfo.getString("attack"),
                            pokeInfo.getString("defense"),
                            pokeInfo.getString("stamina"),
                            pokeInfo.getString("max_cp"),
                            Float.parseFloat(pokeInfo.getString("cp_gain"))
                    );
                    answer += stats;

                    // Answer is done - send it
                    sendAnswer(receivedMes, answer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAnswer(Message receivedMes, String answer){
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
    public void onUpdateReceived(Update update) {
        // Receive message
        Message receivedMes = update.getMessage();
        String recMesText = receivedMes.getText();
        String help = "Please use following format:\n" +
                "pokemon_1 vs pokemon_2 vs ... vs pokemon_n\n" +
                "Where pokemon_(1..n) - any existing pokemon\n" +
                "Pokemons names are case insensitive. E.g., charmander = Charmander etc.";

        // Handle commands
        if (recMesText.equals("/start")) {
            String text = "Nice to meet you here! Have some fun with this bot. Type /help for more info";
            sendAnswer(receivedMes, text);
            return;
        }
        else if (recMesText.equals("/help")) {
            sendAnswer(receivedMes, help);
            return;
        }
        // User request validation
        else if (Pattern.compile("[a-zA-Z-]+( [vV][sS] [a-zA-Z-]+)+").matcher(recMesText).matches()) {
            // Parse user request
            List<String> pokemonNames = new ArrayList<>();

            Matcher m = Pattern.compile("([a-zA-Z-]+)( [vV][sS] )").matcher(recMesText);

            while (m.find()) // find names like 'pokemon vs'
                pokemonNames.add(m.group(1).toLowerCase());

            m = Pattern.compile("( [vV][sS] )([a-zA-Z-]+$)").matcher(recMesText);

            m.find(); // find name like ' vs pokemon', it also tail of user request
            pokemonNames.add(m.group(2).toLowerCase());
            workWithEachPokemon(pokemonNames, receivedMes);
            return;
        }
        else {
            String errorStr = "Your syntax isn't correct. Please, see /help for more info";
            sendAnswer(receivedMes, errorStr);
            return;
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
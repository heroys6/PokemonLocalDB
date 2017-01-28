package com.home.server;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import static com.home.server.Constants.*;

/**
 * Created by User on 27.01.2017.
 */
public class TelegramBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        int i = Integer.valueOf(System.getenv("PORT"));
        if (message.hasText()) {
            SendMessage sendMes = new SendMessage();
            sendMes.setChatId(message.getChatId().toString());
            sendMes.setText("[test]|$echo: " + message.getText());
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

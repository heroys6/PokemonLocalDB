# PokemonLocalDB
It helps to compare pokemons from Pokemon Go with [telegram bot](https://t.me/PokemonVS_bot)(link to the working version) anywhere.
Also it provides comparing localy with no internet connection using [client](src/main/java/com/home/client/Client.java) for local db.

This project contains:

 - [Parser](src/main/java/com/home/parser/PokemonParser.java) for site http://www.pokemongodb.net/ that creates MySQL/PostgreSQL db with pokemons stats

 - [Client](src/main/java/com/home/client/Client.java) for local db(MySQL/PostgreSQL) that provides pokemons comparison

 - [Telegram bot](src/main/java/com/home/server/TelegramBot.java) that's ready for deployment. Works with remote PostgreSQL db

Features:

 - Used Maven for project management
 - Used [Heroku](heroku.com) for free java hosting
 - The ability to easily add support for new databases via inheriting of special abstract [class](src/main/java/com/home/db/DB.java)

How to get started:

 - Clone it project
 - Create [new telegram bot](https://telegram.me/BotFather)
 - Write your bot name and bot token to CLONED_PROJECT/src/main/java/com/home/server/Constants.java
 - When starting first time uncomment 155-164 strings in main class(CLONED_PROJECT/src/main/java/com/home/server/TelegramBot.java) for creating remote db
 - Follow the instructions on [heroku](https://devcenter.heroku.com/articles/getting-started-with-java) to deploy your bot


A few screenshots:

<p align="center"><img src="/screenshot.png"><img src="/bot_screenshot.png"></p>
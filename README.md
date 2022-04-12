# LanguageSupport

A spigot plugin to translate plugin messages into different languages. 
You can translate all plugin messages of plugins which allow editing the messages 
individually in as many languages as you want.

## Important!

- This plugin isn't using a translation API or something so you have to write each translation in every language manual!
- This plugin is still under development so it's possible that bugs may occur! Please report them in the issues section.
- This plugin edits the chat packets a player is receiving using [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)!

## How to use

### Setting up

1. You have to restart your server one time after you loaded the plugin the first time so less bugs can occur!
2. After that you will see that a few files were created: `players.yml`, `lsconfig.yml`, `de.yml`, `en.yml`.
In the `lsconfig.yml` file you have to set your chat format if your server software is spigot or less for example:

![chat](https://user-images.githubusercontent.com/69450649/162993473-20756dbf-6c0c-4e3c-98bc-fefb39b76edb.png)

![config](https://user-images.githubusercontent.com/69450649/162993609-1f4497af-fe15-4093-ae67-8885f53c55e5.png)

3. Next you want to set your default language again in `lsconfig.yml`
4. As the last step you have to reload all things you changed. The easiest way to do so is using the `/lsreload <config/players/languages>` command and execute it with each argument one time.

### Editing translations

In a subdirectory called `languages/` are to files by default. These contain the messages the plugin will need in two languages (`de.yml` -> german, `en.yml` -> english). 

#### Without variables

##### In other plugins config

In the message config of the plugin you want to translate (most times it's called `messages.yml`) you have to go to the message and edit the message to a placeholder:

![grafik](https://user-images.githubusercontent.com/69450649/162997604-07dfedc0-54d9-4404-b570-d757a77ff031.png)

##### In language files

Now you have to go back in the `languages/` subdirectory in the plugin's datafolder and go through every file in there and add a new point with the translation in the file's specific language:

![grafik](https://user-images.githubusercontent.com/69450649/162998956-c2cdad7d-dc5b-4e10-855e-c2b28653bb40.png)

![grafik](https://user-images.githubusercontent.com/69450649/162999354-9c625b6c-5006-4e50-bd6e-2f2ea4d25b8e.png)

#### With variables

Many messages have variables such as: 

![2022-04-12_17 35 36](https://user-images.githubusercontent.com/69450649/163000283-d5e4b006-6b4a-454d-9feb-a4e2c807c5d5.png)

You have to again use a placeholder but also define the variables:

![messages](https://user-images.githubusercontent.com/69450649/163003606-76c525bd-73b2-428e-b97d-49a7938a9535.png)

You can also use more than one variable: `ban-notification,vars={operator=%OPERATOR%,name=%NAME%}` is an excerpt from a modified AdvancedBans config

### Adding languages

Each language has one `.yml` file with the code of the ISO-3166-1-codelist of the country/language. If it doesn't exist in the list just thing of one yourself. For example the file for english is called `en.yml`.
To register the language you have to add one element to the `Languages` list in the `lsconfig.yml`:
```yml
Languages:
  - ==: LSLanguage
    name: English
    code: en
    enabled: true
  - ==: LSLanguage
    name: Deutsch
    code: de
    enabled: true
```
It's important that the `code` is equal to the file name.

## Commands

| Command                                | Description                                             |
| -------------------------------------- | ------------------------------------------------------- |
| /lslist                                | Lists all languages                                     |
| /lsreload <config/players/languages>   | Reloads the specific file(s)                            |
| /lang <auto/lang-code>                 | Sets your language (auto sets it to your game language) |

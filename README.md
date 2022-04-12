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



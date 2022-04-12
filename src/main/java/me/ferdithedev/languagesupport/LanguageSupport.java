package me.ferdithedev.languagesupport;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.ferdithedev.languagesupport.util.Config;
import me.ferdithedev.languagesupport.util.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LanguageSupport implements Listener {

    private final JavaPlugin au;

    private File[] languageFiles;
    private List<LSLanguage> languages = new ArrayList<>();
    private final List<LSLanguage> enabledLanguages = new ArrayList<>();
    private final List<String> enabledTranslations = new ArrayList<>();
    private File languageFolder;

    private YamlConfiguration LSConfig;
    private YamlConfiguration players;
    private File playersFile;

    private boolean loaded;

    public LanguageSupport(JavaPlugin au) {
        this.au = au;
        init();
    }

    public void init() {
        au.getDataFolder().mkdirs();
        new Config(au,"players.yml",true,"/languagesupport");
        playersFile = new File(au.getDataFolder()+"/languagesupport","players.yml");
        players= FileUtils.getConfigOfFile(playersFile);

        new Config(au,"lsconfig.yml",true,"/languagesupport");
        LSConfig = FileUtils.getConfigOfFile(new File(au.getDataFolder()+"/languagesupport","lsconfig.yml"));

        loaded = checkConfigs();

        if(!loaded) return;

        new Config(au, "en.yml", true, "/languagesupport/languages");
        new Config(au, "de.yml", true, "/languagesupport/languages");

        languageFolder = new File(au.getDataFolder() + "/languagesupport/languages");

        languageFolder.mkdirs();

        setLanguages((List<LSLanguage>) getLSConfig().get("Languages"));

        languageFiles = FileUtils.getFilesInDirectory(languageFolder);

        if(languageFiles != null) {
            checkMissingLanguageFiles();
        }

        loaded = checkConfigAccuracy();

        if(!loaded) return;

        loadEnabledLanguages();
        loadEnabledTranslations();

        initPacketListener();
    }

    private boolean checkConfigAccuracy() {
        boolean load = false;
        String defaultLang = LSConfig.getString("DefaultLanguage");
        for(LSLanguage language : languages) {
            if(language.enabled()) {
                if(language.code().equals(defaultLang)) {
                    load = true;
                    break;
                }
            }
        }
        if(!load) {
            au.getLogger().log(Level.SEVERE,"'DefaultLanguage' in 'lsconfig.yml' must be enabled in 'Languages' in 'lsconfig.yml'");
            au.getLogger().log(Level.SEVERE,"LanguageSupport will be disabled and need a server restart to load again");
        }

        return load;
    }

    private boolean checkConfigs() {
        boolean load = true;
        String[] LSConfigS = new String[]{"DefaultLanguage", "EnabledColor", "DisabledColor"};
        for(String s : LSConfigS) {
            if(this.LSConfig.contains(s) && this.LSConfig.getString(s) != null && !this.LSConfig.getString(s).trim().isEmpty()) continue;
            emptyException(s, "lsconfig.yml");
            load = false;
        }
        return load;
    }

    public boolean isNotLoaded() {
        return !loaded;
    }

    public void reloadConfig() {
        File file = new File(au.getDataFolder()+"/languagesupport","lsconfig.yml");
        LSConfig = FileUtils.getConfigOfFile(file);
    }

    public void reloadPlayers() {
        File file = new File(au.getDataFolder().getName()+"/languagesupport","players.yml");
        players = FileUtils.getConfigOfFile(file);
    }

    private void emptyException(String what, String where) {
        au.getLogger().log(Level.SEVERE, "Config section '" + what + "' in '" + where + "' can't be empty!");
        au.getLogger().log(Level.SEVERE,"LanguageSupport will be disabled and need a server restart to load again");
    }

    private void initPacketListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(au, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                String message = "";
                try {
                    String jsonMessage = event.getPacket().getChatComponents().getValues().get(0).getJson();
                    if(jsonMessage != null && !jsonMessage.isEmpty()) {
                        message = jsonToString(jsonMessage);
                    }
                } catch (Throwable ignored) {}

                String oldMessage = "";
                if (event.getPacket().getStrings().size() > 0) {
                    String jsonMessage = event.getPacket().getStrings().read(0);
                    if(jsonMessage != null) {
                        oldMessage = textToString(jsonMessage);
                    }
                }
                message = message.isEmpty() ? oldMessage : message;

                String uncolored = ChatColor.stripColor(message);

                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    String format = getLSConfig().getString("ChatFormat");
                    if (format != null) {
                        format = format.replace("%player%",p.getName());
                        if(uncolored.contains(format)) {
                            return;
                        }
                    }
                }

                if (message.isEmpty()) return;
                String send = translate(message, getPlayerLanguage(event.getPlayer()), event);
                if(send != null) {
                    event.getPlayer().sendMessage(send);
                }
            }

        });

    }

    public String translate(String message, LSLanguage language, PacketEvent... event) {
        String send = message;
        boolean doTranslation = false;
        YamlConfiguration playersLangConfig = FileUtils.getConfigOfFile(getLanguageFile(language));

        if(playersLangConfig.getConfigurationSection("Translations") != null) {
            for (String s : getEnabledTranslations()) {
                if(send.contains(s)) {
                    if(playersLangConfig.getString("Translations."+s) != null) {
                        if(send.contains(s+",vars={") || send.contains(",vars\\u003d{")) {
                            send = translateWithVars(send, s, playersLangConfig);
                        } else {
                            String t = ChatColor.translateAlternateColorCodes('&',playersLangConfig.getString("Translations." + s)).replace("&", "§");

                            if(getLSConfig().getBoolean("ReplaceWholeMessage")) {
                                send = t;
                            } else {
                                send = send.replace(s, t);
                            }
                        }
                    } else {
                        if(send.contains(s+",vars={") || send.contains(",vars\\u003d{")) {
                            send = translateWithVars(send, s, FileUtils.getConfigOfFile(getLanguageFile(getDefaultLanguage())));
                        } else {
                            String t = ChatColor.translateAlternateColorCodes('&',FileUtils.getConfigOfFile(getLanguageFile(getDefaultLanguage())).getString("Translations." + s));

                            if(getLSConfig().getBoolean("ReplaceWholeMessage")) {
                                send = t;
                            } else {
                                send = send.replace(s, t);
                            }
                        }
                    }
                    doTranslation = true;
                }
            }

            if(doTranslation && !send.isEmpty()) {
                if(event != null && event.length >= 1) {
                    event[0].setCancelled(true);
                }
                return send;
            }
        } else {
            au.getLogger().log(Level.WARNING, "Can't find configuration section 'Translations' in language file " + getLanguageFile(language).getName());
        }
        return null;
    }

    private String translateWithVars(String message, String translation, YamlConfiguration config) {
        int pos = (message.contains(",vars={")) ? message.indexOf(",vars={") : message.indexOf(",vars\\u003d{");
        String messageAfterVarsDeclaration = message.substring(pos);

        String[] parts = messageAfterVarsDeclaration.split("}");
        String vars = parts[0];
        vars = vars.replace("{", "");
        vars = vars.replace("}", "");
        vars = vars.replace(",vars=", "");
        vars = vars.replace(",vars\\u003d", "");

        String[] varsSplit = (vars.contains(",")) ? vars.split(",") : new String[]{vars};

        String value;
        String var;

        String translated = config.getString("Translations."+translation);

        for (String string : varsSplit) {
            string = string.replace(",","");
            string = string.trim();
            if(string.contains("=")) {
                var = string.split("=")[0];
                value = string.split("=")[1];
            } else {
                var = string.split("\\\\u003d")[0];
                value = string.split("\\\\u003d")[1];
            }

            if (translated != null) {
                translated = translated.replace("%"+var+"%", value);
            }
        }

        translated = translated != null ? ChatColor.translateAlternateColorCodes('&',translated) : null;
        return translated;
    }

    public void initPlayerToLangConfig(Player player) {
        if(!getPlayerConfig().contains("Players."+player.getUniqueId())) {
            getPlayerConfig().set("Players."+player.getUniqueId(), getDefaultLanguage().code());
            try {
                players.save(playersFile);
            } catch (IOException ignored) { }
        }
    }

    public LSLanguage getDefaultLanguage() {
        for(LSLanguage lang : languages) {
            if(lang.code().equals(getLSConfig().getString("DefaultLanguage"))) {
                return lang;
            }
        }
        return null;
    }

    public LSLanguage getPlayerLanguage(Player player) {
        if(getPlayerConfig().contains("Players."+player.getUniqueId())) {
            LSLanguage language = null;
            String languageCode = getPlayerConfig().getString("Players."+player.getUniqueId());
            for(LSLanguage lang : languages) {
                if(lang.code().equals(languageCode) && lang.enabled()) {
                    language = lang;
                    break;
                }
            }
            if (language == null) {
                language = getDefaultLanguage();
            }
            return language;
        } else {
            initPlayerToLangConfig(player);
            return getPlayerLanguage(player);
        }
    }

    public List<LSLanguage> getLanguages() {
        return languages;
    }

    private void checkMissingLanguageFiles() {
        List<String> fileNames = new ArrayList<>();
        for(File file : languageFiles) {
            fileNames.add(file.getName().replace(".yml", ""));
        }

        List<LSLanguage> forDisabling = new ArrayList<>();

        languages.forEach(language -> {
            if(language.enabled() && !(fileNames.contains(language.code()))) {
                forDisabling.add(language);
            }
        });


        forDisabling.forEach(lang -> {
            languages.remove(lang);
            languages.add(new LSLanguage(lang.name(), lang.code(), false));
            au.getLogger().log(Level.WARNING, "Disabled " + lang.name() + " in lsconfig.yml because it couldn't found in the languages folder (tipp: create file: " + lang.code() + ".yml)");
        });

        getLSConfig().set("Languages", languages);
        au.saveConfig();
    }

    private File getLanguageFile(LSLanguage language) {
        if(languageFiles != null) {
            for(File file : languageFiles) {
                if(file.getName().replace(".yml", "").equals(language.code())) {
                    return file;
                }
            }
        }
        return null;
    }

    public YamlConfiguration getPlayerConfig() {
        return players;
    }

    public void reloadLangConfigs() {
        languageFiles = FileUtils.getFilesInDirectory(languageFolder);
        if(languageFiles != null) {
            checkMissingLanguageFiles();
        }
        loadEnabledLanguages();
        loadEnabledTranslations();
    }

    public void setPlayerLanguage(Player player, LSLanguage language) {
        getPlayerConfig().set("Players."+player.getUniqueId(), language.code());
        try {
            getPlayerConfig().save(playersFile);
        } catch (IOException ignored) { }
    }

    public void loadEnabledLanguages() {
        enabledLanguages.clear();
        for(LSLanguage lang : languages) {
            if(lang.enabled()) {
                enabledLanguages.add(lang);
            }
        }
    }

    public List<LSLanguage> getEnabledLanguages() {
        return enabledLanguages;
    }

    public void loadEnabledTranslations() {
        enabledTranslations.clear();
        enabledTranslations.addAll(FileUtils.getConfigOfFile(getLanguageFile(getDefaultLanguage())).getConfigurationSection("Translations").getKeys(false));
    }

    private List<String> getEnabledTranslations() {
        return enabledTranslations;
    }

    public void setLanguages(List<LSLanguage> languages) {
        this.languages = languages;
    }

    public YamlConfiguration getLSConfig() {
        return LSConfig;
    }

    @EventHandler
    public void onKickMessage(PlayerKickEvent e) {
        String translate = translate(e.getReason(),getPlayerLanguage(e.getPlayer()));
        if(translate != null) {
            e.setReason(translate);
        }
    }

    private String getStringColor (String colorName){
        for (ChatColor c : ChatColor.values()) {
            if (c.name().equalsIgnoreCase(colorName)) return c.toString();
        }
        return "";
    }

    private String getStringValue (String key, String value){
        if(key.equalsIgnoreCase("text")) return value;
        if(key.equalsIgnoreCase("color")) return getStringColor(value);
        return "";
    }

    private String getBooleanValue (String key, boolean value){
        if(!value) return "";
        if(key.equalsIgnoreCase("bold")) return ChatColor.BOLD.toString();
        if(key.equalsIgnoreCase("italic")) return ChatColor.ITALIC.toString();
        if(key.equalsIgnoreCase("underlined")) return ChatColor.UNDERLINE.toString();
        if(key.equalsIgnoreCase("strikethrough")) return ChatColor.STRIKETHROUGH.toString();
        if(key.equalsIgnoreCase("obfuscated")) return ChatColor.MAGIC.toString();
        return "";
    }

    private String jsonToString (JSONObject source){
        StringBuilder result = new StringBuilder();
        for(String key : source.keySet()) {

            Object value = source.get(key);
            if(value instanceof String) {
                if (key == null)continue;
                result.append(getStringValue(key, (String) value));
            } else if(value instanceof Boolean) {
                if (key == null)continue;
                result.append(getBooleanValue(key, (Boolean) value));
            } else if(value instanceof JSONObject) {
                result.append(jsonToString((JSONObject) value));
            } else if(value instanceof JSONArray) {
                result.append(jsonToString((JSONArray) value));
            }

        }
        return result.toString();
    }

    private String jsonToString (JSONArray source){
        StringBuilder result = new StringBuilder();
        for (Object value : source) {
            if(value instanceof String) {
                result.append((String) value);
            } else if(value instanceof JSONObject) {
                result.append(jsonToString((JSONObject) value));
            } else if(value instanceof JSONArray) {
                result.append(jsonToString((JSONArray) value));
            }
        }
        return result.toString();
    }

    private String jsonToString(String json) {
        JSONObject jsonObject = new JSONObject(json);
        if (json.isEmpty()) return json;
        JSONArray array = (JSONArray) jsonObject.get("extra");
        if (array == null || array.isEmpty()) return json;
        return jsonToString (array);
    }

    private String textToString(String message){
        String text = message;
        if(text.matches("^\\{\"text\":\".*\"}")) {
            text = text.replaceAll("^\\{\"text\":\"", "");
            text = text.replaceAll("\"}$", "");
        }
        return ChatColor.stripColor(text);
    }

}

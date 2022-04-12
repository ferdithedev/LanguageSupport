package me.ferdithedev.languagesupport.util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileUtils {

    public static File[] getFilesInDirectory(File file) {
        if(file.isDirectory()) {
            return file.listFiles();
        } else {
            return null;
        }
    }

    public static YamlConfiguration getConfigOfFile(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }
}

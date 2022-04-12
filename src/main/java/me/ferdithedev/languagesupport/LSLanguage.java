package me.ferdithedev.languagesupport;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("LSLanguage")
public record LSLanguage(String name, String code, boolean enabled) implements ConfigurationSerializable {

    public static LSLanguage deserialize(Map<String, Object> args) {
        String name = "";
        String code = "";
        boolean enabled = false;
        if (args.containsKey("name")) {
            name = (String) args.get("name");
        }

        if (args.containsKey("code")) {
            code = (String) args.get("code");
        }

        if (args.containsKey("enabled")) {
            enabled = (boolean) args.get("enabled");
        }

        return new LSLanguage(name, code, enabled);
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("name", name);
        result.put("code", code);
        result.put("enabled", enabled);
        return result;
    }
}

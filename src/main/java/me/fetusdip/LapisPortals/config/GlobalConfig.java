package me.fetusdip.LapisPortals.config;

import me.fetusdip.LapisPortals.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class GlobalConfig extends BaseConfig {
    public static Material portalMaterial;
    public static boolean useLightning;
    public static boolean useSickness;
    public static boolean usePermsAndEcon;
    public static int teleportDelay;

    private static void portalSettings(){
        portalMaterial = getMaterial("portalMaterial", Material.LAPIS_BLOCK);
        useLightning = getBoolean("lightning", true);
        useSickness = getBoolean("teleSickness", true);
        usePermsAndEcon = getBoolean("usePermsAndEcon", true);
        teleportDelay = getInt("teleportDelay", 0);
    }

    public static boolean useVaultInstead;
    public static double price;
    public static Material materialCost;

    private static void priceSettings(){
        useVaultInstead = getBoolean("useVaultInstead", false);
        price = getDouble("price", 1);
        materialCost = getMaterial("priceMaterial", Material.OAK_LOG);

        if (useVaultInstead){
            price = Math.round(price);
        }
    }

    private static void loadMessages(){
        for (Messenger.Phrase phrase : Messenger.Phrase.values()){
            phrase.setMessage(ChatColor.translateAlternateColorCodes('&', getString(
                    "message." + phrase.name().replaceAll("__", ".").replaceAll("_", "-").toLowerCase(),
                    phrase.get()
            )));
        }
    }
}
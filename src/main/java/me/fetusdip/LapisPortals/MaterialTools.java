package me.fetusdip.LapisPortals;

import org.bukkit.Material;

public class MaterialTools {
    public static boolean isDoor(Material material) {
        switch (material) {
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case CRIMSON_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
            case WARPED_DOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHashable(Material material) {
        switch (material) {
            // Wool
            case WHITE_WOOL:
            case ORANGE_WOOL:
            case MAGENTA_WOOL:
            case LIGHT_BLUE_WOOL:
            case YELLOW_WOOL:
            case LIME_WOOL:
            case PINK_WOOL:
            case GRAY_WOOL:
            case LIGHT_GRAY_WOOL:
            case CYAN_WOOL:
            case PURPLE_WOOL:
            case BLUE_WOOL:
            case BROWN_WOOL:
            case GREEN_WOOL:
            case RED_WOOL:
            case BLACK_WOOL:
                // Wood
            case ACACIA_WOOD:
            case BIRCH_WOOD:
            case DARK_OAK_WOOD:
            case JUNGLE_WOOD:
            case OAK_WOOD:
            case SPRUCE_WOOD:
                // Log
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
                // Leaves
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                // Misc
            case BRICKS:
            case GLASS:
                return true;
            default:
                return false;
        }
    }
}
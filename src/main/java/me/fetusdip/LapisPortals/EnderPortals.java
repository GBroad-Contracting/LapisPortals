package me.fetusdip.LapisPortals;

import me.fetusdip.LapisPortals.config.ConfigManager;
import me.fetusdip.LapisPortals.config.GlobalConfig;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderPortals extends JavaPlugin {
	public final PlayerListener playerListener = new PlayerListener(this);
	private static FileHandler fileHandler;

	public void onDisable() {
		getFileHandler().save();
		PluginDescriptionFile pdfFile = getDescription();
		Messenger.info(pdfFile.getName() + " " + pdfFile.getVersion()
				+ " is now disabled.");
	}

	public void onEnable() {
		loadConfig();
		VaultHook.enable(this);
		EnderPortal.initialize(this);
		Messenger.init(this);

		setFileHandler(new FileHandler(this));
		PluginDescriptionFile pdfFile = getDescription();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Runnable() {
					public void run() {
						EnderPortals.getFileHandler().save();
					}
				}, 600L, 600L);
		Messenger.info(pdfFile.getName() + " " + pdfFile.getVersion()
				+ " is now enabled.");
	}

	public static FileHandler getFileHandler() {
		return fileHandler;
	}

	public static void setFileHandler(FileHandler fileHandler) {
		EnderPortals.fileHandler = fileHandler;
	}

	public void loadConfig() {
		ConfigManager manager = new ConfigManager(this);
		manager.loadConfig("config", GlobalConfig.class);
	}
}
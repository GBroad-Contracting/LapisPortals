package me.fetusdip.LapisPortals;

import java.util.List;
import java.util.logging.Logger;

import io.papermc.lib.PaperLib;
import me.fetusdip.LapisPortals.config.GlobalConfig;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.Openable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");

	private final EnderPortals plugin;

	public PlayerListener(EnderPortals newPlugin) {
		this.plugin = newPlugin;
	}

	@EventHandler
	public void onPlayerPlace(BlockPlaceEvent event) {
		if (MaterialTools.isDoor(event.getBlockPlaced().getType())
				&& (VaultHook.hasPermission(event.getPlayer(),
				VaultHook.Perm.CREATE))) {
			int facing = (((Directional) event.getBlockPlaced().getState()
					.getData()).getFacing().ordinal() + 2) % 4;
			Location tmp = event.getBlock().getLocation();
			Location loc = new Location(tmp.getWorld(), tmp.getX(),
					tmp.getY() - 1.0D, tmp.getZ());
			Player p = event.getPlayer();
			ValidPortalReturn returned = EnderPortal.validateLocation(loc,
					facing, this.plugin);
			if (returned.isValid()) {
				if (EnderPortals.getFileHandler().addPortal(
						loc.getWorld().getName(), facing, loc.getX(),
						loc.getY(), loc.getZ(), returned.getHash())) {
					Messenger.tell(p, Messenger.Phrase.CREATE_SUCCESS);
				} else {
					Messenger.tell(p, Messenger.Phrase.CREATE_FAIL);
				}
			} else if (returned.getHash() == -1) {
				Messenger.tell(p, Messenger.Phrase.CREATE_FAIL);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onRightClickDoor(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();

			if (block == null){
				return;
			}

			if (block.getType() == Material.IRON_DOOR) {
				if (block.getRelative(BlockFace.DOWN).getType() == Material.IRON_DOOR) {
					block = block.getRelative(BlockFace.DOWN);
				}

				if (EnderPortals.getFileHandler().isPortalDoor(block)) {
					BlockState state = block.getState();
					Openable door = (Openable) state.getData();
					door.setOpen(!door.isOpen());
					state.update();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		EnderPortal portal = EnderPortals.getFileHandler().onPortal(
				event.getPlayer());

		if (portal == null)
			return;

		if ((portal.isStillValid())
				&& (event.getClickedBlock() != null)
				&& (VaultHook.hasPermission(event.getPlayer(),
				VaultHook.Perm.TELEPORT))) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && MaterialTools.isDoor(event.getClickedBlock().getType())) {
				Block tmpBlock = event.getClickedBlock();
				if (MaterialTools.isDoor(tmpBlock.getRelative(BlockFace.DOWN).getType())) {
					tmpBlock = tmpBlock.getRelative(BlockFace.DOWN);
					Openable door = (Openable) tmpBlock.getState().getData();
					if (door.isOpen()) {
						Player p = event.getPlayer();
						Location toLoc = null;
						if (!portal.isGlobal()) {
							toLoc = EnderPortals.getFileHandler().getTpLocation(
									portal);
							if (toLoc != null) {
								EnderPortal toPortal = EnderPortals
										.getFileHandler().getTpPortal(portal);
								if (toPortal != null) {
									toPortal.setPlayerPortal(p, portal);
								}
							}
						} else {
							EnderPortal tmpPortal = portal.getPlayerPortal(p);
							if (tmpPortal != null) {
								toLoc = tmpPortal.getLocation();
							}
							if (toLoc == null) {
								Messenger.tell(p,
										Messenger.Phrase.TELEPORT_FAIL_UNBOUND);
							} else {
								toLoc = toLoc.clone().add(0.5D, 1.0D, 0.5D);
							}
						}
						if (toLoc != null) {
							List<MetadataValue> md = event.getPlayer().getMetadata("lpLastTele");
							if ((md.size() >= 1)
									&& (!VaultHook.hasPermission(event.getPlayer(),
									VaultHook.Perm.NO_DELAY))) {
								long dt = (System.currentTimeMillis() - md.get(0).asLong()) / 1000L;
								double delayRequired = GlobalConfig.teleportDelay;
								if (dt < delayRequired) {
									Messenger.tell(event.getPlayer(), ChatColor.RED
											+ "You must wait "
											+ (int) (delayRequired - dt)
											+ " more seconds");
									return;
								}
							}

							if (GlobalConfig.useVaultInstead) {
								if (!VaultHook.charge(event.getPlayer(), GlobalConfig.price)){
									Messenger.tell(p, Messenger.Phrase.TELEPORT_FAIL_NOT_ENOUGH);
									return;
								}
							} else {
								Inventory inventory = event.getPlayer().getInventory();
								int slot = inventory.first(GlobalConfig.materialCost);
								if (slot == -1){
									Messenger.tell(p, Messenger.Phrase.TELEPORT_FAIL_NOT_ENOUGH);
									return;
								}

								ItemStack itemStack = inventory.getItem(slot);

								if (itemStack == null){
									return;
								}

								if (itemStack.getAmount() < GlobalConfig.price){
									Messenger.tell(p, Messenger.Phrase.TELEPORT_FAIL_NOT_ENOUGH);
									return;
								}

								itemStack.setAmount((int) (itemStack.getAmount() - GlobalConfig.price));
							}

							if (VaultHook.hasPermission(p,
									VaultHook.Perm.LIGHTNING)) {
								if (GlobalConfig.useLightning) {
									portal.getLocation()
											.getWorld()
											.strikeLightningEffect(
													portal.getLocation()
															.clone()
															.add(0.0D, 3.0D,
																	0.0D));
									toLoc.getWorld()
											.strikeLightningEffect(
													toLoc.clone().add(0.0D,
															3.0D, 0.0D));
								}
							}

							if (!VaultHook.hasPermission(p,
									VaultHook.Perm.NO_SICKNESS)) {
								if (GlobalConfig.useSickness) {
									p.addPotionEffect(new PotionEffect(
											PotionEffectType.CONFUSION, 300, 1));
									p.addPotionEffect(new PotionEffect(
											PotionEffectType.HUNGER, 300, 1));
									p.addPotionEffect(new PotionEffect(
											PotionEffectType.WEAKNESS, 300, 1));
								}
							}

							PaperLib.teleportAsync(p, toLoc); // Async teleport or fallback to spigot sync

							BlockState toDoorState = toLoc.getBlock()
									.getState();
							Openable toDoor = (Openable) toDoorState.getData();
							toDoor.setOpen(true);
							toDoorState.update();
							toLoc.getBlock()
									.getWorld()
									.playEffect(toLoc.getBlock().getLocation(),
											Effect.DOOR_TOGGLE, 0);
							p.setMetadata(
									"lpLastTele",
									new FixedMetadataValue(this.plugin,
											System.currentTimeMillis()));

						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Block door = event.getBlock();
		Material type = door.getType();

		if (MaterialTools.isDoor(type)) {
			if (door.getRelative(BlockFace.DOWN).getType() == type)
				door = door.getRelative(BlockFace.DOWN);
			if (EnderPortals.getFileHandler().isPortalDoor(door))
				block = door.getRelative(BlockFace.DOWN);
		}

		EnderPortal port = EnderPortals.getFileHandler().getPortalBlock(block);
		if (port != null) {
			EnderPortals.getFileHandler().removePortal(port);
		}
	}

	public static Logger getLog() {
		return log;
	}
}
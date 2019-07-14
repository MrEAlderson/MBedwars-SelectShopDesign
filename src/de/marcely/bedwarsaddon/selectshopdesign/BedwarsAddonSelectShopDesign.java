package de.marcely.bedwarsaddon.selectshopdesign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.marcely.bedwars.Language;
import de.marcely.bedwars.Sound;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.BedwarsAddon;
import de.marcely.bedwars.api.Util;
import de.marcely.bedwars.api.BedwarsAddon.BedwarsAddonCommand;
import de.marcely.bedwars.api.CustomLobbyItem;
import de.marcely.bedwars.api.event.PlayerOpenShopEvent;
import de.marcely.bedwars.api.event.PlayerQuitArenaEvent;
import de.marcely.bedwars.api.gui.DecGUIItem;
import de.marcely.bedwars.api.gui.GUI;
import de.marcely.bedwars.api.gui.GUI.CenterFormatType;
import de.marcely.bedwars.api.gui.GUIItem;
import de.marcely.bedwars.game.shop.ShopDesignData;
import de.marcely.bedwars.game.shop.ShopDesignType;
import de.marcely.bedwars.versions.Version;

public class BedwarsAddonSelectShopDesign extends JavaPlugin {
	
	public static BedwarsAddon bedwarsAddon;
	
	public static HashMap<ShopDesignData, ItemStack> DESIGN_ICON = new HashMap<ShopDesignData, ItemStack>();
	public static List<ShopDesignData> DESIGN_DISABLED = new ArrayList<ShopDesignData>();
	public static ItemStack DESIGN_ICON_MISSING;
	private static HashMap<Player, ShopDesignData> PLAYERDESIGNS = new HashMap<Player, ShopDesignData>();
	
	@Override
	public void onEnable(){
		bedwarsAddon = new BedwarsAddon(this);
		
		// register default icon
		DESIGN_ICON.put(ShopDesignType.Normal.getData(), new ItemStack(Material.POTATO_ITEM));
		DESIGN_ICON.put(ShopDesignType.GommeHD.getData(), Util.getSkullItemstack("GommeHD", 1));
		DESIGN_ICON.put(ShopDesignType.HiveMC.getData(), new ItemStack(Material.GOLDEN_APPLE));
		DESIGN_ICON.put(ShopDesignType.HyPixel.getData(), new ItemStack(Material.WOOD_SWORD));
		DESIGN_ICON.put(ShopDesignType.Rewinside.getData(), Util.getSkullItemstack("Rewinside", 1));
		
		DESIGN_ICON_MISSING = new ItemStack(Material.SULPHUR);
		
		// load after every plugin has been loaded because maybe there's a custom design
		new BukkitRunnable(){
			public void run(){
				Config.load();
				Config.save();
			}
		}.runTaskLater(this, 1);
		
		// register item
		BedwarsAPI.registerLobbyItem(new CustomLobbyItem("selectshopdesign"){
			public void onUse(Player player){
				Util.playSound(player, Sound.LOBBY_VOTEARENA_OPEN);
				
				final GUI gui = new GUI(Message.GUI_TITLE.getMessage(), 0);
				
				for(final ShopDesignData data:BedwarsAPI.getShopDesigns()){
					// ignore if it's beta or if the design has been disabled
					if(data.getType().isBeta() || DESIGN_DISABLED.contains(data))
						continue;
					
					// if there's no icon, use missing icon
					ItemStack is = (DESIGN_ICON.containsKey(data) ? DESIGN_ICON.get(data) : DESIGN_ICON_MISSING).clone();
					Util.renameItemstack(is, ChatColor.WHITE + data.getName());
					
					// add glow effect&lore if player selected the design
					if(getPlayerDesign(player).equals(data))
						is = Util.setItemstackLore(Version.addGlow(is), Message.DESIGN_CHOOSEN.getMessage());
					
					// add item to gui
					gui.addItem(new GUIItem(is){
						public void onClick(Player player, boolean paramBoolean1, boolean paramBoolean2){
							if(!PLAYERDESIGNS.containsKey(player)){
								PLAYERDESIGNS.put(player, data);
								
								player.closeInventory();
								player.sendMessage(Message.DESIGN_CHOOSE.getMessage().replace("{design}", data.getName()));
								Util.playSound(player, Sound.LOBBY_VOTEARENA_VOTE);
							
							}else{
								final ShopDesignData pd = PLAYERDESIGNS.get(player);
								
								if(!pd.equals(data)){
									PLAYERDESIGNS.put(player, data);
									
									player.closeInventory();
									player.sendMessage(Message.DESIGN_CHOOSE.getMessage().replace("{design}", data.getName()));
									Util.playSound(player, Sound.LOBBY_VOTEARENA_VOTE);
								
								}else{
									player.sendMessage(Message.DESIGN_CHOOSE_ALREADY.getMessage().replace("{design}", data.getName()));
									Util.playSound(player, Sound.LOBBY_VOTEARENA_ALREADYVOTED);
								}
							}
						}
					});
				}
				
				gui.centerAtYAll(CenterFormatType.Normal);
				gui.setBackground(new DecGUIItem(Util.renameItemstack(new ItemStack(Material.STAINED_GLASS_PANE), " ")));
				
				gui.open(player);
			}
		});
		
		// register commands
		// reload
		bedwarsAddon.registerCommand(new BedwarsAddonCommand("reload", ""){
			public void onWrite(CommandSender sender, String[] args, String fullUsage){
				final long startTime = System.currentTimeMillis();
				sender.sendMessage(Language.Configurations_Reload_Start.getMessage());
				Config.load();
				Config.save();
				sender.sendMessage(Language.Configurations_Reload_End.getMessage().replace("{time}", "" + ((System.currentTimeMillis() - startTime) / 1000D)));
			}
		});
		
		// events
		Bukkit.getPluginManager().registerEvents(new Listener(){
			@EventHandler
			public void onPlayerQuitArenaEvent(PlayerQuitArenaEvent event){
				PLAYERDESIGNS.remove(event.getPlayer());
			}
			
			@EventHandler
			public void onPlayerOpenShopEvent(PlayerOpenShopEvent event){
				event.setDesign(getPlayerDesign(event.getPlayer()));
			}
		}, this);
	}
	
	public static final ShopDesignData getPlayerDesign(Player player){
		return PLAYERDESIGNS.containsKey(player) ? PLAYERDESIGNS.get(player) : BedwarsAPI.getShopDesign();
	}
}

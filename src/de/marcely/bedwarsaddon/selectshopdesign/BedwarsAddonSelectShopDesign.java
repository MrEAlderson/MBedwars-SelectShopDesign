package de.marcely.bedwarsaddon.selectshopdesign;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.marcely.bedwars.Sound;
import de.marcely.bedwars.util.Util;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.LobbyItem;
import de.marcely.bedwars.api.event.PlayerOpenShopEvent;
import de.marcely.bedwars.api.event.PlayerQuitArenaEvent;
import de.marcely.bedwars.api.gui.DecGUIItem;
import de.marcely.bedwars.api.gui.GUI;
import de.marcely.bedwars.api.gui.GUI.CenterFormatType;
import de.marcely.bedwars.api.gui.GUIItem;
import de.marcely.bedwars.config.ShopConfig;
import de.marcely.bedwars.game.shop.ShopDesignData;
import de.marcely.bedwars.game.shop.ShopDesignType;
import de.marcely.bedwars.versions.Version;

public class BedwarsAddonSelectShopDesign extends JavaPlugin {
	
	private static HashMap<ShopDesignData, ItemStack> MATERIALS = new HashMap<ShopDesignData, ItemStack>();
	private static ItemStack MATERIALS_NOTFOUND;
	
	public static HashMap<Player, ShopDesignData> PLAYERDESIGNS = new HashMap<Player, ShopDesignData>();
	
	@Override
	public void onEnable(){
		// register materials
		MATERIALS.put(ShopDesignType.Normal.getData(), new ItemStack(Material.POTATO_ITEM));
		MATERIALS.put(ShopDesignType.GommeHD.getData(), Util.getSkullItem("GommeHD", 1));
		MATERIALS.put(ShopDesignType.HiveMC.getData(), new ItemStack(Material.GOLDEN_APPLE));
		MATERIALS.put(ShopDesignType.HyPixel.getData(), new ItemStack(Material.WOOD_SWORD));
		MATERIALS.put(ShopDesignType.Rewinside.getData(), Util.getSkullItem("Rewinside", 1));
		
		MATERIALS_NOTFOUND = new ItemStack(Material.SULPHUR);
		
		// register item
		BedwarsAPI.registerLobbyItem(new LobbyItem("selectshopdesign"){
			public void onUse(Player player){
				Util.playSound(player, Sound.LOBBY_VOTEARENA_OPEN);
				
				final GUI gui = new GUI(Message.GUI_TITLE.getMessage(), 0);
				
				for(final ShopDesignData data:BedwarsAPI.getShopDesigns()){
					ItemStack is = (MATERIALS.containsKey(data) ? MATERIALS.get(data) : MATERIALS_NOTFOUND).clone();
					Util.renameItemStack(is, ChatColor.WHITE + data.getName());
					if(getPlayerDesign(player).equals(data))
						is = Version.addGlow(is);
					
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
				gui.setBackground(new DecGUIItem(Util.renameItemStack(new ItemStack(Material.STAINED_GLASS_PANE), " ")));
				
				gui.open(player);
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
		return PLAYERDESIGNS.containsKey(player) ? PLAYERDESIGNS.get(player) : ShopConfig.config_design;
	}
}

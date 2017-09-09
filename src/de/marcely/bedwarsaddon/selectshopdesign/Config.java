package de.marcely.bedwarsaddon.selectshopdesign;

import org.bukkit.inventory.ItemStack;

import de.marcely.bedwars.Language;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.Util;
import de.marcely.bedwars.config.ConfigManager;
import de.marcely.bedwars.config.ConfigManager.MultiKey.MultiKeyEntry;
import de.marcely.bedwars.game.shop.ShopDesignData;

public class Config {
	
	public static ConfigManager cm = BedwarsAddonSelectShopDesign.bedwarsAddon.getConfig();
	
	public static void load(){
		cm.load();
		
		// load enabled designs
		for(MultiKeyEntry<String, String> e:cm.getKeysWhichStartWith("design-enabled-").entrySet()){
			final ShopDesignData design = BedwarsAPI.getShopDesign(e.getKey().replaceFirst("design-enabled-", ""));
			
			if(design != null && Util.isBoolean(e.getValue()) && Boolean.valueOf(e.getValue()) == false)
				BedwarsAddonSelectShopDesign.DESIGN_DISABLED.add(design);
		}
		
		// load icons
		for(MultiKeyEntry<String, String> e:cm.getKeysWhichStartWith("design-icon-").entrySet()){
			final ShopDesignData design = BedwarsAPI.getShopDesign(e.getKey().replaceFirst("design-icon-", ""));
			final ItemStack is = Util.getItemItemstackByName(e.getValue());
			
			if(design != null && is != null)
				BedwarsAddonSelectShopDesign.DESIGN_ICON.put(design, is);
		}
		
		// load messages
		for(MultiKeyEntry<String, String> e:cm.getKeysWhichStartWith("message-").entrySet()){
			final Message msg = Message.getByName(e.getKey().replaceFirst("message-", ""));
			
			if(msg != null)
				msg.setCustomMessage(Language.stringToChatColor(e.getValue()));
		}
		
		cm.clear();
	}
	
	public static void save(){
		cm.clear();
		
		cm.addComment("Enable/disable shop designs");
		
		// write enabled/disabled designs
		for(ShopDesignData data:BedwarsAPI.getShopDesigns()){
			if(!data.getType().isBeta())
				cm.addConfig("design-enabled-" + data.getName(), !BedwarsAddonSelectShopDesign.DESIGN_DISABLED.contains(data));
		}
		
		cm.addEmptyLine();
		cm.addComment("Change the icon of the shop designs");
		
		// write icons
		for(ShopDesignData data:BedwarsAPI.getShopDesigns()){
			if(!data.getType().isBeta()){
				cm.addConfig("design-icon-" + data.getName(), 
						BedwarsAddonSelectShopDesign.DESIGN_ICON.containsKey(data) ?
								Util.itemstackToConfigName(BedwarsAddonSelectShopDesign.DESIGN_ICON.get(data)) : Util.itemstackToConfigName(BedwarsAddonSelectShopDesign.DESIGN_ICON_MISSING));
			}
		}
		
		cm.addEmptyLine();
		cm.addComment("Change messages");
		
		// write messages
		for(Message msg:Message.values())
			cm.addConfig("message-" + msg.name(), Language.chatColorToString(msg.getMessage()));
		
		cm.save();
	}
}

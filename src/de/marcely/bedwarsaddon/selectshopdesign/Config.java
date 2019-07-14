package de.marcely.bedwarsaddon.selectshopdesign;

import org.bukkit.inventory.ItemStack;

import de.marcely.bedwars.Language;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.Util;
import de.marcely.bedwars.game.shop.ShopDesignData;
import de.marcely.configmanager2.ConfigManager;

public class Config {
	
	public static ConfigManager cm = BedwarsAddonSelectShopDesign.bedwarsAddon.getConfig();
	
	public static void load(){
		cm.load();
		
		// load enabled designs
		for(de.marcely.configmanager2.objects.Config c:cm.getConfigsWhichStartWith("design-enabled-")){
			final ShopDesignData design = BedwarsAPI.getShopDesign(c.getName().replaceFirst("design-enabled-", ""));
			
			if(design != null && Util.isBoolean(c.getValue()) && Boolean.valueOf(c.getValue()) == false)
				BedwarsAddonSelectShopDesign.DESIGN_DISABLED.add(design);
		}
		
		// load icons
		for(de.marcely.configmanager2.objects.Config c:cm.getConfigsWhichStartWith("design-icon-")){
			final ShopDesignData design = BedwarsAPI.getShopDesign(c.getName().replaceFirst("design-icon-", ""));
			final ItemStack is = Util.getItemItemstackByName(c.getValue());
			
			if(design != null && is != null)
				BedwarsAddonSelectShopDesign.DESIGN_ICON.put(design, is);
		}
		
		// load messages
		for(de.marcely.configmanager2.objects.Config c:cm.getConfigsWhichStartWith("message-")){
			final Message msg = Message.getByName(c.getName().replaceFirst("message-", ""));
			
			if(msg != null)
				msg.setCustomMessage(Language.stringToChatColor(c.getValue()));
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

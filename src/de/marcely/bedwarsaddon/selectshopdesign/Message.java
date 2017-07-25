package de.marcely.bedwarsaddon.selectshopdesign;

import org.bukkit.ChatColor;

import lombok.Getter;

public enum Message {
	
	GUI_TITLE(ChatColor.AQUA + "Select Shop Design"),
	DESIGN_CHOOSE(ChatColor.GREEN + "You changed your design to " + ChatColor.DARK_GREEN + "{design}"),
	DESIGN_CHOOSE_ALREADY(ChatColor.RED + "You already selected this design!");
	
	@Getter private String message;
	
	private Message(String msg){
		this.message = msg;
	}
}

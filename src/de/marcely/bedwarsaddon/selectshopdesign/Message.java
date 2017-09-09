package de.marcely.bedwarsaddon.selectshopdesign;

import java.util.HashMap;

import org.bukkit.ChatColor;

public enum Message {
	
	GUI_TITLE(ChatColor.AQUA + "Select Shop Design"),
	DESIGN_CHOOSE(ChatColor.GREEN + "You changed your design to " + ChatColor.DARK_GREEN + "{design}"),
	DESIGN_CHOOSE_ALREADY(ChatColor.RED + "You already selected this design!"),
	DESIGN_CHOOSEN(ChatColor.GOLD + "You selected this design");
	
	private static final HashMap<Message, String> customMessages = new HashMap<Message, String>();
	
	private final String message;
	
	private Message(String msg){
		this.message = msg;
	}
	
	public String getMessage(){
		if(customMessages.containsKey(this))
			return customMessages.get(this);
		else
			return this.message;
	}
	
	public void setCustomMessage(String msg){
		customMessages.put(this, msg);
	}
	
	public static Message getByName(String msg){
		for(Message m:values()){
			if(m.name().equalsIgnoreCase(msg))
				return m;
		}
		
		return null;
	}
}

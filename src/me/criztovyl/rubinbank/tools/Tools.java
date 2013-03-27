package me.criztovyl.rubinbank.tools;

import me.criztovyl.rubinbank.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Tools{
	public static void msg(String p_n, String msg){
		Bukkit.getServer().getPlayer(p_n).sendMessage(msg);
	}
	public static boolean hasMajorOrMinorInHand(String playername){
		Player p = Bukkit.getServer().getPlayer(playername);
		if(p == null){
			return false;
		}
		return p.getItemInHand().getTypeId() == Config.getMajorID() ||
				p.getItemInHand().getTypeId() == Config.getMinorID();
	}
}

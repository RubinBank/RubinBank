package me.criztovyl.rubinbank.tools;

import org.bukkit.Bukkit;

public class Tools{
	public static void msg(String p_n, String msg){
		Bukkit.getServer().getPlayer(p_n).sendMessage(msg);
	}
}

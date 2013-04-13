package me.criztovyl.rubinbank.tools;

import me.criztovyl.rubinbank.bankomat.BankomatType;
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
	public static String getTypeLine(BankomatType type){
		switch(type){
		case AMOUNT:
			return "Kontostand";
		case BANKOMAT_LOC:
			break;
		case CHOOSING:
			return "";
		case CREATE:
			return "Kontoeröffnung";
		case IN:
			return "Einzahlen";
		case OUT:
			return "Auszahlen";
		case TRANSFER:
			return "Überweisung";
		case TRANSFER_AMOUNT:
			break;
		case TRANSFER_PLAYERII:
			break;
		case UCP_PASS:
			break;
		}
		return "";
	}
}

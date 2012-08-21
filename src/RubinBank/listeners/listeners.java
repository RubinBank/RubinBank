package RubinBank.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import RubinBank.RubinBank;
import RubinBank.bankomat.bankomat;
import RubinBank.tools.BankomatTyp;
import config.Config;

public class listeners implements Listener{
	private static Config conf = new Config();
	public void onPlayerClick(PlayerInteractEvent evt){
		if(evt.getMaterial() == Material.SIGN){
			Sign gray = (Sign) evt.getClickedBlock();
			if(gray.getLine(0) == "[Bankomat]"){
				int temp = RubinBank.getATemp();
				if(!RubinBank.isBankomat(gray.getBlock(), temp)){
					evt.getPlayer().sendMessage(ChatColor.RED+"Fehler: Block ist kein Schild. Bitte benachrichtige einen Dev. Code 01.");
				}
				bankomat bmat = (bankomat) RubinBank.getTemp(temp).getTemp();
				Material itemInHand = evt.getMaterial();
				Material up = conf.getBankomatUp();
				Material down = conf.getBankomatDown();
				if(evt.getPlayer().hasPermission("RubinBank.Bankomat.Use")){
					if(itemInHand == up || itemInHand == down ){					
						//change amount up/down
						if(itemInHand == up){
							//up
							if(evt.getPlayer().hasPermission("RubinBank.Bankomat.changeAmout.up")){
								//left click MAJOR
								if(evt.getAction() == Action.LEFT_CLICK_BLOCK)
									bmat.incraseMajor(bmat.getIncraseValueMajor(null));
								//right click MINOR
								if(evt.getAction() == Action.RIGHT_CLICK_BLOCK)
									bmat.incraseMinor(bmat.getIncraseValueMinor(null));
							}
							else{
								evt.getPlayer().sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung die Menge zu verändern.");
								evt.setCancelled(true);
							}
							
						}
						if(itemInHand == down){
							//down
							if(evt.getPlayer().hasPermission("RubinBank.Bankomat.changeAmout.down")){
								//left click MAJOR
								if(evt.getAction() == Action.LEFT_CLICK_BLOCK)
									bmat.decraseMajor(bmat.getDecraseValueMajor(null));
								//right click MINOR
								if(evt.getAction() == Action.RIGHT_CLICK_BLOCK)
									bmat.decraseMinor(bmat.getDecraseValueMinor(null));
							}
							else{
								evt.getPlayer().sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung die Menge zu verändern.");
								evt.setCancelled(true);
							}
						}
					}
					else{
						//do withdraw
						if(evt.getPlayer().hasPermission("RubinBank.Bankomat.withdraw")){
							evt.getPlayer().sendMessage("Not Implemented Yet");
							evt.setCancelled(true);
						}
						else{
							evt.getPlayer().sendMessage(ChatColor.RED + "DU hast nicht die Berechtigung etwas Abzuheben.");
							evt.setCancelled(true);
						}
					}
				}

			}
		}
	}
	public static void onSignChange(SignChangeEvent evt){
		Player player = evt.getPlayer();
		if(player.hasPermission("RubinBank.Bankomat.createSign")){
			Sign sign = (Sign) evt.getBlock();
			if(sign.getLine(0) == "[Bankomat]" || sign.getLine(0) == "[bankomat]"){
				sign.setLine(0, ChatColor.AQUA + "[Bankomat]");
				if(sign.getLine(1) == "Abheben" || sign.getLine(1) == "Auszahlen"){
					int temp = RubinBank.getATemp();
					RubinBank.isBankomat(sign.getBlock(), temp);
					bankomat bmat = (bankomat) RubinBank.getTemp(temp).getTemp();
					bmat.setType(BankomatTyp.OUT);
				}
				if(sign.getLine(1) == "Einzahlen"){
					int temp =RubinBank.getATemp();
					RubinBank.isBankomat(sign.getBlock(), temp);
					bankomat bmat = (bankomat) RubinBank.getTemp(temp).getTemp();
					bmat.setType(BankomatTyp.IN);
				}
			}

		}
	}
}

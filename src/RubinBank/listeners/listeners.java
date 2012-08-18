package RubinBank.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import RubinBank.RubinBank;
import RubinBank.bankomat.bankomatsign;

import config.Config;

public class listeners implements Listener{
	public void onPlayerClick(PlayerInteractEvent evt){
		if(evt.getMaterial() == Material.SIGN){
			Sign gray = (Sign) evt.getClickedBlock();
			if(gray.getLine(0) == "[Bankomat]"){
				bankomatsign bsign = RubinBank.getbankomatsign(evt.getClickedBlock());
				if(bsign == null){
					evt.getPlayer().sendMessage(ChatColor.RED + "Fehler! Bitte benachrichtige umgehen einen Dev.\n Fehlercode 0x0001");
					evt.setCancelled(true);
				}
				Material itemInHand = evt.getMaterial();
				Material up = Config.getBankomatUp();
				Material down = Config.getBankomatDown();
				if(evt.getPlayer().hasPermission("RubinBank.Bankomat.Use")){
					if(itemInHand == up || itemInHand == down ){
						//change amount up/down
						if(itemInHand == up){
							//up
							if(evt.getPlayer().hasPermission("RubinBank.Bankomat.changeAmout.up")){
								bsign.incraseAmount(10);
							}
							else{
								evt.getPlayer().sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung die Menge zu verändern.");
								evt.setCancelled(true);
							}
							
						}
						if(itemInHand == down){
							//down
							if(evt.getPlayer().hasPermission("RubinBank.Bankomat.changeAmout.down")){
								bsign.decraseAmount(10);
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
							
						}
						else{
							evt.getPlayer().sendMessage(ChatColor.RED + "DU hast nicht die Berechtiging die Menge zu verändern.");
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
				boolean out = false;
				if(sign.getLine(1) == "Abheben" || sign.getLine(1) == "Auszahlen"){
					out = true;
					sign.setLine(1, ChatColor.GOLD + "Abheben");
					sign.setLine(2, "10 "+Config.getMajorP());
					sign.setLine(3, "10 "+Config.getMinorP());
					bankomatsign bmatsign = new bankomatsign(out);
					bmatsign.setBlock(evt.getBlock());
					RubinBank.addBankomatSign(bmatsign);
				}
				if(sign.getLine(1) == "Einzahlen"){
					out = false;
					sign.setLine(1, ChatColor.WHITE + "Einzahlen");
					sign.setLine(2, "10 "+Config.getMajorP());
					sign.setLine(3, "10 "+Config.getMinorP());
					bankomatsign bmatsign = new bankomatsign(out);
					bmatsign.setBlock(evt.getBlock());
					RubinBank.addBankomatSign(bmatsign);
				}
			}

		}
	}
}

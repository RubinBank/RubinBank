package RubinBank.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import RubinBank.RubinBank;
import RubinBank.bankomat.bankomat;
import RubinBank.tools.BankomatTyp;
import RubinBank.tools.GetCompassDirection;
import config.Config;

public class listeners implements Listener{
	private static Config conf = new Config();
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent evt){
		if(evt.getMaterial() == Material.SIGN){
			Sign gray;
			if(evt.getClickedBlock().getState() instanceof Sign){
				gray = (Sign) evt.getClickedBlock().getState();
			}
			else{
				gray = null;
			}
			if(gray != null){
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
	}
	@EventHandler
	public static void onSignChange(SignChangeEvent evt){
		Player player = evt.getPlayer();
		player.sendMessage("SignChangeEvent...");
		if(evt.getBlock().getState() instanceof Sign){
			player.sendMessage("Its a sign...");
			if(player.hasPermission("RubinBank.Bankomat.createSign")){
				player.sendMessage("Has Permission...");
					player.sendMessage("Direction: "+GetCompassDirection.getCardinalDirection(player));
					player.sendMessage("Direction: "+GetCompassDirection.getAntiBlockFace(player).toString());
					player.sendMessage("Block.getState: "+evt.getBlock().getRelative(GetCompassDirection.getBlockFace(player)));
					Sign sign = (Sign) evt.getBlock().getRelative(GetCompassDirection.getBlockFace(player));
					if(sign.getLine(0) == "[Bankomat]" || sign.getLine(0) == "[bankomat]"){
						player.sendMessage("Its a bankomatsign");
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
					player.sendMessage("Its not a bankomatsign...");
					String test = "Test";
					player.sendMessage("sign:"+sign.toString());
					player.sendMessage(sign.getLines());
					if(sign.getLine(1).equals(test)){
						player.sendMessage("Its a test sign...");
					}
				}
			else{
				player.sendMessage("!");
				evt.setCancelled(true);
			}
		}
		else{
			player.sendMessage("!2");
			evt.setCancelled(false);
		}
	}
}

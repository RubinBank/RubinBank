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
import config.Config;

public class listeners implements Listener{
	private static Config conf = new Config();
	@EventHandler
	public static void onPlayerClick(PlayerInteractEvent evt){
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
		//player.sendMessage("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		//player.sendMessage("SignChangeEvent...");
		if(evt.getBlock().getState() instanceof Sign){
			//player.sendMessage("Its a sign...");
			if(player.hasPermission("RubinBank.Bankomat.createSign")){
				//player.sendMessage("Has Permission...");
					String[] lines = evt.getLines();
					if(lines[0].equals("[Bankomat]") || lines[0].equals("[bankomat]")){
						//player.sendMessage("Its a bankomatsign");
						evt.setLine(0, ChatColor.AQUA + "[Bankomat]");
						if(lines[1].equals("Abheben")  || lines[1].equals("Auszahlen") ){
							int temp = RubinBank.getATemp();
							RubinBank.isBankomat(evt.getBlock(), temp);
							bankomat bmat = (bankomat) RubinBank.getTemp(temp).getTemp();
							bmat.setType(BankomatTyp.OUT);
						}
						if(lines[1].equals("Einzahlen")){
							int temp =RubinBank.getATemp();
							RubinBank.isBankomat(evt.getBlock(), temp);
							bankomat bmat = (bankomat) RubinBank.getTemp(temp).getTemp();
							bmat.setType(BankomatTyp.IN);
						}
					}
					//player.sendMessage("Its not a bankomatsign...");
					String test = "Test";
					if(lines[0].equals(test)){
						evt.setLine(1, ChatColor.STRIKETHROUGH+"Not a Test Sign");
						player.sendMessage("Its a test sign...");
					}
					if(lines[0].equals("LightSign")){
						evt.getPlayer().setPlayerTime(0, false);
						player.sendMessage("HI");					}
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

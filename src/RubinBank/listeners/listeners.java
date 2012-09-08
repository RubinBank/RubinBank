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
			if(evt.getClickedBlock().getState() instanceof Sign){
				int temp = RubinBank.getATemp();
				if(RubinBank.isBankomat(evt.getClickedBlock(), temp)){
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
									if(evt.getAction() == Action.LEFT_CLICK_BLOCK){
										bmat.incraseMajorPD(evt.getPlayer());
									}
									//right click MINOR
									if(evt.getAction() == Action.RIGHT_CLICK_BLOCK){
										bmat.incraseMinorPD(evt.getPlayer());
									}
								}
								else{
									evt.getPlayer().sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung die Menge zu ver\u00E4ndern.");
									evt.setCancelled(true);
								}
								
							}
							if(itemInHand == down){
								//down
								if(evt.getPlayer().hasPermission("RubinBank.Bankomat.changeAmout.down")){
									//left click MAJOR
									if(evt.getAction() == Action.LEFT_CLICK_BLOCK)
										bmat.decraseMajorPD(evt.getPlayer());
									//right click MINOR
									if(evt.getAction() == Action.RIGHT_CLICK_BLOCK)
										bmat.decraseMinorPD(evt.getPlayer());
								}
								else{
									evt.getPlayer().sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung die Menge zu ver\u00E4ndern.");
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
								evt.getPlayer().sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung etwas Abzuheben.");
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
		String[] lines = evt.getLines();
		//Bankomat Sign
		if(lines[0].equals("[Bankomat]") || lines[0].equals("[bankomat]")){
			if(player.hasPermission("RubinBank.Bankomat.createSign")){
				evt.setLine(0, ChatColor.AQUA + "[Bankomat]");
				if(lines[1].equals("Abheben")  || lines[1].equals("Auszahlen") ){
					bankomat bmat = new bankomat(evt.getBlock());
					bmat.setType(BankomatTyp.OUT);
				}
				if(lines[1].equals("Einzahlen")){
					bankomat bmat = new bankomat(evt.getBlock());
					bmat.setType(BankomatTyp.IN);
					RubinBank.addBankomat(bmat);
				}
			}
		}
		//Test Sign
		String test = "Test";
		if(lines[0].equals(test)){
			if(player.hasPermission("RubinBank.dummy")){
				evt.setLine(1, ChatColor.STRIKETHROUGH+"STRIKETHROUGH");
				player.sendMessage("Its a Test Sign...");
			}
		}
	}
}

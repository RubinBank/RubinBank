package me.criztovyl.rubinbank.listeners;


import me.criztovyl.clickless.ClicklessPlugin;
import me.criztovyl.questioner.MicroQuestioner;
import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.bankomat.Bankomat;
import me.criztovyl.rubinbank.bankomat.BankomatType;
import me.criztovyl.rubinbank.bankomat.TriggerPosition;
import me.criztovyl.rubinbank.tools.Tools;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;


public class Listeners implements Listener{
        @EventHandler
        public static void onPlayerClick(PlayerInteractEvent evt){
                String p_n = evt.getPlayer().getName();
                if(evt.getAction() == Action.RIGHT_CLICK_BLOCK){
                        if(evt.getClickedBlock().getType().equals(Material.SIGN_POST) || evt.getClickedBlock().getType().equals(Material.SIGN)
                                        || evt.getClickedBlock().getType().equals(Material.WALL_SIGN)){
                                BlockState state = evt.getClickedBlock().getState();
                                Sign sign = (Sign) state;
                                if(sign.getLine(0).equals(ChatColor.DARK_AQUA + "[RubinBank]")){
                                        evt.getPlayer().sendMessage(ChatColor.DARK_BLUE + "Nope.");                                     
                                }       
                        }
                        if(evt.getClickedBlock().getType().equals(Material.STONE_BUTTON) || evt.getClickedBlock().equals(Material.WOOD_BUTTON)){
                                if(TriggerButton.isTriggerButton(evt.getClickedBlock().getLocation())){
                                        if(TriggerButton.getType(evt.getClickedBlock().getLocation()).equals(TriggerButtonType.AMOUNT)){
                                                if(RubinBank.getHelper().getBank().hasAccount(p_n)){
                                                        RubinBank.getHelper().getBank().getAccount(p_n).sendBalanceMessage();
                                                }
                                        }
                                        if(TriggerButton.getType(evt.getClickedBlock().getLocation()).equals(TriggerButtonType.CREATE)){
                                                if(!RubinBank.getHelper().getBank().hasAccount(p_n)){
                                                        RubinBank.getHelper().getBank().createAccount(p_n);
                                                }
                                        }
                                }
                        }
                }
        }
        @EventHandler
        public static void onSignChange(final SignChangeEvent evt){
                Player player = evt.getPlayer();
                final String p_n = evt.getPlayer().getName();
                final String[] lines = evt.getLines();
                String eline2 = "";
                final String line3 = evt.getLine(2);
                final String line4 = evt.getLine(3);
                //Checks if could be a RubinBank Sign
                if(lines[0].equals("[RubinBank]") || lines[0].equals("[RB]")){
                        //Checks if it's a Bankomat Sign
                        if(lines[1].toLowerCase().equals("bankomat")){
                                //Checks if there is a Valid Position
                                if(
                                                lines[2].toLowerCase().equals("up") || 
                                                lines[2].toLowerCase().equals("down")
                                        ){
                                        //Non-Multi Sign
                                        if(!lines[3].equals("")){
                                                //Checks if there is a Valid Type
                                                if(
                                                                lines[3].toLowerCase().equals("einzahlen") || 
                                                                lines[3].toLowerCase().equals("auszahlen") || 
                                                                lines[3].toLowerCase().equals("kontostand") || 
                                                                lines[3].toLowerCase().equals("überweisen") || 
                                                                lines[3].toLowerCase().equals("erstellen")
                                                        ){
                                                        ClicklessPlugin.getMicroQuestions().addQuestioner(new MicroQuestioner() {
                                                                boolean success = false;
                                                                @Override
                                                                public boolean getSuccess() {
                                                                        return success;
                                                                }
                                                                
                                                                @Override
                                                                public String getQuestion() {
                                                                        return "Wo steht dieser Bankomat?";
                                                                }
                                                                
                                                                @Override
                                                                public String getPlayer() {
                                                                        return p_n;
                                                                }
                                                                
                                                                @Override
                                                                public void executeAction(AsyncPlayerChatEvent arg0) {
                                                                        RubinBank.getHelper().getBankomats().addBankomat(new Bankomat(
                                                                                        evt.getBlock().getLocation(),
                                                                                        BankomatType.getType(line4),
                                                                                        TriggerPosition.valueOf(line3.toUpperCase()),
                                                                                        arg0.getMessage(),
                                                                                        true));
                                                                        RubinBank.getHelper().info("Created Sign.");
                                                                        Tools.msg(p_n, ChatColor.GREEN + "Created Bankomat.");
                                                                        success = true;
                                                                }
                                                        });
                                                        eline2 = BankomatType.getType(lines[3].toLowerCase()).getTypeStringGerman();
                                                }
                                        }
                                        //Multi-Sign
                                        else{
                                                final TriggerPosition pos = TriggerPosition.valueOf(lines[2].toUpperCase());
                                                final BankomatType type = BankomatType.CHOOSING;
                                                ClicklessPlugin.getMicroQuestions().addQuestioner(new MicroQuestioner() {
                                                        boolean success = false;
                                                        @Override
                                                        public boolean getSuccess() {
                                                                return success;
                                                        }
                                                        
                                                        @Override
                                                        public String getQuestion() {
                                                                return "Wo steht dieser Bankomat?";
                                                        }
                                                        
                                                        @Override
                                                        public String getPlayer() {
                                                                return p_n;
                                                        }
                                                        
                                                        @Override
                                                        public void executeAction(AsyncPlayerChatEvent arg0) {
                                                                
                                                                RubinBank.getHelper().getBankomats().addBankomat(new Bankomat(
                                                                                evt.getBlock().getLocation(), 
                                                                                type, 
                                                                                pos, 
                                                                                arg0.getMessage(),
                                                                                true));
                                                                success = true;
                                                        }
                                                });
                                        }
                                }
                                else{
                                        player.sendMessage("Zeile drei ist ungültig.");
                                }
                        }
                        evt.setLine(0, ChatColor.DARK_AQUA + "[RubinBank]");
                        evt.setLine(1, ChatColor.DARK_AQUA + "Bankomat");
                        evt.setLine(2, eline2);
                        evt.setLine(3, "");
                }
        }
        @EventHandler
        public static void onSignBreak(BlockBreakEvent evt){
                if(evt.getBlock().getType().equals(Material.SIGN_POST) || evt.getBlock().getType().equals(Material.SIGN)
                                || evt.getBlock().getType().equals(Material.WALL_SIGN)){
                        Sign sign = (Sign) evt.getBlock().getState();
                        if(sign.getLine(0).equals(ChatColor.DARK_AQUA + "[RubinBank]") || sign.getLine(0).equals("[RB]")){
                                if(sign.getLine(1).equals(ChatColor.DARK_AQUA + "Bankomat")){
                                        RubinBank.getHelper().getBankomats().removeBankomatByLocation(evt.getBlock().getLocation());
                                        evt.getPlayer().sendMessage("Removed Sign.");
                                }
                        }
                }
                if(evt.getBlock().getType().equals(Material.STONE_BUTTON) || evt.getBlock().equals(Material.WOOD_BUTTON)){
                        if(TriggerButton.isTriggerButton(evt.getBlock().getLocation())){
                                evt.getPlayer().sendMessage("Unimplemented! Moving to Clickless!");
                                //MySQL_old.removeTriggerButton(evt.getBlock().getLocation());
                        }
                }
        }
        @EventHandler
        public static void onPluginEnable(PluginEnableEvent evt){
                if(evt.getPlugin().getName().equals("WorldGuard")){
                        RubinBank.setUseWorldGuard();
                        Bukkit.getPluginManager().getPlugin("RubinBank").getLogger().info("WorldGuard found and Support enabled :)");
                }
                if(evt.getPlugin().getName().equals("Vault")){
                    if(!RubinBank.getHelper().isHooked()){
                        RubinBank.getHelper().registerEconomy();
                    }
                }
        }
        @EventHandler
        public void onPluginDisable(PluginDisableEvent evt){
            if(evt.getPlugin().getName().equals("Vault")){
                RubinBank.getHelper().unhookEconomy();
            }
        }
}

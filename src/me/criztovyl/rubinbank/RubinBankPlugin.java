package me.criztovyl.rubinbank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.listeners.Listeners;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class RubinBankPlugin extends JavaPlugin{
    private static boolean useWorldGuard;
    private boolean failure = false;
    private static RubinBank helper;
    private Logger log;
    /**
     * Bukkit Stuff
     */
    public void onEnable(){
        log = this.getLogger();
        try {
            helper = new RubinBank(this);
            helper.init();
        } catch (SQLException e1) {
            log.severe("SQL Exception @ Creating RubinBank Helper Object:\n" + e1.toString());
            e1.printStackTrace();
            failure = true;
            this.disable();
        }
        if(helper.getConfig().getBoolean(Config.USERUBINBANK.getPath())){
            helper.info("RubinBank enabeling...");
            Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
            //write the default Configuration if not exists and is set in the Configuration
            this.saveDefaultConfig();
            helper.info("RubinBank enabled.");
            if(!failure){
                //Load the Bankomat Signs to ClicklessSigns
                //MySQL_old.updateTriggers();
                //Load the Trigger Buttons
                //MySQL_old.updateTriggerButtons();
            }
            else{
                helper.severe("There was errors while enabeling. Please Check! RubinBank will be disabled...");
                Bukkit.getServer();
                Bukkit.getServer().broadcast("[RubinBank] Während der Aktivierung sind Fehler aufgetreten! RubinBank wird wieder deaktiviert...", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
                disable();
            }
        }
        else{
            helper.info("RubinBank disabled by option from config.");
            Bukkit.getPluginManager().getPlugin("RubinBank").getPluginLoader().disablePlugin((Plugin) this);
        }

    }
    /**
     * Disable RubinBank
     */
    private void disable(){
        Bukkit.getPluginManager().getPlugin("RubinBank").getPluginLoader().disablePlugin((Plugin) this);
    }
    /**
     * Bukkit Stuff
     */
    public void onDisable(){
        if(!failure){
            try {
                //Save Bank, Accounts and AccountStatements
                String table_accounts = helper.getConfig().getString(Config.ACCOUNTS.getPath());
                String table_statements = helper.getConfig().getString(Config.STATEMENTS.getPath());
                helper.getBank().save(helper.getReopenable(), table_accounts, table_statements);
                //Save New Bankomats
                helper.getBankomats().save();
            } catch (SQLException e) {
                log.severe("There was an Error @ Save Bank:\n" + e.toString());
            }
        }
        helper.info("RubinBank disabeling after " + helper.getSimpleLifeTimeString() + "...");
        helper.info("RubinBank disabled.");
    }
    /**
     * Sends a Player a message by his name
     * @param p_n
     * @param msg
     */
    private static void msg(String p_n, String msg){
        helper.msg(p_n, msg);
    }
    /**
     * Bukkit Stuff
     */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(sender instanceof Player){
            //The Player who send the Command
            Player player = (Player) sender;
            // The Player name of him/her
            String p_n = sender.getName();
            //The RubinBank native Command
            if(cmd.getName().equalsIgnoreCase("rubinbank")){
                //checks if the Player has the Permission to use the Command.
                if(player.hasPermission("RubinBank.cmd.self")){
                    if(args.length >= 1){
                        //List 
                        if(args[0].equals("ids")){
                            msg(p_n, "Major: " + Material.getMaterial(
                                        helper.getConfig().getInt(Config.MAJORID.getPath())
                                    ));
                            msg(p_n, "Minor: " + Material.getMaterial(
                                        helper.getConfig().getInt(Config.MINORID.getPath())
                                    ));
                            return true;
                        }
                        if(args[0].toLowerCase().equals("accounts")){
                            if(player.hasPermission("RubinBank.cmd.listaccs") || player.isOp()){
                                ArrayList<Account> accs = helper.getBank().getAccounts();
                                for(int i = 0; i < accs.size(); i++){
                                    msg(p_n, ChatColor.ITALIC + accs.get(i).getOwner() + ": "
                                            + accs.get(i).getBalance());
                                }
                            }
                        }
                    }
                }
            }
            if(cmd.getName().equalsIgnoreCase("TriggerButton")){
                //Will be moved to Clickless
                Block b = player.getTargetBlock(null, 20);
                if(args.length > 0){
                    if(args.length >= 1){
                        if(args.length >= 2){
                            if(args[0].toLowerCase().equals("create")){
                                if(args[1].toLowerCase().equals("amount") || args[2].toLowerCase().equals("create")){
                                    if(b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.WOOD_BUTTON)){
                                        TriggerButton.addTriggerButton(b.getLocation(), TriggerButtonType.valueOf(args[1].toUpperCase()));
                                        msg(p_n, ChatColor.DARK_AQUA + "Erstellt.");
                                        return true;
                                    }
                                    else{
                                        msg(p_n, "Das ist kein Button!");
                                    }
                                }
                                else{
                                    msg(p_n, "Ungültiger Typ!");
                                    msg(p_n, "Typen: 'create' und 'amount'");
                                }
                            }
                        }
                        if(args[0].toLowerCase().equals("list") || args[0].toLowerCase().equals("ls")){
                            msg(p_n, "LocationX, LocationY, LocationZ from ArrayList");
                            for(int i = 0; i < TriggerButton.triggerbuttons.size(); i++){
                                    Location loc = TriggerButton.triggerbuttons.get(i);
                                    msg(p_n, loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                            }
                        return true;
                        }
                    }
                }
                else{
                    msg(p_n, "RubinBank TriggerButtons: Erstelle und Verwalte TriggerButtons:");
                    msg(p_n, "list(alias: ls)");
                    msg(p_n, "create [type]: erstelle einen Button mit Typ 'create'(Konto erstellen) oder 'amount'(Kontostand).");
                }
            }
            if(cmd.getName().equalsIgnoreCase("error")){
                msg(p_n, "Du hast das Error Command ausgeführt...\n\u00A2");
                msg(p_n, "WHY DO YOU DO THIS? Arghhhh...");
                player.kickPlayer(ChatColor.RED + "Server is crashing...");
                return false;
            }
            if(cmd.getName().equalsIgnoreCase("account")){
                if(args.length == 0){
                    if(helper.getBank().hasAccount(p_n))
                        helper.getBank().getAccount(p_n).sendBalanceMessage();
                    else
                        msg(p_n, ChatColor.RED + "Du hast kein Konto.");
                    return true;
                }
                List<String> cmds = helper.getConfig().getStringList(Config.COMMANDSLIMITEDTOREGION.getPath());
                if(args.length >= 1){
                    if(args[0].equals("create")){
                        if(cmds.contains("create") || cmds.contains("all")){
                            if(inWorldGuardRegion(player)){
                                helper.getBank().createAccount(p_n);
                                msg(p_n, ChatColor.GREEN + "Konto eröffnet.");
                                return true;
                            }
                            else{
                                msg(p_n, "Du kannst ein Konto nur in einer Bankfiliale eröffnen.");
                                return true;
                            }
                        }
                        else{
                            helper.getBank().createAccount(p_n);
                            msg(p_n, ChatColor.GREEN + "Konto eröffnet.");
                            return true;
                        }
                    }
                    if(args[0].equals("amount")){
                        if(cmds.contains("amount") || cmds.contains("all")){
                            if(inWorldGuardRegion(player)){
                                if(helper.getBank().hasAccount(p_n)){
                                    helper.getBank().getAccount(p_n).sendBalanceMessage(ChatColor.DARK_AQUA);
                                }
                                return true;
                            }
                            else{
                                msg(p_n, "Du kannst deinen Kontostand nur in einer Bankfiliale abfragen.");
                                return true;
                            }
                        }
                        else{
                            if(helper.getBank().hasAccount(p_n)){
                                helper.getBank().getAccount(p_n).sendBalanceMessage(ChatColor.DARK_AQUA);
                            }
                        }
                    }
                    if(args.length >= 2){
                        if(args[0].equals("payin")){
                            if(cmds.contains("payin") || cmds.contains("all")){
                                if(inWorldGuardRegion(player)){
                                    if(player.getItemInHand().getTypeId() == helper.getCurrency().getMajorID() ||
                                            player.getItemInHand().getTypeId() == helper.getCurrency().getMinorID()){
                                        if(helper.getBank().hasAccount(p_n)){
                                            helper.getBank().getAccount(p_n).payInItemInHand();
                                            msg(p_n, ChatColor.GREEN + "Done :)");
                                            return true;
                                        }
                                    }
                                    try{
                                        if(helper.getBank().hasAccount(p_n)){
                                            helper.getBank().getAccount(p_n).payInViaInv(Double.parseDouble(args[1]));
                                        }
                                    } catch(NumberFormatException e){
                                        msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
                                    }
                                }
                                else{
                                    msg(p_n, "Du kannst nur in einer Bankfiliale einzahlen");
                                }
                            }
                            else{
                                if(player.getItemInHand().getTypeId() == helper.getCurrency().getMajorID() ||
                                        player.getItemInHand().getTypeId() == helper.getCurrency().getMinorID()){
                                    if(helper.getBank().hasAccount(p_n)){
                                        helper.getBank().getAccount(p_n).payInItemInHand();
                                        msg(p_n, ChatColor.GREEN + "Done :)");
                                        return true;
                                    }
                                }   
                                try{
                                    if(helper.getBank().hasAccount(p_n)){
                                        helper.getBank().getAccount(p_n).payInViaInv(Double.parseDouble(args[1]));
                                    }
                                } catch(NumberFormatException e){
                                    msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
                                }
                            }
                        }
                        if(args[0].equals("payout")){
                            if(cmds.contains("payout") || cmds.contains("all")){
                                if(inWorldGuardRegion(player)){
                                    try{
                                        if(helper.getBank().hasAccount(p_n)){
                                            helper.getBank().getAccount(p_n).payOutViaInv(Double.parseDouble(args[1]));
                                        }
                                    } catch(NumberFormatException e){
                                        msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
                                    }
                                }
                                else{
                                    msg(p_n, "Du kannst nur in einer Bankfiliale Geld abheben.");
                                }
                            }
                            else{
                                try{
                                    if(helper.getBank().hasAccount(p_n)){
                                        helper.getBank().getAccount(p_n).payOutViaInv(Double.parseDouble(args[1]));
                                    }
                                } catch(NumberFormatException e){
                                    msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
                                }
                            }
                        }
                    }
                    if(args[0].equals("help")){
                        msg(p_n, "/account create:          Erstelle ein Konto.");
                        msg(p_n, "/account amount get:      Frage deinen Kontostand ab.");
                        msg(p_n, "/account payin  [amount]: Zahle auf dein Konto ein.");
                        msg(p_n, "/account payout [amount]: Hebe von deinem Konto ab.");
                    }
                }
                else{
                    msg(p_n, "/account create:          Erstelle ein Konto.");
                    msg(p_n, "/account amount get:      Frage deinen Kontostand ab.");
                    msg(p_n, "/account payin  [amount]: Zahle auf dein Konto ein.");
                    msg(p_n, "/account payout [amount]: Hebe von deinem Konto ab.");
                }
            return true;
            }//Account CMD END
        }//PLAYER END
        else{
            if(cmd.getName().equalsIgnoreCase("rubinbank")){
                if(args.length >= 1){
                    if(args[0].toLowerCase().equals("accounts")){
                        ArrayList<Account> accs = helper.getBank().getAccounts();
                        for(int i = 0; i < accs.size(); i++){
                            helper.info(accs.get(i).getOwner() + ": "
                                    + accs.get(i).getBalance());
                        }
                        return true;
                    }
                    
                }
                helper.info("Only a Player can perform this command!");
                return true;
            }
            return true;
        }//CONSOLE END
        return true;
    }
    /**
     * Get Boolean if WorldGuard is present and/or should be used.
     * @return Boolean if WorldGuard is usable and should used
     */
    public static boolean getUseWorldGuard(){
        return useWorldGuard;
    }
    /**
     * Set the Boolean if WorldGuard is present and/or should be used to true.
     */
    public static void setUseWorldGuard(){
        if(helper.getConfig().getBoolean(Config.USEWORLDGUARD.getPath())){
            useWorldGuard = true;
        }
    }
    /**
     * Checks if a player is in a WorldGuard Region with the parent defined in the Config.
     * @param player
     * @return if the Players is inside such a region true; any other false
     */
    public static boolean inWorldGuardRegion(Player player){
        if(Bukkit.getPluginManager().getPlugin("WorldGuard").isEnabled()){
            WorldGuardPlugin wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
            ApplicableRegionSet regions = wgp.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
            for(Iterator<ProtectedRegion> i = regions.iterator(); i.hasNext();){
                ProtectedRegion region = i.next();
                if(region.getParent() != null){
                    if(region.getParent().getId().equals(
                            helper.getConfig().getString(Config.COMMANDLIMITREGION.getPath()).toLowerCase())
                     ){
                        return true;
                    }
                }
                
            }
            return false;
        }
        else{
            return false;
        }
    }
    /**
     * @return The RubinBank PluginHelper
     */
    public static RubinBank getHelper(){
        return helper;
    }
    /**
     * @return The RubinBank Plugin
     */
    public static Plugin getPlugin(){
        return Bukkit.getServer().getPluginManager().getPlugin("RubinBank");
    }
}

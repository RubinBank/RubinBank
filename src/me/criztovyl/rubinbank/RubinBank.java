package me.criztovyl.rubinbank;

import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Logger;

import me.criztovyl.rubinbank.Vault.Economy_RubinBank;
import me.criztovyl.rubinbank.bank.Bank;
import me.criztovyl.rubinbank.bankomat.Bankomats;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.mysql.Reopenable;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public class RubinBank implements PluginHelper{
    private Bank bank;
    private Logger log;
    private Calendar start, end;
    private Bankomats bankomats;
    private boolean debug;
    private boolean vaulthooked;
    private Plugin vault;
    private FileConfiguration config;
    private Currency rubinBankCurrency;
    private Reopenable reopenable;
    /**
     * A new RubinBank
     * @param plugin the RubinBank Plugin
     */
    public RubinBank(Plugin plugin){
        log = plugin.getLogger();
        start = Calendar.getInstance();
        config = plugin.getConfig();
        rubinBankCurrency = new RubinBankCurrency(getConfig());
    }
    /**
     * Initiate the RubinBank
     * @throws SQLException
     */
    public void init() throws SQLException{
        final String dburl = "jdbc:mysql://" + getConfig().getString(Config.MYSQL_HOST.getPath()) + ":" +
                getConfig().getString(Config.MYSQL_PORT.getPath()) + "/" + getConfig().getString(Config.MYSQL_DATABASE.getPath());
        final String dbuser = getConfig().getString(Config.MYSQL_USER.getPath());
        final String dbpassword = getConfig().getString(Config.MYSQL_PASSWORD.getPath());
        log.info(dburl);
        reopenable = new Reopenable() {
            
            @Override
            public java.sql.Connection getDatabaseConnection() throws SQLException {
                return DriverManager.getConnection(dburl, dbuser, dbpassword);
            }
        };
            bankomats = new Bankomats(reopenable, getConfig().getString(Config.BANKOMATS.getPath()));
            bankomats.load();
            bank = new Bank(new RubinBankCurrency(getConfig()));
            bank.load(getReopenable(), getConfig().getString(Config.ACCOUNTS.getPath()));
            debug = getConfig().getBoolean(Config.DEBUG.getPath());
            vaulthooked = false;
            registerEconomy();
    }
    /**
     * @return the Bank
     */
    public Bank getBank(){
            return bank;
    }
    @Override
    public void info(String msg) {
            log.info(msg);
    }
    @Override
    public void severe(String msg) {
            log.severe(msg);
    }
    @Override
    public String getLifeTimeString() {
            return "RubinBank lives since " + getSimpleLifeTimeString();
    }
    @Override
    public String getSimpleLifeTimeString() {
            end = Calendar.getInstance();
            int start_h = start.get(Calendar.HOUR_OF_DAY);
            int start_m = start.get(Calendar.MINUTE);
            int start_s = start.get(Calendar.SECOND);
            int start_ms = start.get(Calendar.MILLISECOND);
            int end_h = end.get(Calendar.HOUR_OF_DAY);
            int end_m = end.get(Calendar.MINUTE);
            int end_s = end.get(Calendar.SECOND);
            int end_ms = end.get(Calendar.MILLISECOND);
            String lifeTime = String.format("%d:%d:%d:%d (h:m:s:ms)", 
                            end_h - start_h,
                            end_m - start_m,
                            end_s - start_s,
                            end_ms - start_ms);
            return lifeTime;
    }
    @Override
    public void warning(String msg) {
            log.warning(msg);
    }
    /**
     * @return a Object of all Bankomats
     */
    public Bankomats getBankomats() {
            return bankomats;
    }
    /**
     * Vault Stuff
     */
    public void registerEconomy(){
        vault = Bukkit.getServer().getPluginManager().getPlugin("Vault");
         if(vault != null && !vaulthooked){
             try {
                 Bukkit.getServer().getServicesManager()
                         .register(Economy.class, Economy_RubinBank.class.getConstructor(Plugin.class).newInstance(vault), vault , ServicePriority.High);
                 vaulthooked = true;
             } catch (IllegalArgumentException e) {
                 severe("RubinBank failed to hook Economy into Vault.");
                 if(debug){
                     e.printStackTrace();
                 }
             } catch (SecurityException e) {
                 severe("RubinBank failed to hook Economy into Vault.");
                 if(debug){
                     e.printStackTrace();
                 }
             } catch (InstantiationException e) {
                 severe("RubinBank failed to hook Economy into Vault.");
                 if(debug){
                     e.printStackTrace();
                 }
             } catch (IllegalAccessException e) {
                 severe("RubinBank failed to hook Economy into Vault.");
                 if(debug){
                     e.printStackTrace();
                 }
             } catch (InvocationTargetException e) {
                 severe("RubinBank failed to hook Economy into Vault.");
                 if(debug){
                     e.printStackTrace();
                 }
             } catch (NoSuchMethodException e) {
                 severe("RubinBank failed to hook Economy into Vault.");
                 if(debug){
                     e.printStackTrace();
                 }
             } 
         }
         else{
             warning("RubinBank not hooked into Vault: Vault not enabled.");
         }
     }
    /**
     * Vault Stuff
     */
     public void unhookEconomy(){
         vaulthooked = false;
         try {
             Bukkit.getServer().getServicesManager().unregister(Economy.class, Economy_RubinBank.class.getConstructor(Plugin.class).newInstance(vault));
         } catch (IllegalArgumentException e) {
             severe("RubinBank failed to unhook Economy from Vault.");
             if(debug){
                 e.printStackTrace();
             }
         } catch (SecurityException e) {
             severe("RubinBank failed to unhook Economy from Vault.");
             if(debug){
                 e.printStackTrace();
             }
         } catch (InstantiationException e) {
             severe("RubinBank failed to unhook Economy from Vault.");
             if(debug){
                 e.printStackTrace();
             }
         } catch (IllegalAccessException e) {
             severe("RubinBank failed to unhook Economy from Vault.");
             if(debug){
                 e.printStackTrace();
             }
         } catch (InvocationTargetException e) {
             severe("RubinBank failed to unhook Economy from Vault.");
             if(debug){
                 e.printStackTrace();
             }
         } catch (NoSuchMethodException e) {
             severe("RubinBank failed to unhook Economy from Vault.");
             if(debug){
                 e.printStackTrace();
             }
         }
     }
     /**
      * Vault Stuff
      * @return if RubinBank is hooked in Vault
      */
     public boolean isHooked(){
         return vaulthooked;
     }
     /**
      * @return the Plugin configuration
      */
     public FileConfiguration getConfig(){
         return config;
     }
     /**
      * @return the RubinBank Currency
      */
     public Currency getCurrency() {
         return rubinBankCurrency;
     }
     /**
      * @return a Reopenable Database Connection
      */
     public Reopenable getReopenable() {
         return reopenable;
     }
     /**
      * Send a Message to a player
      * @param playername the player
      * @param msg the message
      */
    public void msg(String playername, String msg) {
        if(Bukkit.getPlayer(playername) != null){
            Bukkit.getPlayer(playername).sendMessage(msg);
        }
    }
}

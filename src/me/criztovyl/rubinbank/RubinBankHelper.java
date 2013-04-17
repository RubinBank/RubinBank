package me.criztovyl.rubinbank;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Logger;

import me.criztovyl.rubinbank.Vault.Economy_RubinBank;
import me.criztovyl.rubinbank.account.AccountStatementDBSafe;
import me.criztovyl.rubinbank.bank.Bank;
import me.criztovyl.rubinbank.bankomat.Bankomats;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.mysql.MySQLHelper;
import me.criztovyl.rubinbank.tools.Tools;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public class RubinBankHelper implements PluginHelper{
        private Bank bank;
        private Logger log;
        private Calendar start, end;
        private MySQLHelper mysql;
        private Bankomats bankomats;
        private Tools tools;
        private boolean debug;
        private boolean vaulthooked;
        private Plugin vault;
        public RubinBankHelper(Logger log){
                this.log = log;
                start = Calendar.getInstance();
        }
        public void init() throws SQLException{
                mysql = new MySQLHelper(
                                "jdbc:mysql://" + Config.HostAddress(),
                                Config.HostUser(),
                                Config.HostPassword());
                bankomats = new Bankomats();
                bankomats.load();
                bank = new Bank();
                bank.load();
                AccountStatementDBSafe.checkAndEdit(mysql.getConnection());
                tools = new Tools();
                debug = Config.getConf().getBoolean("RubinBank.debug");
                vaulthooked = false;
                registerEconomy();
        }
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
        public MySQLHelper getMySQLHelper(){
                return mysql;
        }
        public Bankomats getBankomats() {
                return bankomats;
        }
        public Tools getTools(){
                return tools;
        }
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
        public boolean isHooked(){
            return vaulthooked;
        }
}

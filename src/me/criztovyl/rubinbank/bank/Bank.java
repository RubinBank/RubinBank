package me.criztovyl.rubinbank.bank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.Currency;
import me.criztovyl.rubinbank.RubinBankPlugin;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.account.AccountDBSafe;
import me.criztovyl.rubinbank.mysql.Reopenable;

import org.bukkit.ChatColor;

/**
 * Represents several accounts
 * @author criztovyl
 */
public class Bank {
    private ArrayList<Account> accounts;
    private Currency currency;
    /**
     * Create the Bank
     */
    public Bank(Currency currency){
            accounts = new ArrayList<Account>();
            this.currency = currency;
    }
    /**
     * Adds a account to this Bank<br/>
     * Only to add a Account loaded from the Database
     * @param account The account
     */
    private void addAccount(Account account){
            accounts.add(account);
    }
    /**
     * Transfer money between accounts
     * @param from The source account
     * @param to The target account
     * @param amount The amount
     * @param location The location
     * @return If the transfer was success true; otherwise false
     */
    public boolean transfer(String from, String to, double amount, String location){
            if(amount >= 0){
                    if(hasAccount(to) && hasAccount(from)){
                            if(getAccount(from).hasEnoughMoney(amount)){
                                    getAccount(from).transferOut(amount, to);
                                    getAccount(to).transferIn(amount, from);
                                    return true;
                            }
                            else{
                                    return false;
                            }
                    }
                    else{
                            return false;
                    }
            }
            else{
                    RubinBankPlugin.getHelper().msg(from, ChatColor.RED + "Du kannst nichts negatives Überweisen!");
                    return false;
            }
        }
        /**
         * Transfer money between accounts
         * @param from The source account
         * @param to The target account
         * @param amount The amount
         * @return If the transfer was success true; otherwise false
         */
        public boolean transfer(String from, String to, double amount){
                return transfer(from, to, amount, Account.RAINBOW);
        }
        /**
         * Checks if there is an account with the given owner
         * @param owner The account owner
         * @return true if there is an account with the owner, otherwise false
         */
        public boolean hasAccount(String owner){
                for(int i = 0; i < accounts.size(); i++){
                        if(accounts.get(i).getOwner().equals(owner)){
                                return true;
                        }
                }
                return false;
        }
        /**
         * @param owner The account owner
         * @return true if there is an account with the give owner, otherwise false
         */
        public Account getAccount(String owner){
                for(int i = 0; i < accounts.size(); i++){
                        if(accounts.get(i).getOwner().equals(owner)){
                                return accounts.get(i);
                        }
                }
                return null;
        }
        /**
         * Creates and adds a Account to the Bank
         * @param owner The owner of the new account
         */
        public void createAccount(String owner){
                accounts.add(new Account(owner, getCurrency()));
        }
        /**
         * @return A List of accounts this Bank is holding
         */
        public ArrayList<Account> getAccounts(){
                return accounts;
        }
        /**
         * Save all Accounts to the Database
         * @param reopenable A {@link Reopenable} Database Connection
         * @param table_accounts the table the accounts should saved to
         * @param table_statements the table the statements should saved to
         * @throws SQLException
         */
        public void save(Reopenable reopenable, String table_accounts, String table_statements) throws SQLException{
                AccountDBSafe safe = new AccountDBSafe(reopenable, table_accounts);
                for(int i = 0; i < accounts.size(); i++){
                        accounts.get(i).saveStatements(reopenable, table_statements);
                        HashMap<String, String> save = new HashMap<String, String>();
                        save.put("owner", accounts.get(i).getOwner());
                        save.put("balance", Double.toString(accounts.get(i).getBalance()));
                        safe.saveToDatabase(save);
                }
        }
        /**
         * Load all Accounts saved in the Database
         * @param reopenable a {@link Reopenable} Database Connection
         * @param table the table the accounts should be loaded from
         * @throws SQLException
         */
        public void load(Reopenable reopenable, String table) throws SQLException{
                RubinBankPlugin.getHelper().info("Load Accounts");
                AccountDBSafe safe = new AccountDBSafe(reopenable, table);
                ArrayList<HashMap<String, String>> loads = safe.loadFromDatabase();
                if(loads != null)
                for(int i = 0; i < loads.size(); i++){
                        HashMap<String, String> load = loads.get(i);
                        addAccount(new Account(load.get("owner"), Double.parseDouble(load.get("balance")), getCurrency()));
                }
        }

        /**
         * @return the currency of this Bank
         */
    public Currency getCurrency() {
        return currency;
    }
}

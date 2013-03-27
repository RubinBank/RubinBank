package me.criztovyl.rubinbank.bank;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.account.AccountDBSafe;
import me.criztovyl.rubinbank.tools.Tools;

/**
 * Represents several accounts
 * @author criztovyl
 */
public class Bank {
	private ArrayList<Account> accounts;
	public Bank(){
		accounts = new ArrayList<Account>();
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
	 * @param from
	 * @param to
	 * @param amount
	 * @return If the transfer was success true; otherwise false
	 */
	public boolean transfer(String from, String to, double amount){
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
			Tools.msg(from, ChatColor.RED + "Du kannst nichts negatives Überweisen!");
			return false;
		}
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
		accounts.add(new Account(owner));
	}
	/**
	 * @return A List of accounts this Bank is holding
	 */
	public ArrayList<Account> getAccounts(){
		return accounts;
	}
	/**
	 * Save all Accounts of this Bank to the Database
	 */
	public void save(){
		AccountDBSafe safe = new AccountDBSafe();
		for(int i = 0; i < accounts.size(); i++){
			accounts.get(i).saveStatements();
			HashMap<String, String> save = new HashMap<String, String>();
			save.put("owner", accounts.get(i).getOwner());
			save.put("balance", Double.toString(accounts.get(i).getBalance()));
			safe.saveToDatabase(save, RubinBank.getCon());
		}
	}
	/**
	 * Load all Accounts saved in the Database
	 */
	public void load(){
		RubinBank.log.info("Load Accounts");
		AccountDBSafe safe = new AccountDBSafe();
		ArrayList<HashMap<String, String>> loads = safe.loadFromDatabase(RubinBank.getCon());
		for(int i = 0; i < loads.size(); i++){
			HashMap<String, String> load = loads.get(i);
			addAccount(new Account(load.get("owner"), Double.parseDouble(load.get("balance"))));
		}
	}
}
package me.criztovyl.rubinbank.bank;

import java.util.List;

import me.criztovyl.rubinbank.account.Account;

/**
 * Represents several accounts
 * @author criztovyl
 */
public class Bank {
	private List<Account> accounts;
	public Bank(){}
	/**
	 * Adds a account to this Bank
	 * @param account The account
	 */
	public void addAccount(Account account){
		accounts.add(account);
	}
	/**
	 * Transfer money between accounts
	 * @param from
	 * @param to
	 * @param amount
	 * @return
	 */
	public boolean transfer(String from, String to, double amount){
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
}

package me.criztovyl.rubinbank.account;

import java.util.HashMap;
import java.util.List;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Statement.StatementType;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.Tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
/**
 * Represents an RubinBank account of a Player
 * @author criztovyl
 *
 */
public class Account{
	/**
	 * The account owner
	 */
	private String owner;
	/**
	 * The account balance
	 */
	private double balance;
	/**
	 * The accounts java.util.List of me.criztovyl.rubinbank.account.Statement 's
	 */
	private List<Statement> stmts;
	/**
	 * Creates an new Account.
	 * @param owner  the account owner
	 */
	public Account(String owner){
		this.owner = owner;
		this.balance = 0;
	}
	/**
	 * Creates a new account with existing statements and amount
	 * @param stmts The statements list
	 * @param owner The account owner
	 * @param amount The account amount
	 */
	public Account(List<Statement> stmts, String owner, double amount){
		this.owner = owner;
		this.balance = amount;
		this.stmts = stmts;
	}
	/**
	 * Pay in some money to the account
	 * @param amount  How much money should be payed in
	 */
	public void payIn(double amount){
		this.balance += amount;
		addStatement(new Statement(owner, StatementType.IN, amount, getBalance()));
	}
	/**
	 * Pay out money from the account
	 * @param amount How much money should be payed out
	 * @return if the has enough money true, otherwise false.
	 */
	public boolean payOut(double amount){
		if(hasEnoughMoney(amount)){
			this.balance -= amount;
			addStatement(new Statement(owner, StatementType.OUT, amount, getBalance()));
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * Checks if enough money is present
	 * @param amount  How much money should be present.
	 * @return
	 */
	public boolean hasEnoughMoney(double amount){
		return this.balance >= amount;
	}
	/**
	 * The account owner
	 * @return The account owner
	 */
	public String getOwner(){
		return owner;
	}
	/**
	 * The account amount
	 * @return  The account amount
	 */
	public double getBalance(){
		return balance;
	}
	/**
	 * Pay in money from the Inventory of the owner
	 * @param amount How much money should be payed in
	 * @return If is a player and has enough money in the Inventory true, otherwise false. 
	 */
	public boolean payInViaInv(double amount){
		Player p = Bukkit.getServer().getPlayer(owner);
		if(p != null){
			int major = (int) amount;
			int minor = (int)((Math.round((amount - (int) amount)*10.0)/10.0)*10.0);
			boolean hasmajor = p.getInventory().contains(Material.getMaterial(Config.getMajorID()), major);
			boolean hasminor = p.getInventory().contains(Material.getMaterial(Config.getMinorID()), minor);
			boolean ignoreminor;
			if(minor <= 0){
				hasminor = true;
				ignoreminor = true;
			}
			else{
				ignoreminor = false;
			}
			if(hasmajor && hasminor){
				p.getInventory().removeItem(new ItemStack(Config.getMajorID(), major));
				if(!ignoreminor)
					p.getInventory().removeItem(new ItemStack(Config.getMinorID(), minor));
				payIn(amount);
				msg(owner, ChatColor.DARK_AQUA + "Neuer Kontostand: " + getBalance());
				return true;
			}
			else{
				msg(owner, ChatColor.YELLOW + "Du hast nicht genug Items in deinem Inventar!");
				return false;
			}
		}
		else{
			RubinBank.log.severe("Can not payInViaInv: Account Owner '" + getOwner() + "' is not Player!");
			return false;
		}
	}
	/**
	 * Pay out money to the Inventory of the owner
	 * @param amount How much money should be payed out.
	 * @return If owner is a player true, otherwise false.
	 */
	public boolean payOutViaInv(double amount){
		Player p = Bukkit.getServer().getPlayer(owner);
		if(p != null){
			
		}
		else{
			RubinBank.log.severe("Can not payOutViaInv: Account Owner '" + getOwner() + "' is not Player!");
			return false;
		}
		int minor = (int)((Math.round((amount - (int) amount)*10.0)/10.0)*10.0);
		ItemStack majorStack = new ItemStack(Config.getMajorID(), (int) amount);
		ItemStack minorStack = new ItemStack(Config.getMinorID(), minor);
		HashMap<Integer, ItemStack> majorfit = new HashMap<Integer, ItemStack>();
		HashMap<Integer, ItemStack> minorfit = new HashMap<Integer, ItemStack>();
		if(minor > 0)
			minorfit = p.getInventory().addItem(minorStack);
		if((int) amount > 0){
			majorfit = p.getInventory().addItem(majorStack);
		}
		if(!majorfit.isEmpty()){
			ItemStack stack = majorfit.get(0);
			amount -=  stack.getAmount();
		}
		if(!minorfit.isEmpty()){
			ItemStack stack = minorfit.get(0);
			amount = (amount*10 - stack.getAmount())/10;
		}
		this.payOut(amount);
		msg(owner, ChatColor.DARK_AQUA + "Neuer Kontostand: " + getBalance());
		return true;
	}
	/**
	 * Add a account statement
	 * @param stmt The me.criztovyl.rubinbank.account.Statement
	 */
	public void addStatement(Statement stmt){
		stmts.add(stmt);
	}
	/**
	 * Get a java.util.List of me.criztovyl.rubinbank.Statement 's
	 * @return
	 */
	public List<Statement> getStatements(){
		return stmts;
	}
	/**
	 * Take off money for a transfer
	 * @param amount The transfer amount
	 * @param participant The transfer partner
	 * @return true if has enough money, otherwise false
	 */
	public boolean transferOut(double amount, String participant){
		if(hasEnoughMoney(amount)){
			balance -= amount;
			addStatement(new Statement(getOwner(), StatementType.TRANSFER_OUT, amount, participant, getBalance()));
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * Pay in money from a transfer
	 * @param amount The transfer amount
	 * @param participant The transfer partner
	 */
	public void transferIn(double amount, String participant){
		balance += amount;
		addStatement(new Statement(getOwner(), StatementType.TRANSFER_IN, amount, participant, getBalance()));
	}
	/**
	 * @see me.criztovyl.rubinbank.tools.Tools.msg()
	 */
	private void msg(String p_n, String msg){
		Tools.msg(p_n, msg);
	}
	/**
	 * @return A balance message
	 */
	public String getBalanceMessage(){
		return "Dein Kontostand beträgt: " + getBalance();
	}
	/**
	 * Sends the owner the balance message
	 */
	public void sendBalanceMessage(){
		msg(getOwner(), getBalanceMessage());
	}
	public void payinItemInHand(){
		Player p = Bukkit.getPlayer(getOwner());
		int id = p.getItemInHand().getTypeId();
		if(id == Config.getMajorID()){
			ItemStack stack = p.getItemInHand();
			p.getInventory().remove(stack);
			payIn(stack.getAmount());
		}
		if(id == Config.getMinorID()){
			ItemStack stack = p.getItemInHand();
			p.getInventory().remove(stack);
			payIn(stack.getAmount());
		}
	}
}

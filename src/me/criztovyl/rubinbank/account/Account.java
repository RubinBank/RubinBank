package me.criztovyl.rubinbank.account;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.DBSafe;
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
	private String owner;
	private double balance;
	private ArrayList<AccountStatement> stmts;
	/**
	 * Creates an new Account.
	 * @param owner  the account owner
	 */
	public Account(String owner){
		this.owner = owner;
		this.balance = 0;
		this.stmts = new ArrayList<AccountStatement>();
	}
	/**
	 * Creates a new account with existing statements and amount
	 * @param stmts The statements list
	 * @param owner The account owner
	 * @param amount The account amount
	 */
	public Account(ArrayList<AccountStatement> stmts, String owner, double balance){
		this.owner = owner;
		this.balance = Math.round(balance * 10)/10;
		this.stmts = stmts;
	}
	/**
	 * Creates a new account with existing amount
	 * @param owner The account owner
	 * @param balance The existing balance
	 */
	public Account(String owner, double balance){
		this.owner = owner;
		this.balance = Math.round(balance * 10)/10;
		this.stmts = new ArrayList<AccountStatement>();
	}
	/**
	 * Pay in some money to the account
	 * @param amount  How much money should be payed in
	 */
	public void payIn(double amount){
		this.balance += Math.round(amount * 10)/10;
		addStatement(new AccountStatement(owner, AccountStatementType.IN, amount, getBalance()));
	}
	/**
	 * Pay out money from the account
	 * @param amount How much money should be payed out
	 * @return if the has enough money true, otherwise false.
	 */
	public boolean payOut(double amount){
		if(hasEnoughMoney(amount)){
			this.balance -= Math.round(amount * 10)/10;
			addStatement(new AccountStatement(owner, AccountStatementType.OUT, amount, getBalance()));
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
		return this.balance >= Math.round(amount * 10.0)/10.0;
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
		return Math.round(balance)/10.0;
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
	public void addStatement(AccountStatement stmt){
		stmts.add(stmt);
	}
	/**
	 * Get a java.util.List of me.criztovyl.rubinbank.Statement 's
	 * @return
	 */
	public ArrayList<AccountStatement> getStatements(){
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
			addStatement(new AccountStatement(getOwner(), AccountStatementType.TRANSFER_OUT, amount, participant, getBalance()));
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
		addStatement(new AccountStatement(getOwner(), AccountStatementType.TRANSFER_IN, amount, participant, getBalance()));
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
	/**
	 * Pay in the money the play hold in his hand
	 */
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
	public void removeStatements(){
		stmts.clear();
	}
	public void saveStatements(){
		for(int i = 0; i < stmts.size(); i++){
			stmts.get(i).save();
		}
		stmts.clear();
	}
	/**
	 * Represents a account statement. Account statements are present until the next stop, then they will be written to the Database and will not appear here again.
	 * @author criztovyl
	 *
	 */
	public class AccountStatement{
		private String owner;
		private AccountStatementType type;
		private double actionAmount;
		private String participant;
		private double newBalance;
		private boolean hasParticipant;
		private Calendar date;
		/**
		 * A new normal Statement; No transfer
		 * @param owner The account/statement owner
		 * @param type The statement type
		 * @param actionAmount The amount of the action
		 * @param newBalance The balance after the action.
		 */
		public AccountStatement(String owner, AccountStatementType type, double actionAmount, double newBalance){
			this.owner = owner;
			this.type = type;
			this.actionAmount = actionAmount;
			this.hasParticipant = false;
			this.newBalance = newBalance;
			this.date = Calendar.getInstance();
		}
		/**
		 * A new transfer Statement
		 * @param owner The account/statement owner
		 * @param type The statement type
		 * @param actionAmount The amount of the action
		 * @param participant The transfer partner
		 * @param newBalance The account balance after the action
		 */
		public AccountStatement(String owner, AccountStatementType type, double actionAmount, String participant, double newBalance){
			this.owner = owner;
			this.type = type;
			this.actionAmount = actionAmount;
			this.hasParticipant = true;
			this.participant = participant;
			this.newBalance = newBalance;
			this.date = Calendar.getInstance();
		}
		/**
		 * The statement owner
		 */
		public String getOwner(){
			return owner;
		}
		/**
		 * @return the statement type
		 */
		public AccountStatementType getType(){
			return type;
		}
		/**
		 * @return the amount of the action
		 */
		public double getActionAmount(){
			return Math.round(actionAmount * 10)/10;
		}
		/**
		 * @return the transfer partner if was a transfer, otherwise an empty String \"\"
		 */
		public String getParticipant(){
			if(hasParticipant){
				return participant;
			}
			else{
				return "";
			}
		}
		/**
		 * @return true if the statement action was a transfer, otherwise false
		 */
		public boolean hasParticipant(){
			return hasParticipant;
		}
		/**
		 * @return the account's balance after the action
		 */
		public double getNewBalance(){
			return Math.round(newBalance * 10)/10;
		}
		/**
		 * Get the Date when the action was executed.
		 */
		public Calendar getDate(){
			return date;
		}
		public String getDateString(){
			return Integer.toString(this.date.get(Calendar.DAY_OF_MONTH)) + "-" + Integer.toString(this.date.get(Calendar.MONTH)) + "-" + Integer.toString(this.date.get(Calendar.YEAR));
		}
		public void save(){
			AccountStatementDBSafe safe = new AccountStatementDBSafe();
			HashMap<String, String> save = new HashMap<String, String>();
			save.put("owner", getOwner());
			save.put("action", getType().toString());
			save.put("participant", getParticipant());
			save.put("actionAmount", Double.toString(getActionAmount()));
			save.put("newBalance", Double.toString(getNewBalance()));
			save.put("date", getDateString());
			safe.saveToDatabase(save, RubinBank.getCon());
			
		}
	}
	public enum AccountStatementType{
		/**
		 * The money flow was into the account
		 */
		IN,
		/**
		 * The money flow was off the account
		 */
		OUT,
		/**
		 * The money flow into the account was a transfer
		 */
		TRANSFER_IN,
		/**
		 * The money flow off the account was a transfer
		 */
		TRANSFER_OUT;
	}
	public class AccountStatementDBSafe implements DBSafe{

		@Override
		public void saveToDatabase(HashMap<String, String> save, Connection con) {
			String query = "";
			try{
				Statement stmt = con.createStatement();
				query = String.format("create table if not exists %s (id int not null auto_increment primary key, owner varchar(20) not null, action varchar(20) not null," +
						" participant varchar(50), actionAmount double, newBalance double, date varchar(12))", Config.StatementsTable());
				stmt.executeUpdate(query);
				query = String.format("Insert into %s (owner, action, participant, actionAmount, newBalance, date) value('%s', %s, '%s', %s, %s, %s)", 
						Config.StatementsTable(), save.get("owner"), save.get("action"), save.get("participant"), save.get("actionamount"), save.get("newbalance"), save.get("date"));
				con.close();
			} catch(SQLException e){
				RubinBank.log.severe("Failed to save AccountStatement to the Database! Error:\n" + e.toString() + "\n@ Query \"" + query +"\"");
			}
		}

		/**
		 * AccountStatements not loaded from the Database, Statements are not used In-Game
		 * @return null; AccountStatements are not loaded from the Database
		 */
		@Override
		public ArrayList<HashMap<String, String>> loadFromDatabase(
				Connection con) {
			return null;
		}
		
	}

}



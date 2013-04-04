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
	public static String RAINBOW = "Somewhere over the Rainbow";
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
	 * @param balance The account balance
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
		this.balance = Math.round(balance * 10.0)/10.0;
		this.stmts = new ArrayList<AccountStatement>();
	}
	/**
	 * Pay in some money to the account and record the location
	 * @param amount  How much money should be payed in
	 * @param location The location
	 */
	public void payIn(double amount, String location){
		this.balance += Math.round(amount * 10.0)/10.0;
		addStatement(new AccountStatement(owner, AccountStatementType.IN, amount, getBalance(), location));
	}
	/**
	 * Pay in some money to the account
	 * @param amount How much money should be payed in
	 */
	public void payIn(double amount){
		payIn(amount, RAINBOW);
	}
	/**
	 * Pay out money from the account
	 * @param amount How much money should be payed out
	 * @param location The location
	 * @return if the has enough money true, otherwise false
	 */
	public boolean payOut(double amount, String location){
		if(hasEnoughMoney(amount)){
			this.balance -= Math.round(amount * 10.0)/10.0;
			addStatement(new AccountStatement(owner, AccountStatementType.OUT, amount, getBalance(), location));
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * Pay out money from the account
	 * @param amount How much money should be payed out
	 * @return if the has enough money true, otherwise false
	 */
	public boolean payOut(double amount){
		return payOut(amount, RAINBOW);
	}
	/**
	 * Checks if enough money is present
	 * @param amount  How much money should be present.
	 * @return If has enough money true, otherwise false
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
		return Math.round(balance*10.0)/10.0;
	}
	/**
	 * Pay in money from the Inventory of the owner and record the location
	 * @param amount How much money should be payed in
	 * @param location The location
	 * @return If is a player and has enough money in the Inventory true, otherwise false
	 */
	public boolean payInViaInv(double amount, String location){
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
				payIn(amount, location);
				msg(owner, ChatColor.DARK_AQUA + "Neuer Kontostand: " + getBalance());
				return true;
			}
			else{
				msg(owner, ChatColor.YELLOW + "Du hast nicht genug Items in deinem Inventar!");
				return false;
			}
		}
		else{
			RubinBank.getHelper().severe("Can not payInViaInv: Account Owner '" + getOwner() + "' is not Player!");
			return false;
		}
	}
	/**
	 * Pay in money from the Inventory of the owner
	 * @param amount How much money should be payed in
	 * @return If is a player and has enough money in the Inventory true, otherwise false
	 */
	public boolean payInViaInv(double amount){
		return payInViaInv(amount, RAINBOW);
	}
	/**
	 * Pay out money to the Inventory of the owner and record the location
	 * @param amount How much money should be payed out
	 * @param location The location
	 * @return If owner is a player true, otherwise false
	 */
	public boolean payOutViaInv(double amount, String location){
		Player p = Bukkit.getServer().getPlayer(owner);
		if(p != null){
			
		}
		else{
			RubinBank.getHelper().severe("Can not payOutViaInv: Account Owner '" + getOwner() + "' is not Player!");
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
		payOut(amount, location);
		msg(owner, ChatColor.DARK_AQUA + "Neuer Kontostand: " + getBalance());
		return true;
	}
	/**
	 * Pay out money to the Inventory of the owner
	 * @param amount How much money should be payed out.
	 * @return If owner is a player true, otherwise false.
	 */
	public boolean payOutViaInv(double amount){
		return payInViaInv(amount, RAINBOW);
	}
	/**
	 * Add a account statement
	 * @param stmt The me.criztovyl.rubinbank.account.Statement
	 */
	public void addStatement(AccountStatement stmt){
		stmts.add(stmt);
	}
	/**
	 * Get {@link AccountStatement}'s
	 * @return A {@link ArrayList} of {@link AccountStatement}
	 */
	public ArrayList<AccountStatement> getStatements(){
		return stmts;
	}
	/**
	 * Take off money for a transfer and record the location
	 * @param amount The transfer amount
	 * @param participant The transfer partner
	 * @param location The location
	 * @return true if has enough money, otherwise false
	 */
	public boolean transferOut(double amount, String participant, String location){
		if(hasEnoughMoney(amount)){
			balance -= amount;
			addStatement(new AccountStatement(getOwner(), AccountStatementType.TRANSFER_OUT, amount, participant, getBalance(), location));
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * Take off money for a transfer
	 * @param amount The transfer amount
	 * @param participant The transfer partner
	 * @return true if has enough money, otherwise false
	 */
	public boolean transferOut(double amount, String participant){
		return transferOut(amount, participant, RAINBOW);
	}
	/**
	 * Pay in money from a transfer
	 * @param amount The transfer amount
	 * @param location The location
	 * @param participant The transfer partner
	 */
	public void transferIn(double amount, String participant, String location){
		balance += amount;
		addStatement(new AccountStatement(getOwner(), AccountStatementType.TRANSFER_IN, amount, participant, getBalance()));
	}
	public void transferIn(double amount, String participant){
		transferIn(amount, participant, RAINBOW);
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
	 * Sends the owner a coloured balance message
	 * @param color - The {@link ChatColor} for the message
	 */
	public void sendBalanceMessage(ChatColor color){
		msg(getOwner(), color + getBalanceMessage());
	}
	/**
	 * Pay in the money the play hold in his hand
	 * @param location The location
	 */
	public void payInItemInHand(String location){
		Player p = Bukkit.getPlayer(getOwner());
		int id = p.getItemInHand().getTypeId();
		if(id == Config.getMajorID()){
			ItemStack stack = p.getItemInHand();
			p.getInventory().remove(stack);
			payIn(stack.getAmount(), location);
		}
		if(id == Config.getMinorID()){
			ItemStack stack = p.getItemInHand();
			p.getInventory().remove(stack);
			payIn(stack.getAmount()/10.0, location);
		}
	}
	public void payInItemInHand(){
		payInItemInHand(RAINBOW);
	}
	/**
	 * Clear all Statements
	 */
	public void removeStatements(){
		stmts.clear();
	}
	/**
	 * Save all Statements to the Database
	 */
	public void saveStatements(){
		RubinBank.getHelper().info("Saving Statements...");
		for(int i = 0; i < stmts.size(); i++){
			stmts.get(i).save();
		}
		RubinBank.getHelper().info("Saved Statements...Do Clean Up...");
		stmts.clear();
		RubinBank.getHelper().info("Cleaned Statements. Finished.");
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
		private String location;
		/**
		 * A new normal Statement; No transfer
		 * @param owner The account/statement owner
		 * @param type The statement type
		 * @param actionAmount The amount of the action
		 * @param newBalance The balance after the action
		 */
		public AccountStatement(String owner, AccountStatementType type, double actionAmount, double newBalance){
			this.owner = owner;
			this.type = type;
			this.actionAmount = actionAmount;
			this.hasParticipant = false;
			this.newBalance = newBalance;
			this.date = Calendar.getInstance();
			this.location = Account.RAINBOW;
		}
		/**
		 * A new normal Statement; No transfer
		 * @param owner The account/statement owner
		 * @param type The statement type
		 * @param actionAmount The amount of the action
		 * @param newBalance The balance after the action
		 * @param location The location of the action
		 */
		public AccountStatement(String owner, AccountStatementType type, double actionAmount, double newBalance, String location){
			this.owner = owner;
			this.type = type;
			this.actionAmount = actionAmount;
			this.hasParticipant = false;
			this.newBalance = newBalance;
			this.date = Calendar.getInstance();
			this.location = location;//No :(
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
			this.location = Account.RAINBOW;
		}
		/**
		 * A new transfer Statement with Location
		 * @param owner The account/statement owner
		 * @param type The statement type
		 * @param actionAmount The amount of the action
		 * @param participant The transfer partner
		 * @param newBalance The account balance after the action
		 * @param location The location of the action
		 */
		public AccountStatement(String owner, AccountStatementType type, double actionAmount, String participant, double newBalance, String location){
			this.owner = owner;
			this.type = type;
			this.actionAmount = actionAmount;
			this.hasParticipant = true;
			this.participant = participant;
			this.newBalance = newBalance;
			this.date = Calendar.getInstance();
			this.location = location;//No ;(
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
		/**
		 * @return A String including the Date the action was executed ind Day-Month-Year
		 */
		public String getDateString(){
			return Integer.toString(this.date.get(Calendar.DAY_OF_MONTH)) + "-" + Integer.toString(this.date.get(Calendar.MONTH) + 1) + "-" + Integer.toString(this.date.get(Calendar.YEAR));
		}
		public String getLocation(){
			return location;
		}
		/**
		 * Save this Statement to the Database
		 */
		public void save(){
			AccountStatementDBSafe safe = new AccountStatementDBSafe();
			HashMap<String, String> save = new HashMap<String, String>();
			save.put("owner", getOwner());
			save.put("action", getType().toString());
			save.put("participant", getParticipant());
			save.put("actionAmount", Double.toString(getActionAmount()));
			save.put("newBalance", Double.toString(getNewBalance()));
			save.put("date", getDateString());
			save.put("location", getLocation());
			safe.saveToDatabase(save, RubinBank.getCon());
		}
		/**
		 * Sets the Location the action was executed.
		 * @param location
		 */
		public void addLocation(String location){
			this.location = location;
		}
	}
	/**
	 * The type of the Account Statement
	 * @author criztovyl
	 *
	 */
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
	/**
	 * A {@link DBSafe} for Account Statements
	 * @author criztovyl
	 *
	 */
	public class AccountStatementDBSafe implements DBSafe{

		@Override
		public void saveToDatabase(HashMap<String, String> save, Connection con) {
			String query = "";
			try{
				Statement stmt = con.createStatement();
				query = String.format("create table if not exists %s (" +
						"id int not null auto_increment primary key, " +
						"owner text not null, " +
						"action text not null," +
						" participant text, " +
						"actionAmount double, " +
						"newBalance double, " +
						"date text" +
						"location text)", 
						Config.StatementsTable());
				stmt.executeUpdate(query);
				query = String.format("Insert into %s (" +
						"owner, " +
						"action, " +
						"participant, " +
						"actionAmount, " +
						"newBalance, " +
						"date," +
						"location) " +
						"value('%s', '%s', '%s', '%s', '%s', '%s', %s)", 
						Config.StatementsTable(), 
						save.get("owner"), 
						save.get("action"), 
						save.get("participant"), 
						save.get("actionAmount"), 
						save.get("newBalance"), 
						save.get("date"),
						save.get("location"));
				stmt.executeUpdate(query);
				con.close();
			} catch(SQLException e){
				RubinBank.getHelper().severe("Failed to save AccountStatement to the Database! Error:\n" + e.toString() + "\n@ Query \"" + query +"\"");
			}
		}

		/**
		 * @return null; AccountStatements are not loaded from the Database, not needed In-Game
		 */
		@Override
		public ArrayList<HashMap<String, String>> loadFromDatabase(
				Connection con) {
			return null;
		}
		
	}

}



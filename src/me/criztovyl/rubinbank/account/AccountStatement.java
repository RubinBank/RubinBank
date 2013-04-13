package me.criztovyl.rubinbank.account;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBank;

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
	private String place;
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
		this.place = Account.RAINBOW;
	}
	/**
	 * A new normal Statement; No transfer
	 * @param owner The account/statement owner
	 * @param type The statement type
	 * @param actionAmount The amount of the action
	 * @param newBalance The balance after the action
	 * @param place The place of the action
	 */
	public AccountStatement(String owner, AccountStatementType type, double actionAmount, double newBalance, String place){
		this.owner = owner;
		this.type = type;
		this.actionAmount = actionAmount;
		this.hasParticipant = false;
		this.newBalance = newBalance;
		this.date = Calendar.getInstance();
		this.place = place;//No :(
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
		this.place = Account.RAINBOW;
	}
	/**
	 * A new transfer Statement with Location
	 * @param owner The account/statement owner
	 * @param type The statement type
	 * @param actionAmount The amount of the action
	 * @param participant The transfer partner
	 * @param newBalance The account balance after the action
	 * @param place The location of the action
	 */
	public AccountStatement(String owner, AccountStatementType type, double actionAmount, String participant, double newBalance, String place){
		this.owner = owner;
		this.type = type;
		this.actionAmount = actionAmount;
		this.hasParticipant = true;
		this.participant = participant;
		this.newBalance = newBalance;
		this.date = Calendar.getInstance();
		this.place = place;//No ;(
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
	public String getPlace(){
		return place;
	}
	/**
	 * Save this Statement to the Database
	 * @throws SQLException 
	 */
	public void save() throws SQLException{
		AccountStatementDBSafe safe = new AccountStatementDBSafe();
		HashMap<String, String> save = new HashMap<String, String>();
		save.put("owner", getOwner());
		save.put("action", getType().toString());
		save.put("participant", getParticipant());
		save.put("actionAmount", Double.toString(getActionAmount()));
		save.put("newBalance", Double.toString(getNewBalance()));
		save.put("date", getDateString());
		save.put("place", getPlace());
		safe.saveToDatabase(save, RubinBank.getHelper().getMySQLHelper().getConnection());
	}
	/**
	 * Sets the place the action was executed.
	 * @param place The place
	 */
	public void addPlace(String place){
		this.place = place;
	}
}

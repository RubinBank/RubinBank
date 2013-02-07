package me.criztovyl.rubinbank.account;
/**
 * Represents a account statement
 * @author criztovyl
 *
 */
public class Statement {
	/**
	 * Represents a statements type.
	 */
	public enum StatementType{
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
	 * The statement owner
	 */
	private String owner;
	/**
	 * The statement type
	 */
	private StatementType type;
	/**
	 * The amount of the action
	 */
	private double actionAmount;
	/**
	 * The participant in the case statement is a transfer
	 */
	private String participant;
	/**
	 * The balance of the account after the action
	 */
	private double newBalance;
	/**
	 * If the action was a transfer true, otherwise false;
	 */
	private boolean hasParticipant;
	/**
	 * A new normal Statement; No transfer
	 * @param owner The account/statement owner
	 * @param type The statement type
	 * @param actionAmount The amount of the action
	 * @param newBalance The balance after the action.
	 */
	public Statement(String owner, StatementType type, double actionAmount, double newBalance){
		this.owner = owner;
		this.type = type;
		this.actionAmount = actionAmount;
		this.hasParticipant = false;
		this.newBalance = newBalance;
	}
	/**
	 * A new transfer Statement
	 * @param owner The account/statement owner
	 * @param type The statement type
	 * @param actionAmount The amount of the action
	 * @param participant The transfer partner
	 * @param newBalance The account balance after the action
	 */
	public Statement(String owner, StatementType type, double actionAmount, String participant, double newBalance){
		this.owner = owner;
		this.type = type;
		this.actionAmount = actionAmount;
		this.hasParticipant = true;
		this.participant = participant;
		this.newBalance = newBalance;
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
	public StatementType getType(){
		return type;
	}
	/**
	 * @return the amount of the action
	 */
	public double getActionAmount(){
		return actionAmount;
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
		return newBalance;
	}
}

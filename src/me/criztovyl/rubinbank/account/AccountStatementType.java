package me.criztovyl.rubinbank.account;
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

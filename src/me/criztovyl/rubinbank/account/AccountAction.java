package me.criztovyl.rubinbank.account;

public enum AccountAction {
	CREATE,
	IN,
	OUT,
	TRANSFER,
	TRANSFER_IN,
	TRANSFER_OUT;
	AccountAction opposite(AccountAction action){
		if(action.equals(IN))
			return OUT;
		if(action.equals(OUT))
			return IN;
		if(action.equals(TRANSFER_IN))
			return TRANSFER_OUT;
		if(action.equals(TRANSFER_OUT))
			return TRANSFER_IN;
		return null;
	}
}

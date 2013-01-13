package me.criztovyl.rubinbank.tools;

public enum SignType {
	CHOOSING,
	IN,
	OUT,
	AMOUNT,
	TRANSFER,
	TRANSFER_AMOUNT,
	TRANSFER_PLAYERII,
	UCP_PASS,
	BANKOMAT_LOC,
	CREATE;
	public static SignType getType(String sth){
		sth = sth.toLowerCase();
		if(sth.equals("einzahlen")){
			return IN;
		}
		if(sth.equals("auszahlen")){
			return OUT;
		}
		if(sth.equals("kontostand")){
			return AMOUNT;
		}
		if(sth.equals("überweisen")){
			return TRANSFER;
		}
		if(sth.equals("erstellen")){
			return CREATE;
		}
		return null;
	}
}

package me.criztovyl.rubinbank.bankomat;

public enum BankomatType {
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
	/**
	 * Get the BankomatType of a String (German Name)
	 * @param sth The German name
	 * @return the BankomatType
	 */
	public static BankomatType getType(String sth){
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
	public String getTypeStringGerman(){
		switch(this){
		case AMOUNT:
			return "Kontostand";
		case CREATE:
			return "Konto erstellen";
		case IN:
			return "Einzahlen";
		case OUT:
			return "Auszahlen";
		case TRANSFER:
			return "Überweisen";
		default:
			return "";
		}
	}
}

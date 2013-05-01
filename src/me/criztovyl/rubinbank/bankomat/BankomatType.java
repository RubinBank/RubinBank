package me.criztovyl.rubinbank.bankomat;

public enum BankomatType {
    /**
     * Choose what you want to do
     */
    CHOOSING,
    /**
     * pay sth in
     */
    IN,
    /**
     * pay sth out
     */
    OUT,
    /**
     * check the amount of your account
     */
    AMOUNT,
    /**
     * do a transfer
     */
    TRANSFER,
    /**
     * the transfer amount
     */
    TRANSFER_AMOUNT,
    /**
     * the player the transfer should go to
     */
    TRANSFER_PLAYERII,
    /**
     * the place of the bankomat
     */
    BANKOMAT_PLACE,
    /**
     * create a account
     */
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
    /**
     * @return the german word
     */
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

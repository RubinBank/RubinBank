package me.criztovyl.rubinbank.account;
/**
 * Account Actions
 * @author criztovyl
 *
 */
public enum AccountAction {
    /**
     * Create an account
     */
    CREATE,
    /**
     * Pay in to an account
     */
    IN,
    /**
     * Pay out from an account
     */
    OUT,
    /**
     * Make a transfer
     */
    TRANSFER,
    /**
     * money flow to the account by a transfer
     */
    TRANSFER_IN,
    /**
     * money flow off the account by a transfer
     */
    TRANSFER_OUT;
    /**
     * The opposite of an action
     * @param action the action
     * @return the opposite
     */
    AccountAction opposite(AccountAction action){
        //TODO convert to this and switch/case
        if(action.equals(IN))
            return OUT;
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

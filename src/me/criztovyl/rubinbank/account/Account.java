package me.criztovyl.rubinbank.account;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.Currency;
import me.criztovyl.rubinbank.RubinBankPlugin;
import me.criztovyl.rubinbank.mysql.Reopenable;

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
    private Currency currency;
    /**
     * The place holder for the place of a statement
     */
    public static final String RAINBOW = "Somewhere over the Rainbow";
    /**
     * Creates an new Account.
     * @param owner  the account owner
     * @param currency the currency of the account
     */
    public Account(String owner, Currency currency){
            this.owner = owner;
            this.balance = 0;
            this.stmts = new ArrayList<AccountStatement>();
            this.currency = currency;
    }
    /**
     * Creates a new account with existing statements and amount
     * @param stmts The statements list
     * @param owner The account owner
     * @param balance The account balance
     * @param currency the currency of the account
     */
    public Account(ArrayList<AccountStatement> stmts, String owner, double balance, Currency currency){
            this.owner = owner;
            this.balance = Math.round(balance * 10.0)/10.0;
            this.stmts = stmts;
            this.currency = currency;
    }
    /**
     * Creates a new account with existing amount
     * @param owner The account owner
     * @param balance The existing balance
     * @param currency the currency of the account
     */
    public Account(String owner, double balance, Currency currency){
            this.owner = owner;
            this.balance = Math.round(balance * 10.0)/10.0;
            this.stmts = new ArrayList<AccountStatement>();
            this.currency = currency;
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
                    boolean hasmajor = p.getInventory().contains(
                                            Material.getMaterial(getCurrency().getMajorID()), major
                                       );
                    boolean hasminor = p.getInventory().contains(
                                            Material.getMaterial(getCurrency().getMajorID()), minor
                                       );
                    boolean ignoreminor;
                    if(minor <= 0){
                            hasminor = true;
                            ignoreminor = true;
                    }
                    else{
                            ignoreminor = false;
                    }
                    if(hasmajor && hasminor){
                            p.getInventory().removeItem(new ItemStack(getCurrency().getMajorID(), major));
                            if(!ignoreminor)
                                    p.getInventory().removeItem(new ItemStack(getCurrency().getMinorID(), minor));
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
                    RubinBankPlugin.getHelper().severe("Can not payInViaInv: Account Owner '" + getOwner() + "' is not Player!");
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
            if(hasEnoughMoney(amount)){
                    Player p = Bukkit.getServer().getPlayer(owner);
                    if(p != null){
                            
                    }
                    else{
                            RubinBankPlugin.getHelper().severe("Can not payOutViaInv: Account Owner '" + getOwner() + "' is not Player!");
                            return false;
                    }
                    int minor = (int)((Math.round((amount - (int) amount)*10.0)/10.0)*10.0);
                    ItemStack majorStack = new ItemStack(getCurrency().getMajorID(), (int) amount);
                    ItemStack minorStack = new ItemStack(getCurrency().getMinorID(), minor);
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
            return false;
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
            RubinBankPlugin.getHelper().msg(p_n, msg);
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
            if(id == getCurrency().getMajorID()){
                    ItemStack stack = p.getItemInHand();
                    p.getInventory().remove(stack);
                    payIn(stack.getAmount(), location);
            }
            if(id == getCurrency().getMinorID()){
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
     * @param reopenable A {@link Reopenable} Database Connection
     * @param table the table the statements should be saved to
     * @throws SQLException
     */
    public void saveStatements(Reopenable reopenable, String table) throws SQLException{
            RubinBankPlugin.getHelper().info("Saving Statements...");
            for(int i = 0; i < stmts.size(); i++){
                    stmts.get(i).save(reopenable, table);
            }
            RubinBankPlugin.getHelper().info("Saved Statements...Do Clean Up...");
            stmts.clear();
            RubinBankPlugin.getHelper().info("Cleaned Statements. Finished.");
    }
    /**
     * @return the currency of this account
     */
    public Currency getCurrency() {
        return currency;
    }
}
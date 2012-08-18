package RubinBank.account;

import org.bukkit.entity.Player;

public class account {
	private static Player player;
	private static double Amount;
	public account(Player p){
		player = p;
	}
	public Player getPlayer(){
		return player;
	}
	public double getAmount(){
		return Amount;
	}
	public void incraseAccount(double i){
		Amount += i;
	}
	public void decraseAccount(double i){
		Amount -= i;
	}
	public void setAmount(double i){
		Amount = i;
	}
}

//TODO meld bankomatsign.java and bankomat.java
package RubinBank.bankomat;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import config.Config;

public class bankomatsign {
	private static double Amount; //Bank Currency
	private static int AmountMajor;
	private static int AmountMinor;
	private static boolean Out;
	private static Block Block;
	private static bankomat bankomat;
	private Config conf;
	public bankomatsign(boolean withdraw){
		Out = withdraw;
		conf = new Config();
	}
	public double getAmount(){
		Amount = AmountMajor + (AmountMinor * conf.getMinorRatio());
		return Amount;
	}
	public static int getMajorAmount(){
		return AmountMajor;
	}
	public static int getMinorAmount(){
		return AmountMinor;
	}
	public Block getBlock(){
		return Block;
	}
	public void setBlock(Block block){
		Block = block;
	}
	public static boolean isOut(){
		return Out;
	}
	public static void doAction(double amount, Player player){
		if(Out){
			bankomat.withdraw(amount, player);
		}
		else{
			//bankomat.store(amount, player);
		}
	}
	public void incraseAmount(int i){
		Amount += i;
	}
	public void decraseAmount(int i){
		Amount -= i;
	}
}

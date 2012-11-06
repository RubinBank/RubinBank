package me.criztovyl.rubinbank.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.bukkit.Location;

public class BankomatTriggers {
	private static ArrayList<Location> triggers = new ArrayList<Location>();
	private static Map<Location, Location> bankomatOfTrigger = new HashMap<Location, Location>();
	public static void addTrigger(Location bankomat, Location trigger){
		triggers.add(trigger);
		bankomatOfTrigger.put(trigger, bankomat);
	}
	public static ArrayList<Location> getTriggers(){
		return triggers;
	}
	public static Location bankomatOfTrigger(Location trigger){
		return bankomatOfTrigger.get(trigger);	
	}
	public static boolean isTrigger(Location trigger){
		return bankomatOfTrigger.containsKey(trigger);
	}
	public static void update(ArrayList<Location> newTriggers, Map<Location, Location> newBankomatOfTrigger){
		triggers = newTriggers;
		bankomatOfTrigger = newBankomatOfTrigger;
	}
	public static boolean sameBankomat(Location triggerFrom, Location triggerTo){
		if(isTrigger(triggerFrom) && isTrigger(triggerTo)){
			if(bankomatOfTrigger(triggerFrom).equals(bankomatOfTrigger(triggerTo))){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
}

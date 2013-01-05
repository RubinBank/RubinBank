package me.criztovyl.clicklesssigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;







import org.bukkit.Location;

public class ClicklessSignTriggers {
	private static ArrayList<Location> triggers = new ArrayList<Location>();
	private static ArrayList<Location> nonMultis = new ArrayList<Location>();
	private static Map<Location, Location> clicklessSignOfTrigger = new HashMap<Location, Location>();
	private static Map<Location, ClicklessSignType> nonMultiTypes = new HashMap<Location, ClicklessSignType>();
	public static void addTrigger(Location bankomat, Location trigger){
		triggers.add(trigger);
		clicklessSignOfTrigger.put(trigger, bankomat);
	}
	public static ArrayList<Location> getTriggers(){
		return triggers;
	}
	public static Location getClicklessSignOfTrigger(Location trigger){
		return clicklessSignOfTrigger.get(trigger);	
	}
	public static boolean isTrigger(Location trigger){
		return clicklessSignOfTrigger.containsKey(trigger);
	}
	public static void update(ArrayList<Location> newTriggers, ArrayList<Location> newNonMultiLocs, Map<Location, Location> newClicklessSignOfTrigger,
			Map<Location, ClicklessSignType> newNonMultiTypes){
		triggers = newTriggers;
		nonMultis = newNonMultiLocs;
		nonMultiTypes = newNonMultiTypes; 
		clicklessSignOfTrigger = newClicklessSignOfTrigger;
	}
	public static boolean isNonMulti(Location trigger){
		return nonMultis.contains(trigger);
	}
	public static boolean sameBankomat(Location triggerFrom, Location triggerTo){
		if(isTrigger(triggerFrom) && isTrigger(triggerTo)){
			if(getClicklessSignOfTrigger(triggerFrom).equals(getClicklessSignOfTrigger(triggerTo))){
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
	public static ArrayList<Location> getNonMultis() {
		return nonMultis;
	}
	public static void setNonMultis(ArrayList<Location> nonMultis) {
		ClicklessSignTriggers.nonMultis = nonMultis;
	}
	public static Map<Location, ClicklessSignType> getNonMultiTypes() {
		return nonMultiTypes;
	}
	public static void setNonMultiTypes(Map<Location, ClicklessSignType> nonMultiType) {
		ClicklessSignTriggers.nonMultiTypes = nonMultiType;
	}
	public static ClicklessSignType getNonMultiType(Location trigger){
		if(nonMultiTypes.containsKey(trigger)){
			return nonMultiTypes.get(trigger);
		}
		else{
			return null;
		}
	}
}

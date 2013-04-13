package me.criztovyl.rubinbank.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class TriggerButton{
	public static ArrayList<Location> triggerbuttons = new ArrayList<Location>();
	public static Map<Location, TriggerButtonType> triggerbuttontype = new HashMap<Location, TriggerButtonType>();
	public static boolean isTriggerButton(Location loc){
		Location triggerLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		return triggerbuttons.contains(triggerLoc);
	}
	public static void addTriggerButton(Location loc, TriggerButtonType type){
		Location triggerLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(type.equals(TriggerButtonType.AMOUNT)){
			triggerbuttons.add(triggerLoc);
			triggerbuttontype.put(triggerLoc, type);
			//MySQL_old.addTriggerButton(triggerLoc, type.toString());
		}	
		if(type.equals(TriggerButtonType.CREATE)){
			triggerbuttons.add(triggerLoc);
			triggerbuttontype.put(triggerLoc, type);
			//MySQL_old.addTriggerButton(triggerLoc, type.toString());
		}
	}
	public static TriggerButtonType getType(Location loc){
		Location triggerLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(triggerbuttontype.containsKey(triggerLoc)){
			return triggerbuttontype.get(triggerLoc);
		}
		else{
			return null;
		}
	}
	public static void updateTriggerButtons(ArrayList<Location> newTriggerButtons, Map<Location, TriggerButtonType> newTriggerButtonType){
		triggerbuttons = newTriggerButtons;
		triggerbuttontype = newTriggerButtonType;
	}
}

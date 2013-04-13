package me.criztovyl.rubinbank.bankomat;

import org.bukkit.Location;

public enum TriggerPosition{
	UP,
	DOWN;
	public Location getTrigger(Location loc){
		switch(this){
		case DOWN:
			return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
		case UP:
			return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
		}
		return null;
	}
}
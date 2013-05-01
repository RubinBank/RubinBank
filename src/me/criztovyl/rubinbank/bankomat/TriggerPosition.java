package me.criztovyl.rubinbank.bankomat;

import org.bukkit.Location;

public enum TriggerPosition{
    /**
     * the trigger is two blocks over the sign
     */
    UP,
    /**
     * the trigger is under the sign
     */
    DOWN;
    /**
     * @param loc a Location
     * @return the trigger location
     */
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
package me.criztovyl.rubinbank.tools;

import me.criztovyl.clicklesssigns.ClicklessSign;
import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.clicklesssigns.ClicklessSigns.SignPos;

import org.bukkit.Location;

public class Signs {
	public static void addSign(final Location loc, final SignType t, final SignPos pos){
		switch(t){
		case AMOUNT:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.AMOUNT);
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					switch(pos){
					case DOWN:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
					case UP:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
					}
					return null;
				}
			});
			break;
		case CHOOSING:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.CHOOSING);
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					switch(pos){
					case DOWN:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
					case UP:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
					}
					return null;
				}
			});
			break;
		case CREATE:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.CREATE);
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					switch(pos){
					case DOWN:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
					case UP:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
					}
					return null;
				}
			});
			break;
		case IN:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.IN);
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					switch(pos){
					case DOWN:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
					case UP:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
					}
					return null;
				}
			});
			break;
		case OUT:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.OUT);
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					switch(pos){
					case DOWN:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
					case UP:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
					}
					return null;
				}
			});
			break;
		case TRANSFER:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.TRANSFER);
					
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					switch(pos){
					case DOWN:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
					case UP:
						return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
					}
					return null;
				}
			});
			break;
		}
	}
}

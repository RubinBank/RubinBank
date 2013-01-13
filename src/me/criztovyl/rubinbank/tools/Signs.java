package me.criztovyl.rubinbank.tools;

import me.criztovyl.clicklesssigns.ClicklessSign;
import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.clicklesssigns.ClicklessSigns.SignPos;

import org.bukkit.Location;

public class Signs {
	public static void addSign(Location loc, SignType t, SignPos pos){
		switch(t){
		case AMOUNT:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.AMOUNT);
				}
			});
			break;
		case CHOOSING:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.CHOOSING);
				}
			});
			break;
		case CREATE:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.CREATE);
				}
			});
			break;
		case IN:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.IN);
				}
			});
			break;
		case OUT:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.OUT);
				}
			});
			break;
		case TRANSFER:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				
				@Override
				public void action(String arg0) {
					me.criztovyl.rubinbank.tools.TimeShift.addShifted(arg0, me.criztovyl.rubinbank.tools.SignType.TRANSFER);
					
				}
			});
			break;
		}
	}
}

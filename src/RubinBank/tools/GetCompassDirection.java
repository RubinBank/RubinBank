package RubinBank.tools;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class GetCompassDirection{
	public GetCompassDirection(){
		
	}
	public static String getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
		rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
		return "S";
		} else if (22.5 <= rotation && rotation < 67.5) {
		return "SW";
		} else if (67.5 <= rotation && rotation < 112.5) {
		return "W";
		} else if (112.5 <= rotation && rotation < 157.5) {
		return "NW";
		} else if (157.5 <= rotation && rotation < 202.5) {
		return "N";
		} else if (202.5 <= rotation && rotation < 247.5) {
		return "NE";
		} else if (247.5 <= rotation && rotation < 292.5) {
		return "E";
		} else if (292.5 <= rotation && rotation < 337.5) {
		return "SE";
		} else if (337.5 <= rotation && rotation < 360.0) {
		return "S";
		} else {
		return null;
		}
		}
	@Deprecated
	public static BlockFace getAntiBlockFace(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
		rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
		return BlockFace.SOUTH;
		} else if (22.5 <= rotation && rotation < 67.5) {
		return BlockFace.SOUTH_WEST;
		} else if (67.5 <= rotation && rotation < 112.5) {
		return BlockFace.WEST;
		} else if (112.5 <= rotation && rotation < 157.5) {
		return BlockFace.NORTH_WEST;
		} else if (157.5 <= rotation && rotation < 202.5) {
		return BlockFace.NORTH;
		} else if (202.5 <= rotation && rotation < 247.5) {
		return BlockFace.NORTH_EAST;
		} else if (247.5 <= rotation && rotation < 292.5) {
		return BlockFace.EAST;
		} else if (292.5 <= rotation && rotation < 337.5) {
		return BlockFace.SOUTH_EAST;
		} else if (337.5 <= rotation && rotation < 360.0) {
		return BlockFace.SOUTH;
		} else {
		return BlockFace.SELF;
		}
		}
	//TODO Starts with South, not with North
	public static BlockFace getBlockFace(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
		rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
		return BlockFace.NORTH;
		} else if (22.5 <= rotation && rotation < 67.5) {
		return BlockFace.NORTH_EAST;
		} else if (67.5 <= rotation && rotation < 112.5) {
		return BlockFace.EAST;
		} else if (112.5 <= rotation && rotation < 157.5) {
		return BlockFace.SOUTH_EAST;
		} else if (157.5 <= rotation && rotation < 202.5) {
		return BlockFace.SOUTH;
		} else if (202.5 <= rotation && rotation < 247.5) {
		return BlockFace.SOUTH_WEST;
		} else if (247.5 <= rotation && rotation < 292.5) {
		return BlockFace.WEST;
		} else if (292.5 <= rotation && rotation < 337.5) {
		return BlockFace.NORTH_WEST;
		} else if (337.5 <= rotation && rotation < 360.0) {
		return BlockFace.NORTH;
		} else {
		return null;
		}
		}
	
}

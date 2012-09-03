package RubinBank.tools;
//It looks like I don't need this class :/
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class GetCompassDirection{
	/*
	 * BlockFace:
	 * N:  45 < yaw <= 135
	 * W: 135 < yaw <= 225 
	 * S: 225 < yaw <= 315
	 * E: 315 > yaw <= 45

	 */
	public static String getCardinalDirection(Player player) {
		float yaw = player.getLocation().getYaw();
		yaw = yaw * (-1);
		player.sendMessage("Yaw: "+yaw);
		//North
		if( 45 < yaw && yaw <= 135)
			return "N";
		//West
		if(135 < yaw && yaw <= 125)
			return "W";
		//South
		if(225 > yaw && yaw <= 315)
			return "S";
		//East
		if(315 < yaw && yaw <= 45)
			return "W";
		player.sendMessage("oO Error?");
		return null;
		}
	public static String getAntiCardinalDirection(Player player){
		float yaw = player.getLocation().getYaw();
		yaw = yaw * (-1);
		//North
		if( 45 < yaw && yaw <= 135)
			return "S";
		//West
		if(135 < yaw && yaw <= 125)
			return "E";
		//South
		if(225 > yaw && yaw <= 315)
			return "N";
		//West
		if(315 < yaw && yaw <= 45)
			return "E";
		player.sendMessage("oO Error?");
		return null;
	}
	public static BlockFace getAntiBlockFace(Player player) {
		String dirct = getAntiCardinalDirection(player);
		if(dirct.equals("N"))
			return BlockFace.NORTH;
		if(dirct.equals("E"))
			return BlockFace.EAST;
		if(dirct.equals("S"))
			return BlockFace.SOUTH;
		if(dirct.equals("W"))
			return BlockFace.WEST;
		return null;
	}
	public static BlockFace getBlockFace(Player player) {
		String dirct = getCardinalDirection(player);
		player.sendMessage("Direction: "+dirct);
		if(dirct.equals("N"))
			return BlockFace.NORTH;
		if(dirct.equals("E"))
			return BlockFace.EAST;
		if(dirct.equals("S"))
			return BlockFace.SOUTH;
		if(dirct.equals("W"))
			return BlockFace.WEST;
		return null;
		}
	
}

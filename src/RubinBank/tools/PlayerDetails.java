package RubinBank.tools;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class PlayerDetails {
	private Player player;
	private ArrayList<Object> details;
	private ArrayList<String> id;
	public PlayerDetails(Player p){
		player = p;
		details = new ArrayList<Object>();
	}
	public Player getPlayer(){
		return player;
	}
	public ArrayList<Object> getDetails(){
		return details;
	}
	public void addDetail(Object o, String ID){
		int i = details.size();
		details.add(i, o);
		id.add(ID+";"+i);
	}
	public Object getDetail(String ID){
		int i = 0;
		while(i < id.size()){
			String[] string = id.get(i).split(";");
			if(string[0] == ID){
				return details.get(Integer.parseInt(string[1]));
			}
			i++;
		}
		return null;
	}
}

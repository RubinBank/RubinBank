package RubinBank.tools;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.util.ArrayList;

import RubinBank.RubinBank;

public class ConfigComment {
	private static File file;
	private ArrayList<String> lines;
	//private FileOutputStream fo;
	private FileInputStream fs;
	private BufferedReader br;
	//private BufferedWriter bw;
	public ConfigComment(File f){
		file = f;
		if(file.exists()){
			try{
				fs = new FileInputStream(file);
				//fo = new FileOutputStream(file);
				//br = new BufferedReader(new InputStreamReader(fs));
				//bw = new BufferedWriter(new OutputStreamWriter(fo));
			}
			catch(Exception e){
				RubinBank.log.severe("Exception at RubinBank.tools.ConfigComment:\n"+e.toString());
			}
			String Line;
			try {
				while((Line = br.readLine()) != null){
					lines.add(Line);
				}
				fs.close();
			} catch (IOException e) {
				RubinBank.log.severe("Exception at RubinBank.tools.ConfigComment:\n"+e.toString());
			}
		}
		else{
			RubinBank.log.warning("Exception at RubinBank.tools.ConfigComment:\n");
		}
	}
	public static File getFile(){
		return file;
	}
	public boolean addComment(String s, String after){
		int i = 0;
		ArrayList<String> newLines = new ArrayList<String>();
		while(i < lines.size()){
			if(lines.get(i) == after){
				int j = 0;
				while(j < lines.size() || j <= i){
					newLines.add(lines.get(j));
					j++;
				}
				newLines.add(s);
				while(j < lines.size()){
					newLines.add(lines.get(j));
					j++;
				}
			}
			i++;
		}
		return true;
	}
}

package me.criztovyl.rubinbank;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

import me.criztovyl.rubinbank.account.AccountStatementDBSafe;
import me.criztovyl.rubinbank.bank.Bank;
import me.criztovyl.rubinbank.bankomat.Bankomats;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.mysql.MySQLHelper;
import me.criztovyl.rubinbank.tools.SignArgStore;

public class RubinBankHelper implements PluginHelper{
	private Bank bank;
	private Logger log;
	private HashMap<String, SignArgStore> signArgsStores;
	private Calendar start, end;
	private MySQLHelper mysql;
	private Bankomats bankomats;
	public RubinBankHelper(Logger log){
		this.log = log;
		start = Calendar.getInstance();
	}
	public void init() throws SQLException{
		mysql = new MySQLHelper(
				"jdbc:mysql://" + Config.HostAddress(),
				Config.HostUser(),
				Config.HostPassword());
		bankomats = new Bankomats();
		bankomats.load();
		bank = new Bank();
		bank.load();
		signArgsStores = new HashMap<String, SignArgStore>();
		AccountStatementDBSafe.checkAndEdit(mysql.getConnection());
	}
	public Bank getBank(){
		return bank;
	}
	public void createSignArgStore(String p_n){
		signArgsStores.put(p_n, new SignArgStore());
	}
	public void addSignArgStore(String p_n, SignArgStore store){
		signArgsStores.put(p_n, store);
	}
	public SignArgStore getSignArgStore(String p_n){
		return signArgsStores.get(p_n);
	}
	@Override
	public void info(String msg) {
		log.info(msg);
	}
	@Override
	public void severe(String msg) {
		log.severe(msg);
	}
	@Override
	public String getLifeTimeString() {
		return "RubinBank lives since " + getSimpleLifeTimeString();
	}
	@Override
	public String getSimpleLifeTimeString() {
		end = Calendar.getInstance();
		int start_h = start.get(Calendar.HOUR_OF_DAY);
		int start_m = start.get(Calendar.MINUTE);
		int start_s = start.get(Calendar.SECOND);
		int start_ms = start.get(Calendar.MILLISECOND);
		int end_h = end.get(Calendar.HOUR_OF_DAY);
		int end_m = end.get(Calendar.MINUTE);
		int end_s = end.get(Calendar.SECOND);
		int end_ms = end.get(Calendar.MILLISECOND);
		String lifeTime = String.format("%d:%d:%d:%d (h:m:s:ms)", 
				end_h - start_h,
				end_m - start_m,
				end_s - start_s,
				end_ms - start_ms);
		return lifeTime;
	}
	@Override
	public void warning(String msg) {
		log.warning(msg);
	}
	public MySQLHelper getMySQLHelper(){
		return mysql;
	}
	public Bankomats getBankomats() {
		return bankomats;
	}
}

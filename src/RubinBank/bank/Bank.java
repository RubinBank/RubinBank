package RubinBank.bank;



public class Bank {
	private static String Name;
	private static int Major; 
	private static int Minor;
	private static double Amount;// Bank Currency (Major + Minor in Major)
	public Bank(){
		
	}
	public static boolean setName(String Name){
		Bank.Name = Name;
		return true;
	}
	public static int getMajorAmount(){
		return Major;
	}
	public static int getMinorAmount(){
		return Minor;
	}
	public static double getAmount(){
		return Amount;
	}
	public static String getName(){
		return Name;
	}
	/*public static double withdraw(double amount, Player player){
		account acc = getAccount(player);
		double accamount = acc.getAmount();
		if(accamount > 0 && accamount - amount > 0){
			acc.decraseAccount(amount);
			return amount;
		}
		else{
			player.sendMessage("Du hast nicht genug Geld!");
			return -1;
		}
	}
	public static double store(double amount, Player player){
		account acc = getAccount(player);
		acc.incraseAccount(amount);
		return amount;
		
	}
	public static account getAccount(Player p){
		if(accounts.contains(p)){
			int i = 0;
			while(i < accounts.size()){
				if(accounts.get(i).getPlayer() == p){
					return accounts.get(i);
				}
			}
		}
		return null;
	}
	//TODO use DataBase(MySQL)
	public void save(){
		if(!SaveFile.exists()){
			SaveFile.mkdirs();
		}
		s.addDefault("ArrayLists", accounts.size());
		int i = 0;
		while(i < accounts.size()){
			if(i == accounts.size())
				break;
			s.addDefault("ArrayList."+i+".playername", accounts.get(i).getPlayer().getName());
			s.addDefault("ArrayList."+i+".amount", accounts.get(i).getAmount());
			i++;
		}
		Date date = new Date();
		File backup = new File("plugins"+sep+"RubinBank"+sep+"Bank"+sep+"Backup"+sep+date.getTime()+"backup.yml");
		s.addDefault("BackupFileName", backup.getName());
		try {
			s.save(SaveFile);
			s.save(backup);
		} catch (IOException e) {
			RubinBank.log.severe("Exception at Bank.save!\n"+e.toString());
			Bukkit.getServer().broadcastMessage(ChatColor.RED+"ATTENTION: BANK.save exception!\n\t"+e.toString());
		}
		
	}
	//TODO use Database(MySQL)
	public static void load(){
		Date date1 = new Date();
		if(!SaveFile.exists()){
			RubinBank.log.warning("It looks like you have no Bank Save.");
			Date date2 = new Date();
			long time = date2.getTime() - date1.getTime();
			RubinBank.log.info("Save not loaded("+time+" ms)");
		}
		else{
			try {
				s.load(SaveFile);
			} catch (FileNotFoundException e) {
				RubinBank.log.severe("Exception at RubinBank.bank.Bank s.Load()(Branch SV.e/load):\n"+e.toString());
			} catch (IOException e) {
				RubinBank.log.severe("Exception at RubinBank.bank.Bank s.Load()(Branch SV.e/load):\n"+e.toString());
			} catch (InvalidConfigurationException e) {
				RubinBank.log.severe("Exception at RubinBank.bank.Bank s.Load()(Branch SV.e/load):\n"+e.toString());
			}
			accounts = new ArrayList<account>();
			int i = 0;
			int j = s.getInt("ArrayLists");
			while(i < j){
				if(i == j)
					break;
				account acc = new account(Bukkit.getPlayer(s.getString("ArrayList."+i+".playername")));
				acc.setAmount(s.getDouble("ArrayList."+i+".amount"));
				accounts.add(acc);
				i++;
			}
			Date date2 = new Date();
			long time = date2.getTime() - date1.getTime();
			RubinBank.log.info("Loaded Save ("+time+" ms)");
		}
	}*/
}

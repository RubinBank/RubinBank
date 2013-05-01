package me.criztovyl.rubinbank.config;

public enum Config {
    MYSQL_HOST("MySQL.Host.Address"),
    MYSQL_PORT("MySQL.Host.Port"),
    MYSQL_USER("MySQL.Host.User"),
    MYSQL_PASSWORD("MySQL.Host.Password"),
    MYSQL_DATABASE("MySQL.Host.Database"),
    ACCOUNTS("MySQL.Host.Table_Accounts"),
    BANKOMATS("MySQL.Host.Table_Bankomats"),
    STATEMENTS("MySQL.Host.Table_Statements"),
    DEBUG("RubinBank.debug"),
    USEWORLDGUARD("enabled.WorldGuard"),
    USERUBINBANK("enabled.RubinBank"),
    MAJORID("Currency.Major.ItemID"),
    MINORID("Currency.Minor.ItemID"),
    MAJORSINGULAR("Currency.Name.Major.Singular"),
    MINORSINGULAR("Currency.Name.Minor.Singular"),
    MAJORPLURAL("Currency.Name.Major.Plural"),
    MINORUSEPLURAL("Currency.Name.Minor.usePlural"),
    MINORPLURAL("Curreny.Name.Minor.Plural"),
    COMMANDSLIMITEDTOREGION("WorldGuardOptions.commandsLimitedToRegion"),
    COMMANDLIMITREGION("WorldGuard.limitToRegionWithParent");
    private Config(String text){
        this.text = text;
    }
    private String text;
    public String getPath(){
        return this.text;
    }
}

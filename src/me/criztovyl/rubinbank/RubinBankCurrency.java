package me.criztovyl.rubinbank;

import me.criztovyl.rubinbank.config.Config;

import org.bukkit.configuration.file.FileConfiguration;

public class RubinBankCurrency implements Currency{

    private FileConfiguration config;
    /**
     * The RubinBank Currency
     * @param config the configuration with the currency values
     */
    public RubinBankCurrency(FileConfiguration config){
        this.config = config;
    }
    @Override
    public int getMajorID() {
        return config.getInt(Config.MAJORID.getPath());
    }

    @Override
    public int getMinorID() {
        return config.getInt(Config.MINORID.getPath());
    }

    @Override
    public String getMajorSingular() {
        return config.getString(Config.MAJORSINGULAR.getPath());
    }

    @Override
    public String getMinorSingular() {
        return config.getString(Config.MINORSINGULAR.getPath());
    }

    @Override
    public String getMajorPlural() {
        return config.getString(Config.MAJORPLURAL.getPath());
    }

    @Override
    public String getMinorPlural() {
        return config.getString(Config.MINORPLURAL.getPath());
    }

}

package me.criztovyl.rubinbank.Vault;

import java.util.List;

import me.criztovyl.rubinbank.RubinBankPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Economy_RubinBank implements Economy {

        public Economy_RubinBank(Plugin plugin){
            
        }
        private EconomyResponse failure =  new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "RubinBank has no banks.");
        @Override
        public EconomyResponse bankBalance(String arg0) {
                return failure;
        }

        @Override
        public EconomyResponse bankDeposit(String arg0, double arg1) {
                return failure;
        }

        @Override
        public EconomyResponse bankHas(String arg0, double arg1) {
                return failure;
        }

        @Override
        public EconomyResponse bankWithdraw(String arg0, double arg1) {
                return failure;
        }

        @Override
        public EconomyResponse createBank(String arg0, String arg1) {
                return failure;
        }

        @Override
        public boolean createPlayerAccount(String arg0) {
                RubinBankPlugin.getHelper().getBank().createAccount(arg0);
                return true;
        }

        @Override
        public boolean createPlayerAccount(String arg0, String arg1) {
                return createPlayerAccount(arg0);
        }

        @Override
        public String currencyNamePlural() {
                return RubinBankPlugin.getHelper().getCurrency().getMajorPlural();
        }

        @Override
        public String currencyNameSingular() {
                return RubinBankPlugin.getHelper().getCurrency().getMajorSingular();
        }

        @Override
        public EconomyResponse deleteBank(String arg0) {
                return failure;
        }

        @Override
        public EconomyResponse depositPlayer(String arg0, double arg1) {
                if(hasAccount(arg0)){
                        if(has(arg0, arg1)){
                                RubinBankPlugin.getHelper().getBank().getAccount(arg0).payOut(arg1, "Vault");
                                return new EconomyResponse(arg1, getBalance(arg0), ResponseType.SUCCESS, "");
                        }
                        else{
                                return new EconomyResponse(arg1, getBalance(arg0), ResponseType.FAILURE, "Has not enugth money!");
                        }
                }
                else{
                        return new EconomyResponse(arg1, 0, ResponseType.FAILURE, "Has no account");
                }
        }

        @Override
        public EconomyResponse depositPlayer(String arg0, String arg1, double arg2) {
                return depositPlayer(arg0, arg2);
        }

        @Override
        public String format(double arg0) {
                if(arg0 == 1){
                        return String.format("%f %s", arg0, currencyNameSingular());
                }
                else{
                        return String.format("%f %s", arg0, currencyNamePlural());
                }
        }

        @Override
        public int fractionalDigits() {
                return 1;
        }

        @Override
        public double getBalance(String arg0) {
                if(hasAccount(arg0)){
                        return RubinBankPlugin.getHelper().getBank().getAccount(arg0).getBalance();
                }
                return 0;
        }

        @Override
        public double getBalance(String arg0, String arg1) {
                return getBalance(arg0);
        }

        @Override
        public List<String> getBanks() {
                return null;
        }

        @Override
        public String getName() {
                return "RubinBank";
        }

        @Override
        public boolean has(String arg0, double arg1) {
                if(hasAccount(arg0)){
                        return RubinBankPlugin.getHelper().getBank().getAccount(arg0).hasEnoughMoney(arg1);
                }
                return false;
        }

        @Override
        public boolean has(String arg0, String arg1, double arg2) {
                return has(arg0, arg2);
        }

        @Override
        public boolean hasAccount(String arg0) {
                return RubinBankPlugin.getHelper().getBank().hasAccount(arg0);
        }

        @Override
        public boolean hasAccount(String arg0, String arg1) {
                return hasAccount(arg0);
        }

        @Override
        public boolean hasBankSupport() {
                return false;
        }

        @Override
        public EconomyResponse isBankMember(String arg0, String arg1) {
                return failure;
        }

        @Override
        public EconomyResponse isBankOwner(String arg0, String arg1) {
                return failure;
        }

        @Override
        public boolean isEnabled() {
                return Bukkit.getServer().getPluginManager().isPluginEnabled("RubinBank");
        }

        @Override
        public EconomyResponse withdrawPlayer(String arg0, double arg1) {
                if(hasAccount(arg0)){
                        RubinBankPlugin.getHelper().getBank().getAccount(arg0).payIn(arg1, "Vault");
                        return new EconomyResponse(arg1, getBalance(arg0), ResponseType.SUCCESS, "");
                }
                else{
                        return new EconomyResponse(arg1, 0, ResponseType.FAILURE, "Has no account!");
                }
        }

        @Override
        public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
                return withdrawPlayer(arg0, arg2);
        }

}

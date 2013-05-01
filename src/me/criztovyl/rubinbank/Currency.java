package me.criztovyl.rubinbank;
/**
 * A Currency
 * @author criztovyl
 *
 */
public interface Currency {
    /**
     * @return the Item ID of the Major Item
     */
    public int getMajorID();
    /**
     * @return the Item ID of the Minor Plugin
     */
    public int getMinorID();
    /**
     * @return the Name of Major Item Singular
     */
    public String getMajorSingular();
    /**
     * @return the Name of Minor Item Singular
     */
    public String getMinorSingular();
    /**
     * @return the Name of Major Item Plural
     */
    public String getMajorPlural();
    /**
     * @return the Name of MInor Item Plural
     */
    public String getMinorPlural();
}

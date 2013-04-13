package me.criztovyl.rubinbank.bankomat;


public interface Bankomat_I {
	/**
	 * Register the Sign @ ClicklessPlugin
	 */
	public void createSign();
	/**
	 * Remove the Sign
	 */
	public void removeSign();
	/**
	 * @return If sign was newly created 
	 */
	public boolean isNew();
	/**
	 * @return If sign was deleted
	 */
	public boolean deleted();
}

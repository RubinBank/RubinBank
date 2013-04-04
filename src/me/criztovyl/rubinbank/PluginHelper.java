package me.criztovyl.rubinbank;

public interface PluginHelper {
	/**
	 * Logs an "[INFO]" Message
	 * @param msg
	 */
	public void info(String msg);
	/**
	 * Logs a "[WARNING]" Message
	 * @param msg
	 */
	public void warning(String msg);
	/**
	 * Logs a "[SEVERE]" Message
	 * @param msg
	 */
	public void severe(String msg);
	/**
	 * @return "[PluginName] live since XX:XX:XX:XX (h:m:s:ms)."
	 */
	public String getLifeTimeString();
	/**
	 * @return "XX:XX:XX:XX (h:m:s:ms)"
	 */
	public String getSimpleLifeTimeString();
}

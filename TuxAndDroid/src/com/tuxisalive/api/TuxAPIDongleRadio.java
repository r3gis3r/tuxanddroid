package com.tuxisalive.api;

/**
 * Class to interact with the connection/disconnection of the radio/dongle.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIDongleRadio
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	private TuxEventHandlers eventHandlers;
	private TuxEventHandler eventHandler;
	/*
	 * Status field
	 */
	private String pStStName;
	
	/**
	 * Constructor of the class.
	 * 
	 * @param parent parent object as TuxAPI.
	 * @param stStName status name.
	 */
	public TuxAPIDongleRadio(TuxAPI parent, String stStName)
	{
		pStStName = stStName;
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
		eventHandler = (TuxEventHandler)eventHandlers.getEventHandler(pStStName);
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy() {}
	
	/**
	 * Get the state of the radio/dongle connection.
	 * 
	 * @return the state of the radio/dongle connection.
	 */
	public Boolean getConnected()
	{
		Object[] result = pParent.status.requestOne(pStStName);
		if (result[0] == null)
		{
			return false;
		}
		if (result[0].equals("True"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Wait until the radio/dongle was connected.
	 * 
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitConnected(Double timeout)
	{
		if (getConnected())
		{
			return true;
		}
		return eventHandler.waitCondition(timeout, "True", null);
	}
	
	/**
	 * Wait until the radio/dongle was disconnected.
	 * 
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitDisconnected(Double timeout)
	{
		if (!getConnected())
		{
			return true;
		}
		return eventHandler.waitCondition(timeout, "False", null);
	}
	
	/**
	 * Register a callback on the connected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnConnected(Object sender, String method)
	{
		return registerEventOnConnected(sender, method, -1);
	}
	
	/**
	 * Register a callback on the connected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnConnected(Object sender, String method, Integer idx)
	{
		return eventHandler.register(idx, sender, method, "True", null);
	}
	
	/**
	 * Unregister a callback from the connected event.
	 * 
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnConnected(Integer idx)
	{
		eventHandler.unregister(idx);
	}
	
	/**
	 * Register a callback on the disconnected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnDisconnected(Object sender, String method)
	{
		return registerEventOnDisconnected(sender, method, -1);
	}
	
	/**
	 * Register a callback on the disconnected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnDisconnected(Object sender, String method, Integer idx)
	{
		return eventHandler.register(idx, sender, method, "False", null);
	}
	
	/**
	 * Unregister a callback from the disconnected event.
	 * 
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnDisconnected(Integer idx)
	{
		eventHandler.unregister(idx);
	}
}

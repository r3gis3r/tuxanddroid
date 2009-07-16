package com.tuxisalive.api;

/**
 * Class to control the state of a switch.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPISwitch
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	private TuxEventHandlers eventHandlers;
	
	/*
	 * Private field
	 */
	private String switchStName;
	
	/**
	 * Constructor of the class.
	 * @param parent parent object as TuxAPI
	 */
	public TuxAPISwitch(TuxAPI parent, String switchStName)
	{
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
		this.switchStName = switchStName;
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy() {}
	
	/**
	 * Return the state of the switch.
	 * @return the state of the switch.
	 */
	public Boolean getState()
	{
		Object result[] = pParent.status.requestOne(switchStName);
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
	 * Wait until the switch was pressed.
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitPressed(Double timeout)
	{
		if (getState())
		{
			return true;
		}
		return eventHandlers.waitCondition(switchStName, timeout, "True", null);
	}
	
	/**
	 * Wait until the switch was released.
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitReleased(Double timeout)
	{
		if (!getState())
		{
			return true;
		}
		return eventHandlers.waitCondition(switchStName, timeout, "False", null);
	}
	
	/**
	 * Register a callback on the pressed event.
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnPressed(Object sender, String method, Integer idx)
	{
		return eventHandlers.register(switchStName, idx, sender, method, "True", null);
	}
	
	/**
	 * Register a callback on the pressed event.
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnPressed(Object sender, String method)
	{
		return registerEventOnPressed(sender, method, -1);
	}
	
	/**
	 * Unregister a callback from the pressed event.
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnPressed(Integer idx)
	{
		eventHandlers.unregister(switchStName, idx);
	}
	
	/**
	 * Register a callback on the released event.
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnReleased(Object sender, String method, Integer idx)
	{
		return eventHandlers.register(switchStName, idx, sender, method, "False", null);
	}
	
	/**
	 * Register a callback on the released event.
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnReleased(Object sender, String method)
	{
		return registerEventOnReleased(sender, method, -1);
	}
	
	/**
	 * Unregister a callback from the released event.
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnReleased(Integer idx)
	{
		eventHandlers.unregister(switchStName, idx);
	}
}

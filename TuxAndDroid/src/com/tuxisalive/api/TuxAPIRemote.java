package com.tuxisalive.api;

/**
 * Class to control the state of a switch.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIRemote
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	private TuxEventHandlers eventHandlers;
	
	/**
	 * Constructor of the class.
	 * @param parent parent object as TuxAPI
	 */
	public TuxAPIRemote(TuxAPI parent)
	{
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy() {}
	
	/**
	 * Return the state of the switch.
	 * @return the state of the switch.
	 */
	public String getState()
	{
		Object result[] = pParent.status.requestOne(TuxAPIConst.ST_NAME_REMOTE_BUTTON);
		return (String)result[0];
	}
	
	/**
	 * Wait until the remote was pressed.
	 * @param timeout maximal delay to wait.
	 * @param key: key to wiat.
	 * @return the state of the wait result.
	 */
	public Boolean waitPressed(Double timeout, String key)
	{
		if (getState() == key)
		{
			return true;
		}
		return eventHandlers.waitCondition(TuxAPIConst.ST_NAME_REMOTE_BUTTON, 
				timeout, key, null);
	}
	
	/**
	 * Wait until the remote was released.
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitReleased(Double timeout)
	{
		if (getState() == TuxAPIConst.K_RELEASED)
		{
			return true;
		}
		return eventHandlers.waitCondition(TuxAPIConst.ST_NAME_REMOTE_BUTTON, 
				timeout, TuxAPIConst.K_RELEASED, null);
	}
	
	/**
	 * Register a callback on the pressed event.
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param key remote key.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnPressed(Object sender, String method, 
			String key, Integer idx)
	{
		return eventHandlers.register(TuxAPIConst.ST_NAME_REMOTE_BUTTON, idx, 
				sender, method, key, null);
	}
	
	/**
	 * Register a callback on the pressed event.
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param key remote key.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnPressed(Object sender, String method, 
			String key)
	{
		return eventHandlers.register(TuxAPIConst.ST_NAME_REMOTE_BUTTON, -1, 
				sender, method, key, null);
	}
	
	/**
	 * Unregister a callback from the pressed event.
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnPressed(Integer idx)
	{
		eventHandlers.unregister(TuxAPIConst.ST_NAME_REMOTE_BUTTON, idx);
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
		return eventHandlers.register(TuxAPIConst.ST_NAME_REMOTE_BUTTON, idx, sender, 
				method, TuxAPIConst.K_RELEASED, null);
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
		eventHandlers.unregister(TuxAPIConst.ST_NAME_REMOTE_BUTTON, idx);
	}
}

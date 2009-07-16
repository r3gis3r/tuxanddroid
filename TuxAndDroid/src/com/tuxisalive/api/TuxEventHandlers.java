package com.tuxisalive.api;

import java.util.*;

/**
 * TuxEventsHandler is a container of TuxEventHandler objects.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 * @see TuxEventHandler
 */
public class TuxEventHandlers
{
	private Hashtable<Object,Object> eventHt;
	
	/**
	 * Constructor of class.
	 */
	public TuxEventHandlers()
	{
		eventHt = new Hashtable<Object,Object>();
		this.insert("all");
	}
	
	/**
	 * Destructor of class.
	 */
	public void destroy()
	{
		for (Enumeration<Object> e = eventHt.elements(); e.hasMoreElements(); )
		{
			TuxEventHandler currEH = (TuxEventHandler)e.nextElement();
			currEH.destroy();
		}
		eventHt.clear();
	}
	
	/**
	 * Create and insert a new event handler in the container.
	 * 
	 * @param eventName	name of the new event handler.
	 */
	public Boolean insert(String eventName)
	{
		if (!eventHt.containsKey(eventName))
		{
			TuxEventHandler newEH = new TuxEventHandler();
			eventHt.put(eventName, newEH);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Get an event handler by its name.
	 * 
	 * @param eventName	name of the handler.
	 * @return	a TuxEventHandler object or null if the event name was not found.
	 */
	public TuxEventHandler getEventHandler(String eventName)
	{
		if (eventHt.containsKey(eventName))
		{
			TuxEventHandler currEH = (TuxEventHandler)eventHt.get(eventName);
			return currEH;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Register a callback function to an event handler.
	 * 
	 * @param 	eventName	name of the event handler.
	 * @param 	sender	parent object of the method.
	 * @param 	method	method name as string.
	 * @return	the index of the callback in the handler.
	 * @see TuxEventHandler#register(Object, String)
	 */
	public Integer register(String eventName, Object sender, String method)
	{
		Object[] condition = new Object[0];
		
		return register(eventName, sender, method, condition);
	}
	
	/**
	 * Register a callback function to an event handler.
	 * 
	 * @param 	eventName	name of the event handler.
	 * @param 	sender		parent object of the method.
	 * @param 	method		method name as string.
	 * @param 	condition	object array of the condition.
	 * @return	the index of the callback in the handler.
	 * @see TuxEventHandler#register(Object, String, Object...)
	 */
	public Integer register(String eventName, Object sender, String method, 
			Object... condition)
	{
		return register(eventName, -1, sender, method, condition);
	}
	
	/**
	 * Register another callback at the place of a previous index.
	 * 
	 * @param 	eventName	name of the event handler.
	 * @param	idx			the index of the callback
	 * @param 	sender		parent object of the method.
	 * @param 	method		method name as string.
	 * @param 	condition	object array of the condition.
	 * @return	the index of the callback in the handler.
	 * @see TuxEventHandler#register(Integer, Object, String, Object...)
	 */
	public Integer register(String eventName, Integer idx, Object sender, 
			String method, Object... condition)
	{
		if (!eventHt.containsKey(eventName))
		{
			return -1;
		}
		else
		{
			TuxEventHandler currEH = (TuxEventHandler)eventHt.get(eventName);
			return currEH.register(idx, sender, method, condition);
		}
	}
	
	/**
	 * Unregister a callback from an event handler.
	 * 
	 * @param 	eventName	name of the event handler.
	 * @param idx	index of the callback.
	 * @see TuxEventHandler#unregister
	 */
	public void unregister(String eventName, Integer idx)
	{
		if (eventHt.containsKey(eventName))
		{
			TuxEventHandler currEH = (TuxEventHandler)eventHt.get(eventName);
			currEH.unregister(idx);
		}
	}
	
	/**
	 * This method store in a stack the configuration of the linked callbacks in
	 * the event handlers.
	 * @see TuxEventHandler#storeContext()
	 */
	public void storeContext()
	{
		for (Enumeration<Object> e = eventHt.elements(); e.hasMoreElements(); )
		{
			TuxEventHandler currEH = (TuxEventHandler)e.nextElement();
			currEH.storeContext();
		}
	}
	
	/**
	 * This method restore from a stack the configuration of the linked callbacks in
	 * the event handlers.
	 * @see TuxEventHandler#restoreContext()
	 */
	public void restoreContext()
	{
		for (Enumeration<Object> e = eventHt.elements(); e.hasMoreElements(); )
		{
			TuxEventHandler currEH = (TuxEventHandler)e.nextElement();
			currEH.restoreContext();
		}
	}
	
	/**
	 * This method clears the configuration of the linked callbacks in the
	 * event handlers.
	 * @see TuxEventHandler#clearContext()
	 */
	public void clearContext()
	{
		for (Enumeration<Object> e = eventHt.elements(); e.hasMoreElements(); )
		{
			TuxEventHandler currEH = (TuxEventHandler)e.nextElement();
			currEH.clearContext();
		}
	}
	
	/**
	 * Emit a signal on the event handler with a set of parameters.
	 * 
	 * @param eventName	name of the handler.
	 * @param args		parameters.
	 * @see TuxEventHandler#emit(Object...)
	 */
	public void emit(String eventName, Object...args)
	{
		Object[] aArgs = new Object[args.length + 1];
		
		aArgs[0] = eventName;
		for (int i = 0; i < args.length; i++)
		{
			aArgs[i + 1] = args[i];
		}
		
		if (eventHt.containsKey(eventName))
		{
			TuxEventHandler currEH = (TuxEventHandler)eventHt.get(eventName);
			currEH.emit(args);
			TuxEventHandler currEH1 = (TuxEventHandler)eventHt.get("all");
			currEH1.emit(aArgs);
		}
	}
	
	/**
	 * Emit a signal on the event handler with a set of parameters.
	 * 
	 * @param eventName	name of the handler.
	 * @param args	parameters.
	 * @see TuxEventHandler#notify()
	 */
	public void notify(String eventName, Object...args)
	{
		Object[] aArgs = new Object[args.length + 1];
		
		aArgs[0] = eventName;
		for (int i = 0; i < args.length; i++)
		{
			aArgs[i + 1] = args[i];
		}
		
		if (eventHt.containsKey(eventName))
		{
			TuxEventHandler currEH = (TuxEventHandler)eventHt.get(eventName);
			currEH.emit(args);
			TuxEventHandler currEH1 = (TuxEventHandler)eventHt.get("all");
			currEH1.emit(aArgs);
		}
	}
	
	/**
	 * Synchronize a condition with a specific event.
	 * 
	 * @param eventName		name of the handler.
	 * @param timeout		maximal delay to wait.
	 * @param condition		object array of the condition.
	 * @return	the success of the waiting.
	 * @see TuxEventHandler#waitCondition(Double, Object...)
	 */
	public Boolean waitCondition(String eventName, Double timeout, 
			Object... condition)
	{
		if (eventHt.containsKey(eventName))
		{
			TuxEventHandler currEH = (TuxEventHandler)eventHt.get(eventName);
			return currEH.waitCondition(timeout, condition);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Clear all pending wait.
	 * @see TuxEventHandler#clearPending()
	 */
	public void clearPending()
	{
		for (Enumeration<Object> e = eventHt.elements(); e.hasMoreElements(); )
		{
			TuxEventHandler currEH = (TuxEventHandler)e.nextElement();
			currEH.clearPending();
		}
	}
}

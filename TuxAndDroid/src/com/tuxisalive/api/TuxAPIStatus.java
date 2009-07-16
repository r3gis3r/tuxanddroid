package com.tuxisalive.api;

import java.util.Hashtable;

/**
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIStatus 
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	
	/**
	 * Constructor of the class.
	 */
	public TuxAPIStatus(TuxAPI parent)
	{
		pParent = parent;
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy() {}
	
	/**
	 * Get the value and delay of a status.
	 * 
	 * @param statusName name of the status.
	 * @return an array with the value and the delay.
	 */
	public Object[] requestOne(String statusName)
	{
		Object[] result = new Object[2];
		Hashtable<Object,Object> varStruct = new Hashtable<Object,Object>();
		Hashtable<Object,Object> varResult = new Hashtable<Object,Object>();
		String cmd;
		
		cmd = String.format("status/request_one?status_name=%s", statusName);
		varStruct.put("value", "data0.value");
		varStruct.put("delay", "data0.delay");
		result[0] = null;
		result[1] = null;
		
		// Request
		if (pParent.server.request(cmd, varStruct, varResult, false))
		{
			result[0] = varResult.get("value");
			result[1] = varResult.get("delay");
		}
		return result;
	}
	
	/**
	 * 
	 * @param statusName name of the status.
	 * @param timeout maximal delay to wait.
	 * @param condition object array of the condition.
	 * @return the success of the waiting.
	 */
	public Boolean wait(String statusName, Double timeout, 
			Object... condition)
	{
		return pParent.event.handler.waitCondition(statusName, timeout, condition);
	}
}

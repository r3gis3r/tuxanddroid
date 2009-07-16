package com.tuxisalive.api;

import java.util.*;

/**
 * Class to control the resource access. When your have your level to
 * CLIENT_LEVEL_RESTRICTED, you need to acquiring and releasing the resource.
 * It mechanism is needed for the synchronization of the resource access by the
 * programs which want using Tuxdroid.
 * CLIENT_LEVEL_FREE, CLIENT_LEVEL_ROOT and CLIENT_LEVEL_ANONYME don't have
 * this restriction.
 * When you make a tux gadget, you must to use the CLIENT_LEVEL_RESTRICTED level.
 * (Only by convention ;) )
 *  
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIAccess
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	
	/**
	 * Constructor of the class.
	 */
	public TuxAPIAccess(TuxAPI parent)
	{
		pParent = parent;
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy() {}
	
	/*
	 * 
	 */
	private Boolean cmdSimpleResult(String cmd)
	{
		Hashtable<Object,Object> varStruct = new Hashtable<Object,Object>();
		Hashtable<Object,Object> varResult = new Hashtable<Object,Object>();
		
		return pParent.server.request(cmd, varStruct, varResult, false);
	}
	
	/**
	 * To acquiring the resource access.
	 * Need for CLIENT_LEVEL_RESTRICTED level.
	 * Don't forget to release the access after !!!
	 *  
	 * @param priorityLevel (ACCESS_PRIORITY_LOW|ACCESS_PRIORITY_NORMAL|
	 * 						 ACCESS_PRIORITY_HIGH|ACCESS_PRIORITY_CRITICAL)
	 * @return the success of the acquiring.
	 */
	public Boolean acquire(Integer priorityLevel)
	{
		String cmd;
		cmd = String.format("access/acquire?priority_level=%d", priorityLevel);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * To acquiring the resource access.
	 * Need for CLIENT_LEVEL_RESTRICTED level.
	 * Don't forget to release the access after !!!
	 * 
	 * @return the success of the acquiring.
	 */
	public Boolean acquire()
	{
		return acquire(TuxAPIConst.ACCESS_PRIORITY_NORMAL);
	}
	
	/**
	 * Wait that the resource can be acquired.
	 * Need for CLIENT_LEVEL_RESTRICTED level.
	 * Don't forget to release the access after !!!
	 *  
	 * @param timeout maximal delay to wait.
	 * @param priorityLevel (ACCESS_PRIORITY_LOW|ACCESS_PRIORITY_NORMAL|
	 * 						 ACCESS_PRIORITY_HIGH|ACCESS_PRIORITY_CRITICAL)
	 * @return the success of the acquiring.
	 */
	public Boolean waitAcquire(Double timeout, Integer priorityLevel)
	{
		Double tBegin = System.currentTimeMillis() / 1000.0;
		Double currentTime;
		
		while (!acquire(priorityLevel))
		{
			currentTime = System.currentTimeMillis() / 1000.0;
			if ((currentTime - tBegin) >= timeout)
			{
				return false;
			}
			TuxAPIMisc.sleep(0.25);
		}
		return true;
	}
	
	/**
	 * Wait that the resource can be acquired.
	 * Need for CLIENT_LEVEL_RESTRICTED level.
	 * Don't forget to release the access after !!!
	 *  
	 * @param timeout maximal delay to wait.
	 * @return the success of the acquiring.
	 */
	public Boolean waitAcquire(Double timeout)
	{
		return waitAcquire(timeout, TuxAPIConst.ACCESS_PRIORITY_NORMAL);
	}
	
	/**
	 * To releasing the resource access.
	 * Need for CLIENT_LEVEL_RESTRICTED level.
	 * 
	 * @return the success of the command.
	 */
	public Boolean release()
	{
		String cmd = "access/release?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * To force the acquisition of the resource by a specified client.
	 * Only available for CLIENT_LEVEL_ROOT level.
	 * 
	 * @param idClient idx of the client.
	 * @return the success of the command.
	 */
	public Boolean forcingAcquire(Integer idClient)
	{
		String cmd;
		cmd = String.format("access/forcing_acquire?id_client=%d", idClient);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * To force the releasing of the resource.
	 * Only available for CLIENT_LEVEL_ROOT level.
	 * 
	 * @return the success of the command.
	 */
	public Boolean forcingRelease()
	{
		String cmd = "access/forcing_release?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * To lock the resource access. After it, nobody will can acquiring
	 * the resource.
	 * Only available for CLIENT_LEVEL_ROOT level.
	 * 
	 * @return the success of the command.
	 */
	public Boolean lock()
	{
		String cmd = "access/lock?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * To unlock the resource access.
	 * Only available for CLIENT_LEVEL_ROOT level.
	 * 
	 * @return the success of the command.
	 */
	public Boolean unlock()
	{
		String cmd = "access/unlock?null=true";
		return cmdSimpleResult(cmd);
	}
}

package com.tuxisalive.api;

import java.util.Hashtable;

/**
 * Class to control the movements of a body part (mouth or eyes).
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIMouthEyes {
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	private TuxEventHandlers eventHandlers;
	/*
	 * Status field
	 */
	private String pPositionStName;
	private String pMvmRemStName;
	private String pPartName;

	/**
	 * Constructor of the class.
	 * 
	 * @param parent parent object as TuxAPI.
	 * @param positionStName
	 * @param mvmRemStName
	 * @param partName
	 */
	public TuxAPIMouthEyes(TuxAPI parent, String positionStName, String mvmRemStName,
			String partName)
	{
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
		pPositionStName = positionStName;
		pMvmRemStName = mvmRemStName;
		pPartName = partName;
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
	 * Open this body part.
	 * 
	 * @return the success of the command.
	 */
	public Boolean open()
	{
		String cmd;
		
		cmd = String.format("%s/open?null=true", pPartName);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Close this body part.
	 * 
	 * @return the success of the command.
	 */
	public Boolean close()
	{
		String cmd;
		
		cmd = String.format("%s/close?null=true", pPartName);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Stop the movement of this body part.
	 * 
	 * @return the success of the command.
	 */
	public Boolean off()
	{
		String cmd;
		
		cmd = String.format("%s/off?null=true", pPartName);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Move this body part.
	 * (asynchronous)
	 * 
	 * @param count number of movements.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_OPEN|SSV_CLOSE)
	 * @return the success of the command.
	 */
	public Boolean onAsync(Integer count, String finalState)
	{
		String cmd;
		
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_MOUTHEYES_POSITIONS,
				finalState))
		{
			return false;
		}
		
		cmd = String.format("%s/on?count=%d&final_state=%s", pPartName, 
				count, finalState);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Move this body part.
	 * 
	 * @param count number of movements.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_OPEN|SSV_CLOSE)
	 * @return the success of the command.
	 */
	public Boolean on(Integer count, String finalState)
	{
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_MOUTHEYES_POSITIONS,
				finalState))
		{
			return false;
		}
		Double timeout = count * 1.0;
		Boolean ret = onAsync(count, finalState);
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return ret;
		}
		if (ret)
		{
			ret = waitMovingOff(timeout);
		}
		return ret;
	}
	
	/**
	 * Move this body part during a number of seconds.
	 * (asynchronous)
	 * 
	 * @param duration duration time in seconds.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_OPEN|SSV_CLOSE)
	 * @return the success of the command.
	 */
	public Boolean onDuringAsync(Double duration, String finalState)
	{
		String cmd;
		
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_MOUTHEYES_POSITIONS,
				finalState))
		{
			return false;
		}
		
		cmd = String.format("%s/on_during?duration=%s&final_state=%s", pPartName, 
				duration.toString(), finalState);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Move this body part during a number of seconds.
	 * (asynchronous)
	 * 
	 * @param duration duration time in seconds.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_OPEN|SSV_CLOSE)
	 * @return the success of the command.
	 */
	public Boolean onDuring(Double duration, String finalState)
	{
		Double timeout = duration * 2.0;
		Boolean ret = onDuringAsync(duration, finalState);
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return ret;
		}
		if (ret)
		{
			ret = waitMovingOff(timeout);
		}
		return ret;
	}
	
	/**
	 * Get the position of the body part.
	 * 
	 * @return (SSV_NDEF|SSV_OPEN|SSV_CLOSE)
	 */
	public String getPosition()
	{
		Object[] result = pParent.status.requestOne(pPositionStName);
		if (result[0] == null)
		{
			return TuxAPIConst.SSV_NDEF;
		}
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_MOUTHEYES_POSITIONS,
				(String)result[0]))
		{
			return TuxAPIConst.SSV_NDEF;
		}
		else
		{
			return (String)result[0];
		}
	}
	
	/**
	 * Get the moving state of this body part.
	 * 
	 * @return a boolean.
	 */
	public Boolean getMovingState()
	{
		Object[] result = pParent.status.requestOne(pMvmRemStName);
		if (result[0] == null)
		{
			return false;
		}
		if (result[0].equals("0"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * Wait that this body part don't move.
	 * 
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitMovingOff(Double timeout)
	{
		return eventHandlers.waitCondition(pMvmRemStName, timeout,
				"0", null);
	}
	
	/**
	 * Wait a specific position of this body part.
	 * 
	 * @param position position to wait.
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitPosition(String position, Double timeout)
	{
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_MOUTHEYES_POSITIONS,
				position))
		{
			return false;
		}
		if (getPosition().equals(position))
		{
			return true;
		}
		return eventHandlers.waitCondition(pPositionStName, timeout,
				position, null);
	}
}

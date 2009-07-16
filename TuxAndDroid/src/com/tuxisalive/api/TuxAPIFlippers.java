package com.tuxisalive.api;

import java.util.Hashtable;

/**
 * Class to control the flippers movements.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIFlippers
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
	public TuxAPIFlippers(TuxAPI parent)
	{
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
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
	
	/*
	 * 
	 */
	private Integer checkSpeed(Integer speed)
	{
		if (speed < TuxAPIConst.SPV_VERYSLOW)
		{
			speed = TuxAPIConst.SPV_VERYSLOW;
		}
		if (speed > TuxAPIConst.SPV_VERYFAST)
		{
			speed = TuxAPIConst.SPV_VERYFAST;
		}
		return speed;
	}
	
	/**
	 * Set the flippers to up.
	 * 
	 * @return the success of the command.
	 */
	public Boolean up()
	{
		String cmd;
		
		cmd = "flippers/up?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Set the flippers to down.
	 * 
	 * @return the success of the command.
	 */
	public Boolean down()
	{
		String cmd;
		
		cmd = "flippers/down?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Stop the movement of flippers.
	 * 
	 * @return the success of the command.
	 */
	public Boolean off()
	{
		String cmd;
		
		cmd = "flippers/off?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Set the speed of the flippers movement.
	 * 
	 * @param speed speed of the movement.
	 * @return the success of the command.
	 */
	public Boolean setSpeed(Integer speed)
	{
		speed = checkSpeed(speed);
        String cmd = String.format("flippers/speed?value=%d", speed);
        return cmdSimpleResult(cmd);
	}
	
	/**
	 * Move the flippers.
	 * (asynchronous)
	 * 
	 * @param count number of movements.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @param speed speed of the movement.
	 * @return the success of the command.
	 */
	public Boolean onAsync(Integer count, String finalState, Integer speed)
	{
		String cmd;
		
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_FLIPPERS_POSITIONS,
				finalState))
		{
			return false;
		}
		
		cmd = String.format("flippers/on?count=%d&final_state=%s", count, finalState);
		Boolean ret = cmdSimpleResult(cmd);
		if (ret)
		{
			ret = setSpeed(speed);
		}
		return ret;
	}
	
	/**
	 * Move the flippers.
	 * (asynchronous)
	 * 
	 * @param count number of movements.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @return the success of the command.
	 */
	public Boolean onAsync(Integer count, String finalState)
	{
		return onAsync(count, finalState, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the flippers.
	 * 
	 * @param count number of movements.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @param speed speed of the movement.
	 * @return the success of the command.
	 */
	public Boolean on(Integer count, String finalState, Integer speed)
	{
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_FLIPPERS_POSITIONS,
				finalState))
		{
			return false;
		}
		Double timeout = count * 1.0;
		Boolean ret = onAsync(count, finalState, speed);
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
	 * Move the flippers.
	 * 
	 * @param count number of movements.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @return the success of the command.
	 */
	public Boolean on(Integer count, String finalState)
	{
		return on(count, finalState, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the flippers during a number of seconds.
	 * (asynchronous)
	 * 
	 * @param duration duration time in seconds.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @param speed speed of the movement.
	 * @return the success of the command.
	 */
	public Boolean onDuringAsync(Double duration, String finalState, Integer speed)
	{
		String cmd;
		
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_FLIPPERS_POSITIONS,
				finalState))
		{
			return false;
		}
		
		cmd = String.format("flippers/on_during?duration=%s&final_state=%s", 
				duration.toString(), finalState);
		Boolean ret = cmdSimpleResult(cmd);
		if (ret)
		{
			ret = setSpeed(speed);
		}
		return ret;
	}
	
	/**
	 * Move the flippers during a number of seconds.
	 * (asynchronous)
	 * 
	 * @param duration duration time in seconds.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @return the success of the command.
	 */
	public Boolean onDuringAsync(Double duration, String finalState)
	{
		return onDuringAsync(duration, finalState, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the flippers during a number of seconds.
	 * 
	 * @param duration duration time in seconds.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @param speed speed of the movement.
	 * @return the success of the command.
	 */
	public Boolean onDuring(Double duration, String finalState, Integer speed)
	{
		Double timeout = duration * 2.0;
		Boolean ret = onDuringAsync(duration, finalState, speed);
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
	 * Move the flippers during a number of seconds.
	 * 
	 * @param duration duration time in seconds.
	 * @param finalState requested state after the movement.
	 * 						(SSV_NDEF|SSV_UP|SSV_DOWN)
	 * @return the success of the command.
	 */
	public Boolean onDuring(Double duration, String finalState)
	{
		return onDuring(duration, finalState, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Get the position of the flippers.
	 * 
	 * @return (SSV_NDEF|SSV_UP|SSV_DOWN)
	 */
	public String getPosition()
	{
		Object[] result = pParent.status.requestOne(TuxAPIConst.ST_NAME_FLIPPERS_POSITION);
		if (result[0] == null)
		{
			return TuxAPIConst.SSV_NDEF;
		}
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_FLIPPERS_POSITIONS,
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
	 * Get the moving state of the flippers.
	 * 
	 * @return a boolean.
	 */
	public Boolean getMovingState()
	{
		Object[] result = pParent.status.requestOne(TuxAPIConst.ST_NAME_FLIPPERS_MOTOR_ON);
		if (result[0] == null)
		{
			return false;
		}
		if (result[0].equals("False"))
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
		return eventHandlers.waitCondition(TuxAPIConst.ST_NAME_FLIPPERS_MOTOR_ON, timeout,
				"False", null);
	}
	
	/**
	 * Wait a specific position of the flippers.
	 * 
	 * @param position position to wait.
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitPosition(String position, Double timeout)
	{
		if (!TuxAPIMisc.stringInStringArray(TuxAPIConst.SSV_FLIPPERS_POSITIONS,
				position))
		{
			return false;
		}
		if (getPosition().equals(position))
		{
			return true;
		}
		return eventHandlers.waitCondition(TuxAPIConst.ST_NAME_FLIPPERS_POSITION, timeout,
				position, null);
	}
}

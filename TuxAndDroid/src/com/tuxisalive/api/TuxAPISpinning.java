package com.tuxisalive.api;

import java.util.Hashtable;

/**
 * Class to control the spinning movements.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPISpinning 
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
	public TuxAPISpinning(TuxAPI parent)
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
	 * Set the speed of rotation.
	 * 
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean setSpeed(Integer speed)
	{
		speed = checkSpeed(speed);
        String cmd = String.format("spinning/speed?value=%d", speed);
        return cmdSimpleResult(cmd);
	}
	
	/**
	 * Stop the spinning movement.
	 * 
	 * @return the success of the command.
	 */
	public Boolean off()
	{
		String cmd;
		
		cmd = "spinning/off?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Move the robot to the left. 
	 * (asynchronous)
	 * 
	 * @param turns number of turns.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean leftOnAsync(Double turns, Integer speed)
	{
		turns *= 4;
		Integer count = turns.intValue();
		if (count == 0)
		{
			count = 1;
		}
		if (count > 255)
		{
			count = 255;
		}
		String cmd = String.format("spinning/left_on?count=%d", count);
		Boolean ret = cmdSimpleResult(cmd);
		if (ret)
		{
			ret = setSpeed(speed);
		}
		return ret;
	}
	
	/**
	 * Move the robot to the left. 
	 * (asynchronous)
	 * 
	 * @param turns number of turns.
	 * @return the success of the command.
	 */
	public Boolean leftOnAsync(Double turns)
	{
		return leftOnAsync(turns, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the robot to the right. 
	 * (asynchronous)
	 * 
	 * @param turns number of turns.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean rightOnAsync(Double turns, Integer speed)
	{
		turns *= 4;
		Integer count = turns.intValue();
		if (count == 0)
		{
			count = 1;
		}
		if (count > 255)
		{
			count = 255;
		}
		String cmd = String.format("spinning/right_on?count=%d", count);
		Boolean ret = cmdSimpleResult(cmd);
		if (ret)
		{
			ret = setSpeed(speed);
		}
		return ret;
	}
	
	/**
	 * Move the robot to the right. 
	 * (asynchronous)
	 * 
	 * @param turns number of turns.
	 * @return the success of the command.
	 */
	public Boolean rightOnAsync(Double turns)
	{
		return rightOnAsync(turns, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the robot to the left. 
	 * 
	 * @param turns number of turns.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean leftOn(Double turns, Integer speed)
	{
		Double timeout = turns * 5.0;
		Boolean ret = leftOnAsync(turns, speed);
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return ret;
		}
		if (ret)
		{
			ret = waitLeftMovingOff(timeout);
		}
		return ret;
	}
	
	/**
	 * Move the robot to the left. 
	 * 
	 * @param turns number of turns.
	 * @return the success of the command.
	 */
	public Boolean leftOn(Double turns)
	{
		return leftOn(turns, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the robot to the right. 
	 * 
	 * @param turns number of turns.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean rightOn(Double turns, Integer speed)
	{
		Double timeout = turns * 5.0;
		Boolean ret = rightOnAsync(turns, speed);
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return ret;
		}
		if (ret)
		{
			ret = waitRightMovingOff(timeout);
		}
		return ret;
	}
	
	/**
	 * Move the robot to the right. 
	 * 
	 * @param turns number of turns.
	 * @return the success of the command.
	 */
	public Boolean rightOn(Double turns)
	{
		return rightOn(turns, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the robot to the left during a number of seconds. 
	 * (asynchronous)
	 * 
	 * @param duration duration of the rotation.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean leftOnDuringAsync(Double duration, Integer speed)
	{
		String cmd = String.format("spinning/left_on_during?duration=%s",
				duration.toString());
		Boolean ret = cmdSimpleResult(cmd);
		if (ret)
		{
			ret = setSpeed(speed);
		}
		return ret;
	}
	
	/**
	 * Move the robot to the left during a number of seconds. 
	 * (asynchronous)
	 * 
	 * @param duration duration of the rotation.
	 * @return the success of the command.
	 */
	public Boolean leftOnDuringAsync(Double duration)
	{
		return leftOnDuringAsync(duration, TuxAPIConst.SPV_VERYFAST);
	}

	/**
	 * Move the robot to the right during a number of seconds. 
	 * (asynchronous)
	 * 
	 * @param duration duration of the rotation.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean rightOnDuringAsync(Double duration, Integer speed)
	{
		String cmd = String.format("spinning/right_on_during?duration=%s",
				duration.toString());
		Boolean ret = cmdSimpleResult(cmd);
		if (ret)
		{
			ret = setSpeed(speed);
		}
		return ret;
	}
	
	/**
	 * Move the robot to the right during a number of seconds. 
	 * (asynchronous)
	 * 
	 * @param duration duration of the rotation.
	 * @return the success of the command.
	 */
	public Boolean rightOnDuringAsync(Double duration)
	{
		return rightOnDuringAsync(duration, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the robot to the left during a number of seconds.
	 * 
	 * @param duration duration of the rotation.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean leftOnDuring(Double duration, Integer speed)
	{
		Double timeout = duration * 2.0;
		Boolean ret = leftOnDuringAsync(duration, speed);
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return ret;
		}
		if (ret)
		{
			ret = waitLeftMovingOff(timeout);
		}
		return ret;
	}

	/**
	 * Move the robot to the left during a number of seconds.
	 * 
	 * @param duration duration of the rotation.
	 * @return the success of the command.
	 */
	public Boolean leftOnDuring(Double duration)
	{
		return leftOnDuring(duration, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Move the robot to the right during a number of seconds.
	 * 
	 * @param duration duration of the rotation.
	 * @param speed speed of the rotation.
	 * @return the success of the command.
	 */
	public Boolean rightOnDuring(Double duration, Integer speed)
	{
		Double timeout = duration * 2.0;
		Boolean ret = rightOnDuringAsync(duration, speed);
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return ret;
		}
		if (ret)
		{
			ret = waitRightMovingOff(timeout);
		}
		return ret;
	}

	/**
	 * Move the robot to the right during a number of seconds.
	 * 
	 * @param duration duration of the rotation.
	 * @return the success of the command.
	 */
	public Boolean rightOnDuring(Double duration)
	{
		return rightOnDuring(duration, TuxAPIConst.SPV_VERYFAST);
	}
	
	/**
	 * Get the left rotation state of the robot.
	 * 
	 * @return a boolean.
	 */
	public Boolean getLeftMovingState()
	{
		Object[] result = pParent.status.requestOne(TuxAPIConst.ST_NAME_SPIN_LEFT_MOTOR_ON);
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
	 * Wait that the robot don't turn to the left.
	 * 
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitLeftMovingOff(Double timeout)
	{
		return eventHandlers.waitCondition(TuxAPIConst.ST_NAME_SPIN_LEFT_MOTOR_ON, timeout,
				"False", null);
	}
	
	/**
	 * Get the right rotation state of the robot.
	 * 
	 * @return a boolean.
	 */
	public Boolean getRightMovingState()
	{
		Object[] result = pParent.status.requestOne(TuxAPIConst.ST_NAME_SPIN_RIGHT_MOTOR_ON);
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
	 * Wait that the robot don't turn to the right.
	 * 
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitRightMovingOff(Double timeout)
	{
		return eventHandlers.waitCondition(TuxAPIConst.ST_NAME_SPIN_RIGHT_MOTOR_ON, timeout,
				"False", null);
	}

}

package com.tuxisalive.api;

import java.util.*;

/**
 * Class to use the 8K 8Bit Mono wave files with Tuxdroid.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIWav
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
	public TuxAPIWav(TuxAPI parent)
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
	
	/**
	 * Play a wave file.
	 * (Asynchronous)
	 * 
	 * @param waveFile wave file to play.
	 * @param begin start seconds.
	 * @param end stop seconds.
	 * @return the success of the command.
	 */
	public Boolean playAsync(String waveFile, Double begin, Double end)
	{
		String cmd = String.format("wav/play?path=%s&begin=%s&end=%s", 
				waveFile, begin.toString(), end.toString());
		
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Play a wave file.
	 * (Asynchronous)
	 * 
	 * @param waveFile wave file to play.
	 * @return the success of the command.
	 */
	public Boolean playAsync(String waveFile)
	{
		return playAsync(waveFile, 0., 0.);
	}
	
	/**
	 * Play a wave file.
	 * 
	 * @param waveFile wave file to play.
	 * @param begin start seconds.
	 * @param end stop seconds.
	 * @return the success of the command.
	 */
	public Boolean play(String waveFile, Double begin, Double end)
	{
		Integer channel;
		
		if (!playAsync(waveFile, begin, end))
		{
			return false;
		}
		
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return true;
		}
		
		Boolean ret = eventHandlers.waitCondition(TuxAPIConst.ST_NAME_WAV_CHANNEL_START, 
				1.0, null, null);
		
		if (!ret)
		{
			return false;
		}
		Object result[] = pParent.status.requestOne(TuxAPIConst.ST_NAME_WAV_CHANNEL_START);
		
		try
		{
			channel = Integer.valueOf((String)result[0]);
		}
		catch (Exception e)
		{
			return false;
		}
		System.out.println(channel);
		String endStName = TuxAPIConst.WAV_CHANNELS_NAME_LIST[channel];
		result = pParent.status.requestOne(endStName);
		
		if (((String)result[0]).equals("OFF"))
		{
			eventHandlers.waitCondition(endStName, 1.0, "ON", null);
		}
		
		return eventHandlers.waitCondition(endStName, 99999999., "OFF", null);
	}
	
	/**
	 * Play a wave file.
	 * 
	 * @param waveFile wave file to play.
	 * @return the success of the command.
	 */
	public Boolean play(String waveFile)
	{
		return play(waveFile, 0., 0.);
	}
	
	/**
	 * Stop the current wave file.
	 * 
	 * @return the success of the command.
	 */
	public Boolean stop()
	{
		String cmd = "wav/stop?null=true";
		
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Set the pause state of the wave player.
	 * 
	 * @param value paused or not paused.
	 * @return the success of the command.
	 */
	public Boolean setPause(Boolean value)
	{
		String cmd;
		
		if (value)
		{
			cmd = "wav/pause?value=True";
		}
		else
		{
			cmd = "wav/pause?value=False";
		}
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Set the current played wave to paused.
	 * 
	 * @return the success of the command.
	 */
	public Boolean setPause()
	{
		return setPause(true);
	}
}

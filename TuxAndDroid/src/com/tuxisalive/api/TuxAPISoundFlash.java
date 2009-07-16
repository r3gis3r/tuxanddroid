package com.tuxisalive.api;

import java.util.*;

/**
 * Class to play and manages the internal sounds from the
 * flash memory.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPISoundFlash
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
	public TuxAPISoundFlash(TuxAPI parent)
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
	 * Play a sound from the internal memory.
	 * (Asynchronous)
	 * @param track index of the sound.
	 * @param volume (0.0 .. 100.0)
	 * @return the success of the command.
	 */
	public Boolean playAsync(Integer track, Double volume)
	{
		String cmd = String.format("sound_flash/play?track=%d&volume=%s", track,
				volume.toString());
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Play a sound from the internal memory.
	 * (Asynchronous)
	 * @param track index of the sound.
	 * @return the success of the command.
	 */
	public Boolean playAsync(Integer track)
	{
		return playAsync(track, 100.0);
	}
	
	/**
	 * Play a sound from the internal memory.
	 * @param track index of the sound.
	 * @param volume (0.0 .. 100.0)
	 * @return the success of the command.
	 */
	public Boolean play(Integer track, Double volume)
	{
		if (playAsync(track, volume))
		{
			if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
			{
				return true;
			}
			String trackName = String.format("TRACK_%03d", track);
			eventHandlers.waitCondition(TuxAPIConst.ST_NAME_AUDIO_FLASH_PLAY, 0.2, 
					trackName, null);
			Object result[] = pParent.status.requestOne(TuxAPIConst.ST_NAME_AUDIO_FLASH_PLAY);
			if (result[0].equals(trackName))
			{
				eventHandlers.waitCondition(TuxAPIConst.ST_NAME_AUDIO_FLASH_PLAY, 70.0, 
						"STOP", null);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Play a sound from the internal memory.
	 * @param track index of the sound.
	 * @return the success of the command.
	 */
	public Boolean play(Integer track)
	{
		return play(track, 100.0);
	}
	
	/**
	 * Reflash the sound flash memory.
	 * Only available for CLIENT_LEVEL_RESTRICTED and CLIENT_LEVEL_ROOT
	 * levels.
	 * @param wavList wave file path list.
	 * @return (SOUND_REFLASH_NO_ERROR|SOUND_REFLASH_ERROR_PARAMETERS|
	 * 			SOUND_REFLASH_ERROR_RF_OFFLINE|SOUND_REFLASH_ERROR_WAV|
	 * 			SOUND_REFLASH_ERROR_USB)
	 */
	public String reflash(String ...wavList)
	{
		if (wavList.length <= 0)
		{
			return TuxAPIConst.SOUND_REFLASH_ERROR_PARAMETERS;
		}
		String tracks = "";
		
		for (int i = 0; i < wavList.length; i++)
		{
			tracks = String.format("%s%s|", tracks, wavList[i]);
		}
		
		tracks = tracks.substring(0, tracks.length() - 1);
		
		String cmd = String.format("sound_flash/reflash?tracks=%s", tracks);
		if (!cmdSimpleResult(cmd))
		{
			return TuxAPIConst.SOUND_REFLASH_ERROR_PARAMETERS;
		}
		else
		{
			if (eventHandlers.waitCondition(TuxAPIConst.ST_NAME_SOUND_REFLASH_END, 5.0, 
					TuxAPIConst.SSV_NDEF, null))
			{
				eventHandlers.waitCondition(TuxAPIConst.ST_NAME_SOUND_REFLASH_END, 150.0, 
						null, null);
			}
			Object result[] = pParent.status.requestOne(TuxAPIConst.ST_NAME_SOUND_REFLASH_END);
			return (String)result[0];
		}		
	}
}

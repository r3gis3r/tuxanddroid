package com.tuxisalive.api;

import java.util.*;
import java.net.URLEncoder;

/**
 * Class to use the text to speech engine.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPITTS
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	private TuxEventHandlers eventHandlers;
	
	/*
	 * Private field
	 */
	@SuppressWarnings("unused")
	private String encoding = "latin-1";
	private String locutor = "Ryan";
	private Integer pitch = 100;
	private String currText, currLocutor;
	private Integer currPitch;
	private SLockedStack syncStackLock, asyncStackLock;
	
	/**
	 * Constructor of the class.
	 * @param parent parent object as TuxAPI
	 */
	public TuxAPITTS(TuxAPI parent)
	{
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
		syncStackLock = new SLockedStack();
		asyncStackLock = new SLockedStack();
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
	@SuppressWarnings("unchecked")
	private List<String> cmdVoiceList()
	{
		List<String> result = new ArrayList<String>();
		Hashtable<Object,Object> varStruct = new Hashtable<Object,Object>();
		Hashtable<Object,Object> varResult = new Hashtable<Object,Object>();
		if (pParent.server.request("tts/voices?null=true", varStruct, varResult, false))
		{
			Integer dataCount = (Integer)varResult.get("data_count");
			for (int i = 0; i < dataCount; i++)
			{
				String dataName = String.format("data%d", i);
				Hashtable<Object,Object> data = (Hashtable)varResult.get(dataName);
				String locFName = (String)data.get("locutor");
				locFName = locFName.substring(0, locFName.length() - 2);
				result.add(locFName);
			}
		}
		return result;
	}
	
	/**
	 * Set the pitch of the locutor.
	 * @param value pitch (50 .. 200)
	 */
	public void setPitch(Integer value)
	{
		this.pitch = value;
	}
	
	/**
	 * Get the pitch of the locutor.
	 * @return the pitch of the locutor.
	 */
	public Integer getPitch()
	{
		return this.pitch;
	}
	
	/**
	 * Set the locutor.
	 * @param value name of the locutor.
	 */
	public void setLocutor(String value)
	{
		this.locutor = value;
	}
	
	public String getLocutor()
	{
		return this.locutor;
	}

	/**
	 * Read a text with the text to speak engine.
	 * (Asynchronous)
	 * 
	 * @param text text to speak.
	 * @param locutor name of the locutor.
	 * @param pitch pitch (50 .. 200)
	 * @return the success of the command.
	 */
	public Boolean speakAsync(String text, String locutor, Integer pitch)
	{	
		if (!pParent.radio.getConnected())
		{
			return false;
		}
		
		Boolean ret;
		String cmd;
		Object soundState[];
		
		asyncStackLock.incStackCounter();
		
		asyncStackLock.mutexPre.acquire();
		stop();
		soundState = pParent.status.requestOne(TuxAPIConst.ST_NAME_TTS_SOUND_STATE);
		if (soundState[0].equals("ON"))
		{
			eventHandlers.waitCondition(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, 5.0, 
					"OFF", null);
		}
		asyncStackLock.mutexPre.release();
		
		asyncStackLock.mutexIn.acquire();
		if (asyncStackLock.getStackCounter() <= 1)
		{
			// Set the locutor
			if (locutor != "")
			{
				setLocutor(locutor);
			}
			cmd = String.format("tts/locutor?name=%s", this.locutor);
			ret = cmdSimpleResult(cmd);
			if (!ret)
			{
				asyncStackLock.decStackCounter();
				asyncStackLock.mutexIn.release();
				return false;
			}
			// Set the pitch
			if (pitch != 0)
			{
				setPitch(pitch);
			}
			cmd = String.format("tts/pitch?value=%d", this.pitch);
			ret = cmdSimpleResult(cmd);
			if (!ret)
			{
				asyncStackLock.decStackCounter();
				asyncStackLock.mutexIn.release();
				return false;
			}
			// Remove ending lines
			text = text.replace("\n", ".");
			// Try to encode the string
			try
			{
				text = URLEncoder.encode(text, "UTF-8");
			} catch (Exception e) {}
			// Perform the speech
			cmd = String.format("tts/speak?text=%s", text);
			ret = cmdSimpleResult(cmd);
			if (!ret)
			{
				asyncStackLock.decStackCounter();
				asyncStackLock.mutexIn.release();
				return false;
			}
			if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
			{
				asyncStackLock.decStackCounter();
				asyncStackLock.mutexIn.release();
				return true;
			}
			// Wait the speak status
			ret = eventHandlers.waitCondition(TuxAPIConst.ST_NAME_SPEAK_STATUS, 5.0,
					null, null);
			if (!ret)
			{
				asyncStackLock.decStackCounter();
				asyncStackLock.mutexIn.release();
				return false;
			}
			// Get the speak status
			Object result[] = pParent.status.requestOne(TuxAPIConst.ST_NAME_SPEAK_STATUS);
			if (result[0].equals("NoError"))
			{
				asyncStackLock.decStackCounter();
				asyncStackLock.mutexIn.release();
				return true;
			}
			else
			{
				asyncStackLock.decStackCounter();
				asyncStackLock.mutexIn.release();
				return false;
			}
		}
		asyncStackLock.decStackCounter();
		asyncStackLock.mutexIn.release();
		return true;
	}
	
	/**
	 * Read a text with the text to speak engine.
	 * (Asynchronous)
	 * 
	 * @param text text to speak.
	 * @return the success of the command.
	 */
	public Boolean speakAsync(String text)
	{
		return speakAsync(text, "", 0);
	}
	
	/**
	 * Read a text with the text to speak engine.
	 * 
	 * @param text text to speak.
	 * @param locutor name of the locutor.
	 * @param pitch pitch (50 .. 200)
	 * @return the success of the command.
	 */
	public Boolean speak(String text, String locutor, Integer pitch)
	{
		if (!pParent.radio.getConnected())
		{
			return false;
		}
		
		Object soundState[];
		Boolean result = true;
		
		syncStackLock.incStackCounter();
		syncStackLock.mutexPre.acquire();
		currText = text;
		currLocutor = locutor;
		currPitch = pitch;
		stop();
		soundState = pParent.status.requestOne(TuxAPIConst.ST_NAME_TTS_SOUND_STATE);
		if (soundState[0].equals("ON"))
		{
			eventHandlers.waitCondition(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, 5.0, 
					"OFF", null);
		}
		syncStackLock.mutexPre.release();
		
		syncStackLock.mutexIn.acquire();
		if (syncStackLock.getStackCounter() <= 1)
		{
			if (speakAsync(currText, currLocutor, currPitch))
			{
				if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
				{
					result = true;
				}
				else
				{
					eventHandlers.waitCondition(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, 1.0, "ON", null);
					soundState = pParent.status.requestOne(TuxAPIConst.ST_NAME_TTS_SOUND_STATE);
					if (soundState[0].equals("ON"))
					{
						eventHandlers.waitCondition(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, 99999999.0, 
								"OFF", null);
					}
				}
				result = true;
			}
			else
			{
				result = false;
			}
		}
		syncStackLock.decStackCounter();
		syncStackLock.mutexIn.release();
		
		return result;
	}
	
	/**
	 * Read a text with the text to speak engine.
	 * 
	 * @param text text to speak.
	 * @return the success of the command.
	 */
	public Boolean speak(String text)
	{
		return speak(text, "", 0);
	}
	
	/**
	 * Push a text in the TTS stack.
	 * 
	 * @param text text to speak.
	 * @return the success of the command.
	 */
	public Boolean speakPush(String text)
	{
		String cmd;
		
		// Remove ending lines
		text = text.replace("\n", ".");
		// Try to encode the string
		try
		{
			text = URLEncoder.encode(text, "UTF-8");
		} catch (Exception e) {}
		// Perform the speech
		cmd = String.format("tts/stack_speak?text=%s", text);
		
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Stop the current speech and flush the TTS stack.
	 * @return the success of the command.
	 */
	public Boolean speakFlush()
	{
		String cmd = "tts/stack_flush?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Stop the current speech.
	 * @return the success of the command.
	 */
	public Boolean stop()
	{
		String cmd = "tts/stop?null=true";
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Set the pause state of the tts engine.
	 * @param value True or False.
	 * @return the success of the command.
	 */
	public Boolean setPause(Boolean value)
	{
		String pauseSt;
		String cmd;
		
		if (value)
		{
			pauseSt = "True";
		}
		else
		{
			pauseSt = "False";
		}
		cmd = String.format("tts/pause?value=%s", pauseSt);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Return the current available voice list.
	 * @return a list of string.
	 */
	public List<String> getVoices()
	{
		return cmdVoiceList();
	}
	
	/**
	 * Register a callback on the voice list event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnVoiceList(Object sender, String method, Integer idx)
	{
		return eventHandlers.register(TuxAPIConst.ST_NAME_VOICE_LIST, idx, sender, method, 
				null, null);
	}
	
	/**
	 * Register a callback on the voice list event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnVoiceList(Object sender, String method)
	{
		return registerEventOnVoiceList(sender, method, -1);
	}
	
	/**
	 * Unregister a callback from the voice list event.
	 * 
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnVoiceList(Integer idx)
	{
		eventHandlers.unregister(TuxAPIConst.ST_NAME_VOICE_LIST, idx);
	}
	
	/**
	 * Register a callback on the sound on event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnSoundOn(Object sender, String method, Integer idx)
	{
		return eventHandlers.register(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, idx, sender, method, 
				"ON", null);
	}
	
	/**
	 * Register a callback on the sound on event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnSoundOn(Object sender, String method)
	{
		return registerEventOnSoundOn(sender, method, -1);
	}
	
	/**
	 * Unregister a callback from the sound on event.
	 * 
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnSoundOn(Integer idx)
	{
		eventHandlers.unregister(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, idx);
	}
	
	/**
	 * Register a callback on the sound off event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnSoundOff(Object sender, String method, Integer idx)
	{
		return eventHandlers.register(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, idx, sender, method, 
				"OFF", null);
	}
	
	/**
	 * Register a callback on the sound off event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnSoundOff(Object sender, String method)
	{
		return registerEventOnSoundOff(sender, method, -1);
	}
	
	/**
	 * Unregister a callback from the sound off event.
	 * 
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnSoundOff(Integer idx)
	{
		eventHandlers.unregister(TuxAPIConst.ST_NAME_TTS_SOUND_STATE, idx);
	}
}

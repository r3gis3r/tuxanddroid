package com.tuxisalive.api;

import java.util.Hashtable;

/**
 * Base class to control a led.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPILedBase
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	
	/*
	 * Private field
	 */
	private String ledNamex;
	
	/**
	 * Constructor of the class.
	 * @param parent parent object as TuxAPI
	 */
	public TuxAPILedBase(TuxAPI parent, String ledName)
	{
		pParent = parent;
		if (ledName == "both")
		{
			ledNamex = TuxAPIConst.LED_NAME_BOTH;
		}
		else if (ledName == "left")
		{
			ledNamex = TuxAPIConst.LED_NAME_LEFT;
		}
		else
		{
			ledNamex = TuxAPIConst.LED_NAME_RIGHT;
		}
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
	private Boolean changeIntensity(Integer fxType, Double intensity)
	{
		String cmd;
		Integer fxStep;
		String fxCType;
		
		if (fxType == TuxAPIConst.LFX_NONE)
		{
			cmd = String.format("leds/on?intensity=%s&leds=%s",
					intensity.toString(), ledNamex);
		}
		else
		{
			if (fxType == TuxAPIConst.LFX_FADE)
			{
				fxStep = 10;
			}
			else // TuxAPIConst.LFX_STEP
			{
				fxStep = 3;
			}
			fxCType = TuxAPIConst.LFXEX_GRADIENT_NBR;
			cmd = String.format("leds/set?fx_speed=0.5&fx_step=%d&fx_type=%s&intensity=%s&leds=%s",
					fxStep, fxCType, intensity.toString(), ledNamex);
		}
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Set the intensity of the led.
	 * 
	 * @param intensity intensity of the led (0.0 .. 1.0)
	 * @return the success of the command.
	 */
	public Boolean setIntensity(Double intensity)
	{
		return changeIntensity(TuxAPIConst.LFX_NONE, intensity);
	}
	
	/**
	 * Set the led state to ON.
	 * 
	 * @param fxType type of the transition effect.
	 * 					(LFX_NONE|LFX_FADE|LFX_STEP)
	 * @return the success of the command.
	 */
	public Boolean on(Integer fxType)
	{
		return changeIntensity(fxType, 1.0);
	}
	
	/**
	 * Set the led state to ON.
	 * 
	 * @return the success of the command.
	 */
	public Boolean on()
	{
		return on(TuxAPIConst.LFX_NONE);
	}
	
	/**
	 * Set the led state to OFF.
	 * 
	 * @param fxType type of the transition effect.
	 * 					(LFX_NONE|LFX_FADE|LFX_STEP)
	 * @return the success of the command.
	 */
	public Boolean off(Integer fxType)
	{
		return changeIntensity(fxType, 0.0);
	}
	
	/**
	 * Set the led state to OFF.
	 * 
	 * @return the success of the command.
	 */
	public Boolean off()
	{
		return off(TuxAPIConst.LFX_NONE);
	}
	
	/**
	 * Make a pulse effect with the led.
	 * (Asynchronous)
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param duration duration of the effect.
	 * @param fxType type of the transition effect.
	 * 				(LFX_NONE|LFX_FADE|LFX_STEP)
	 * @return the success of the command.
	 */
	public Boolean blinkDuringAsync(Integer speed, Double duration, Integer fxType)
	{
		Double perSec = speed * 1.0;
		duration = duration * perSec * 2.;
		Integer count = duration.intValue();
		Double delay = 1.0 / perSec;
		String cmd;
		Integer fxStep;
		String fxCType;
		Double fxSpeed;
		
		if (fxType == TuxAPIConst.LFX_NONE)
		{
			cmd = String.format("leds/blink?leds=%s&count=%d&delay=%s", ledNamex,
					count, delay.toString());
		}
		else
		{
			if (fxType == TuxAPIConst.LFX_FADE)
			{
				fxStep = 10;
			}
			else // TuxAPIConst.LFX_STEP
			{
				fxStep = 2;
			}
			fxCType = TuxAPIConst.LFXEX_GRADIENT_NBR;
			fxSpeed = delay / 3.0;
			cmd = String.format("leds/pulse?count=%d&fx_speed=%s&fx_step=%d&fx_type=%s&leds=%s&max_intensity=1.0&min_intensity=0.0&period=%s",
					count, fxSpeed.toString(), fxStep, fxCType, ledNamex, delay.toString());
		}
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Make a pulse effect with the led.
	 * (Asynchronous)
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param duration duration of the effect.
	 * @return the success of the command.
	 */
	public Boolean blinkDuringAsync(Integer speed, Double duration)
	{
		return blinkDuringAsync(speed, duration, TuxAPIConst.LFX_NONE);
	}
	
	/**
	 * Make a pulse effect with the led.
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param duration duration of the effect.
	 * @param fxType type of the transition effect.
	 * 				(LFX_NONE|LFX_FADE|LFX_STEP)
	 * @return the success of the command.
	 */
	public Boolean blinkDuring(Integer speed, Double duration, Integer fxType)
	{
		Boolean ret = blinkDuringAsync(speed, duration, fxType);
		if (ret)
		{
			TuxAPIMisc.sleep(duration);
		}
		return ret;
	}
	
	/**
	 * Make a pulse effect with the led.
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param duration duration of the effect.
	 * @return the success of the command.
	 */
	public Boolean blinkDuring(Integer speed, Double duration)
	{
		return blinkDuring(speed, duration, TuxAPIConst.LFX_NONE);
	}
	
	/**
	 * Make a pulse effect with the led.
	 * (Asynchronous)
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param count number of blinks.
	 * @param fxType type of the transition effect.
	 * 				(LFX_NONE|LFX_FADE|LFX_STEP)
	 * @return the success of the command.
	 */
	public Boolean blinkAsync(Integer speed, Integer count, Integer fxType)
	{
		count *= 2;
		Double delay = 1.0 / speed;
		String cmd;
		Integer fxStep;
		String fxCType;
		Double fxSpeed;
		
		if (fxType == TuxAPIConst.LFX_NONE)
		{
			cmd = String.format("leds/blink?leds=%s&count=%d&delay=%s", ledNamex,
					count, delay.toString());
		}
		else
		{
			if (fxType == TuxAPIConst.LFX_FADE)
			{
				fxStep = 10;
			}
			else // TuxAPIConst.LFX_STEP
			{
				fxStep = 2;
			}
			fxCType = TuxAPIConst.LFXEX_GRADIENT_NBR;
			fxSpeed = delay / 3.0;
			cmd = String.format("leds/pulse?count=%d&fx_speed=%s&fx_step=%d&fx_type=%s&leds=%s&max_intensity=1.0&min_intensity=0.0&period=%s",
					count, fxSpeed.toString(), fxStep, fxCType, ledNamex, delay.toString());
		}
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Make a pulse effect with the led.
	 * (Asynchronous)
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param count number of blinks.
	 * @return the success of the command.
	 */
	public Boolean blinkAsync(Integer speed, Integer count)
	{
		return blinkAsync(speed, count, TuxAPIConst.LFX_NONE);
	}
	
	/**
	 * Make a pulse effect with the led.
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param count number of blinks.
	 * @param fxType type of the transition effect.
	 * 				(LFX_NONE|LFX_FADE|LFX_STEP)
	 * @return the success of the command.
	 */
	public Boolean blink(Integer speed, Integer count, Integer fxType)
	{
		Boolean ret = blinkAsync(speed, count, fxType);
		if (ret)
		{
			Double delay = 1.0 / speed;
			Double duration = delay * count;
			TuxAPIMisc.sleep(duration);
		}
		return ret;
	}
	
	/**
	 * Make a pulse effect with the led.
	 * 
	 * @param speed speed of the state changing.
	 * 				(SPV_VERYSLOW|SPV_SLOW|SPV_NORMAL|SPV_FAST|SPV_VERYFAST)
	 * @param count number of blinks.
	 * @return the success of the command.
	 */
	public Boolean blink(Integer speed, Integer count)
	{
		return blink(speed, count, TuxAPIConst.LFX_NONE);
	}
	
	/**
	 * Get the state of the led.
	 * 
	 * @return (SSV_ON|SSV_OFF|SSV_CHANGING)
	 */
	public String getState()
	{
		String result = TuxAPIConst.SSV_OFF;
		Object ret[];
		Object ret1[];
		
		if (ledNamex == TuxAPIConst.LED_NAME_LEFT)
		{
			ret = pParent.status.requestOne(TuxAPIConst.ST_NAME_LEFT_LED);
			result = (String)ret[0];
		}
		else if (ledNamex == TuxAPIConst.LED_NAME_RIGHT)
		{
			ret = pParent.status.requestOne(TuxAPIConst.ST_NAME_RIGHT_LED);
			result = (String)ret[0];
		}
		else
		{
			ret = pParent.status.requestOne(TuxAPIConst.ST_NAME_LEFT_LED);
			ret1 = pParent.status.requestOne(TuxAPIConst.ST_NAME_RIGHT_LED);
			if ((String)ret[0] == (String)ret1[0])
			{
				result = (String)ret[0];
			}
		}
		return result;
	}
}

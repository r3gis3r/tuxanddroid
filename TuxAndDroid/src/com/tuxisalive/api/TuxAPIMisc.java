package com.tuxisalive.api;

/**
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIMisc
{
	public static void sleep(Double sec)
	{
		int delay;
		
		try
		{
			sec = sec * 1000;
			delay = sec.intValue();
			Thread.sleep(delay);
		}
		catch (Exception e) {}
	}
	
	public static Boolean stringInStringArray(String[] stArray, String myString)
	{
		for (int i = 0; i < stArray.length; i++)
		{
			if (stArray[i].equals(myString))
			{
				return true;
			}
		}
		return false;
	}
}

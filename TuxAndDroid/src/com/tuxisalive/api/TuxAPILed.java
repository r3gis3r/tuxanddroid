package com.tuxisalive.api;

/**
 * Class to control the blue leds.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPILed
{
	/*
	 * Public field
	 */
	public TuxAPILedBase left;
	public TuxAPILedBase right;
	public TuxAPILedBase both;
	
	/**
	 * Constructor of the class.
	 * @param parent parent object as TuxAPI
	 */
	public TuxAPILed(TuxAPI parent)
	{
		left = new TuxAPILedBase(parent, "left");
		right = new TuxAPILedBase(parent, "right");
		both = new TuxAPILedBase(parent, "both");
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy() {}
}

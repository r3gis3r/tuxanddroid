package com.tuxisalive.api;

/**
 * Class to control the buttons.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIButton
{
	/*
	 * Public field
	 */
	public TuxAPISwitch left;
	public TuxAPISwitch right;
	public TuxAPISwitch head;
	public TuxAPIRemote remote;
	
	/**
	 * Constructor of the class.
	 * @param parent parent object as TuxAPI
	 */
	public TuxAPIButton(TuxAPI parent)
	{
		left = new TuxAPISwitch(parent, TuxAPIConst.ST_NAME_LEFT_BUTTON);
		right = new TuxAPISwitch(parent, TuxAPIConst.ST_NAME_RIGHT_BUTTON);
		head = new TuxAPISwitch(parent, TuxAPIConst.ST_NAME_HEAD_BUTTON);
		remote = new TuxAPIRemote(parent);
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy() {}
}

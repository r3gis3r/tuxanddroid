package com.tuxisalive.api;

import java.util.Hashtable;

/**
 * Class to control the attitune files.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIAttitune
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	
	/**
	 * Constructor of the class.
	 * @param parent parent object as TuxAPI
	 */
	public TuxAPIAttitune(TuxAPI parent)
	{
		pParent = parent;
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
	 * Load an attitune file.
	 * 
	 * @param path path of the attitune file.
	 * @return the success of the command.
	 */
	public Boolean load(String path)
	{
		String cmd = String.format("attitune/load?path=%s", path);
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Play the loaded attitune.
	 * 
	 * @param begin starting second.
	 * @return the success of the command.
	 */
	public Boolean play(Double begin)
	{
		String cmd = String.format("attitune/play?begin=%s", begin.toString());
		return cmdSimpleResult(cmd);
	}
	
	/**
	 * Play the loaded attitune.
	 * 
	 * @return the success of the command.
	 */
	public Boolean play()
	{
		return play(0.0);
	}
	
	/**
	 * Stop the current attitune.
	 * 
	 * @return the success of the command.
	 */
	public Boolean stop()
	{
		String cmd = String.format("attitune/stop?null=true");
		return cmdSimpleResult(cmd);
	}
}

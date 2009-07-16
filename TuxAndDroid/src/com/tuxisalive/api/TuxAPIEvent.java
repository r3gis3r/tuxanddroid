package com.tuxisalive.api;

import java.util.Hashtable;

/**
 * TuxAPIEvent is a module part of TuxAPI. It run an asynchronous loop to retrieve the
 * events from Tuxdroid, every 100 msec.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPIEvent
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	private TuxEventHandlers eventHandlers;
	public TuxEventHandlers handler;
	/*
	 * Event loop field
	 */
	private SThread eventLoopThread;
	private SLock eventLoopMutex;
	private Boolean eventLoopRun;
	private Double eventLoopDelay;
	
	/**
	 * Constructor of the class.
	 * 
	 * @param parent parent object as TuxAPI.
	 */
	public TuxAPIEvent(TuxAPI parent)
	{
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
		handler = eventHandlers;
		eventLoopThread = null;
		eventLoopMutex = new SLock();
		eventLoopRun = false;
		eventLoopDelay = 0.1;
	}
	
	/**
	 * Destructor of the class
	 */
	public void destroy()
	{
		stop();
	}
	
	/**
	 * Set the delay of the event loop.
	 * 
	 * @param value Delay in seconds. (default value is 0.1 sec)
	 */
	public void setDelay(double value)
	{
		if (value < 0.1)
		{
			value = 0.1;
		}
		else if (value > 2.0)
		{
			value = 2.0;
		}
		eventLoopDelay = value;
	}
	
	/*
	 * 
	 */
	private Boolean getEventLoopRun()
	{
		Boolean value;
		
		eventLoopMutex.acquire();
        value = eventLoopRun;
        eventLoopMutex.release();
        return value;
	}
	
	/*
	 * 
	 */
	private void setEventLoopRun(Boolean value)
	{
		eventLoopMutex.acquire();
        eventLoopRun = value;
        eventLoopMutex.release();
	}
	
	/**
	 * Start the loop of event retrieving.
	 */
	public void start()
	{
		if (pParent.server.getClientLevel() == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			return;
		}
		if (getEventLoopRun())
		{
			return;
		}
		eventLoopThread = new SThread((Object)this, "eventLoop");
		eventLoopThread.start();
	}
	
	/**
	 * Stop the loop of event retrieving.
	 */
	public void stop()
	{
		if (!getEventLoopRun())
		{
			return;
		}
		setEventLoopRun(false);
		TuxAPIMisc.sleep(eventLoopDelay);
		if (eventLoopThread != null)
		{
			if (eventLoopThread.isAlive())
			{
				eventLoopThread.stop();
			}
		}
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void eventLoop()
	{
		String stName;
		String stValue;
		Double stDelay;
		
		setEventLoopRun(true);
		while (getEventLoopRun())
		{
			// Make command
			String cmd = "status/events?null=true";
			Hashtable<Object,Object> varStruct = new Hashtable<Object,Object>();
			Hashtable<Object,Object> varResult = new Hashtable<Object,Object>();
			// Request
			if (pParent.server.request(cmd, varStruct, varResult, false))
			{
				Integer dataCount = (Integer)varResult.get("data_count");
				for (int i = 0; i < dataCount; i++)
				{
					String dataName = String.format("data%d", i);
					try
					{
						Hashtable<Object,Object> eventStruct = (Hashtable)varResult.get(dataName);
						stName = (String)eventStruct.get("name");
						stValue = (String)eventStruct.get("value");
						String tmp = (String)eventStruct.get("delay");
						stDelay = Double.parseDouble(tmp);
					}
					catch (Exception e)
					{
						continue;
					}
					
					if (stName.equals(TuxAPIConst.ST_NAME_EXTERNAL_STATUS))
					{
						Object[] pList = stValue.split("\\|");

						if (pList.length > 0)
						{
							eventHandlers.emit("all", pList);
						}
					}
					else
					{
						eventHandlers.emit(stName, stValue, stDelay);
					}
				}
			}
			// Wait before the next cycle
			TuxAPIMisc.sleep(eventLoopDelay);
		}
	}
}

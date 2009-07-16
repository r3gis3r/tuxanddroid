package com.tuxisalive.api;

import java.util.*;

/**
 * TuxAPIServer is a module part of TuxAPI. This class make a connection 
 * to the Tuxdroid HTTP REST server.
 * 
 * @author 		Rémi Jocaille
 * @version		0.1
 * @since		0.1
 */
public class TuxAPIServer
{
	/*
	 * Parent field
	 */
	private TuxAPI pParent;
	private TuxEventHandlers eventHandlers;
	private TuxEventHandler connectedEventHandler;
	/*
	 * Server field
	 */
	private String pHost;
	private Integer pPort;
	private String cmdUrl;
	private SLock cmdUrlMutex;
	/*
	 * Client field
	 */
	@SuppressWarnings("unused")
	private String clientName;
	private String clientPasswd;
	private Integer clientLevel;
	@SuppressWarnings("unused")
	private Integer clientId;
	/*
	 * Connection field
	 */
	private Boolean connected;
	private Double connectedLU;
	private SLock connectedMutex;
	/*
	 * Auto connection field
	 */
	private Boolean autoConnectionRun;
	private SLock autoConnectionMutex;
	private SThread autoConnectionThread;
	private Double autoConnectionLoopDelay;
	/*
	 * 
	 */
	private TuxHTTPRequest sender;
	
	/**
	 * Constructor of the class.
	 * 
	 * @param parent parent object as TuxAPI.
	 * @param host host of the server.
	 * @param port port of the server.
	 */
	public TuxAPIServer(TuxAPI parent, String host, Integer port)
	{
		pParent = parent;
		eventHandlers = pParent.getEventHandlers();
		connectedEventHandler = (TuxEventHandler)eventHandlers.getEventHandler(TuxAPIConst.ST_NAME_API_CONNECT);
		pHost = host;
		pPort = port;
		cmdUrl = "0/";
		cmdUrlMutex = new SLock();
		clientName = "";
		clientPasswd = "";
		clientLevel = TuxAPIConst.CLIENT_LEVEL_ANONYME;
		clientId = 0;
		connected = false;
		connectedLU = System.currentTimeMillis() / 1000.0;
		connectedMutex = new SLock();
		autoConnectionRun = false;
		autoConnectionMutex = new SLock();
		autoConnectionLoopDelay = 1.0;
		sender = new TuxHTTPRequest(pHost, pPort);
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy()
	{
		this.autoConnectionStop();
		this.disconnect();
	}
	
	/**
	 * Get the client level of the API instance.
	 * @return the client level.
	 */
	public Integer getClientLevel()
	{
		return clientLevel;
	}
	
	/*
	 * 
	 */
	private void setCmdUrl(String value)
	{
		cmdUrlMutex.acquire();
		cmdUrl = value;
		cmdUrlMutex.release();
	}
	
	/*
	 * 
	 */
	private String getCmdUrl()
	{
		String value;
		
		cmdUrlMutex.acquire();
		value = cmdUrl;
		cmdUrlMutex.release();
		return value;
	}
	
	/*
	 * 
	 */
	private void setConnected(Boolean value)
	{
		Double currentTime = System.currentTimeMillis() / 1000.0;
        Double lU = currentTime - connectedLU;
        
		connectedMutex.acquire();
        connected = value;
        connectedMutex.release();
        
        if (value)
        {
        	connectedEventHandler.emit(true, lU);
        }
        else
        {
        	connectedEventHandler.emit(false, lU);
        }
        connectedLU = currentTime;
	}
	
	/*
	 * 
	 */
	private void setDisconnected()
	{
		if (!getConnected())
		{
			return;
		}
		
		clientName = "";
		clientPasswd = "";
		clientLevel = TuxAPIConst.CLIENT_LEVEL_ANONYME;
		clientId = 0;
		setConnected(false);
		setCmdUrl("0/");
		System.out.println("TuxAPI is disconnected.");
	}
	
	/**
	 * Get the state of connection to the server.
	 * 
	 * @return the state of connection to the server.
	 */
	public Boolean getConnected()
	{
		Boolean value;
		
		connectedMutex.acquire();
        value = connected;
        connectedMutex.release();
        return value;
	}
	
	/**
	 * Wait until the client is connected to the server.
	 * 
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitConnected(Double timeout)
	{
		if (getConnected())
		{
			return true;
		}
		return connectedEventHandler.waitCondition(timeout, true, null);
	}
	
	/**
	 * Wait until the client is disconnected from the server.
	 * 
	 * @param timeout maximal delay to wait.
	 * @return the state of the wait result.
	 */
	public Boolean waitDisconnected(Double timeout)
	{
		if (!getConnected())
		{
			return true;
		}
		return connectedEventHandler.waitCondition(timeout, false, null);
	}
	
	/**
	 * Register a callback on the connected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnConnected(Object sender, String method)
	{
		return registerEventOnConnected(sender, method, -1);
	}
	
	/**
	 * Register a callback on the connected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnConnected(Object sender, String method, Integer idx)
	{
		return connectedEventHandler.register(idx, sender, method, true, null);
	}
	
	/**
	 * Unregister a callback from the connected event.
	 * 
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnConnected(Integer idx)
	{
		connectedEventHandler.unregister(idx);
	}
	
	/**
	 * Register a callback on the disconnected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnDisconnected(Object sender, String method)
	{
		return registerEventOnDisconnected(sender, method, -1);
	}
	
	/**
	 * Register a callback on the disconnected event.
	 * 
	 * @param sender parent object of the method.
	 * @param method method name as string.
	 * @param idx index from a previous register.
	 * @return the new index of the callback in the handler.
	 */
	public Integer registerEventOnDisconnected(Object sender, String method, Integer idx)
	{
		return connectedEventHandler.register(idx, sender, method, false, null);
	}
	
	/**
	 * Unregister a callback from the disconnected event.
	 * 
	 * @param idx index from a previous register.
	 */
	public void unregisterEventOnDisconnected(Integer idx)
	{
		connectedEventHandler.unregister(idx);
	}
	
	/**
	 * Send a request to the server.
	 * 
	 * @param cmd formated command in an url.
	 * @param varStruct structure definition of the requested values.
	 * @param varResult returned values in a structure.
	 * @param forceExec force the sending of the command when the client is not yet connected.
	 * @return the success of the request.
	 */
	@SuppressWarnings("unchecked")
	public Boolean request(String cmd, Hashtable<Object,Object> varStruct, 
			Hashtable<Object,Object> varResult, Boolean forceExec)
	{
		if (!forceExec)
		{
			if (!getConnected())
			{
				return false;
			}
		}
		
		// Completing the command
		cmd = String.format("%s%s", getCmdUrl(), cmd);
		// Send the request and get the xml structure
		Hashtable<Object,Object> xmlStruct = sender.request(cmd);
		// Check server run and the command success
		if (!xmlStruct.get("server_run").equals("Success"))
		{
			// Server seems to be disconnected
			setDisconnected();
			return false;
		}
		if (!xmlStruct.get("result").equals("Success"))
		{
			return false;
		}
		// Get values from paths
		if (varStruct.size() > 0)
		{	
			for (Enumeration e = varStruct.keys(); e.hasMoreElements(); )
			{
				String valueName = (String)e.nextElement();
				String valuePath = (String)varStruct.get(valueName);
				Object value = getValueFromStructure(xmlStruct, valuePath);
				varResult.put(valueName, value);
			}
		}
		else
		{
			for (Enumeration e = xmlStruct.keys(); e.hasMoreElements(); )
			{
				Object value = e.nextElement();
				varResult.put(value, xmlStruct.get(value));
			}
		}
		
		return true;
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	private Object getValueFromStructure(Hashtable<Object,Object> struct, String valuePath)
	{
		String pathList[] = valuePath.split("\\.");
		Hashtable<Object,Object> node = struct;
		Object result = (Object)null;
		
		for (int i = 0; i < pathList.length; i++)
		{
			String p = pathList[i];
			// Current node in path is valid
			if (node.containsKey(p))
			{
				// Path : leaf
				if (i == (pathList.length - 1))
				{
					// Return the value of the matched path
					result = (Object)node.get(p);
					return result;
				}
				// Path : node
				else
				{
					node = (Hashtable)node.get(p);
				}
			}
			// Invalid path
			else
			{
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Get the HTTP server version.
	 * @return the version of the HTTP server.
	 */
	public String getVersion()
	{
		String result = "";
		Hashtable<Object,Object> varStruct = new Hashtable<Object,Object>();
		Hashtable<Object,Object> varResult = new Hashtable<Object,Object>();
		
		varStruct.put("version", "data0.version");
		
		if (request("version?null=true", varStruct, varResult, false))
		{
			result = (String)varResult.get("version");
		}
		return result;
	}
	
	/**
	 * Attempt to connect to the server.
	 * 
	 * @param level requested level of the client.
	 * @param name name of the client.
	 * @param passwd password of the client.
	 * @return the success of the connection.
	 */
	public Boolean connect(Integer level, String name, String passwd)
	{
		// If already connected - Success
		if (getConnected())
		{
			return true;
		}
		
		// If client level is invalid - Failed
		Boolean validLevel = false;
		for (int i = 0; i < TuxAPIConst.CLIENT_LEVELS.length; i++)
		{
			if (level == TuxAPIConst.CLIENT_LEVELS[i])
			{
				validLevel = true;
				break;
			}
		}
		if (!validLevel)
		{
			return false;
		}
		clientLevel = level;
		// If client level is ANONYME - Success
		if (level == TuxAPIConst.CLIENT_LEVEL_ANONYME)
		{
			cmdUrl = "0/";
			setCmdUrl(cmdUrl);
			setConnected(true);
		}
		// Make command
		String cmd = String.format("client/create?level=%d&name=%s&passwd=%s", 
				level, name, passwd);
		Hashtable<Object,Object> varStruct = new Hashtable<Object,Object>();
		varStruct.put("client_id", "data0.client_id");
		Hashtable<Object,Object> varResult = new Hashtable<Object,Object>();
		// Request
		if (!this.request(cmd, varStruct, varResult, true))
		{
			return false;
		}
		// Check client_id
		if ((varResult.get("client_id") == null) ||
			((String)varResult.get("client_id") == "-1"))
		{
			return false;
		}
		// Ok
		clientName = name;
		clientPasswd = passwd;
		clientLevel = level;
		cmdUrl = String.format("%s/", (String)varResult.get("client_id"));
		setCmdUrl(cmdUrl);
		setConnected(true);
		System.out.println("TuxAPI is connected.");
		
		return true;
	}
	
	/**
	 * Disconnect the client from the server.
	 * 
	 * @return the success of the disconnection.
	 */
	public Boolean disconnect()
	{
		// If already disconnected - Success
		if (! getConnected())
		{
			return true;
		}
		// Make command
		String cmd = String.format("client/destroy?passwd=%s", clientPasswd);
		Hashtable<Object,Object> varStruct = new Hashtable<Object,Object>();
		Hashtable<Object,Object> varResult = new Hashtable<Object,Object>();
		// Request
		if (!this.request(cmd, varStruct, varResult, false))
		{
			return false;
		}
		// OK
		autoConnectionStop();
		setDisconnected();
		
		return true;
	}
	
	/*
	 * 
	 */
	private void setAutoConnectionRun(Boolean value)
	{
		autoConnectionMutex.acquire();
        autoConnectionRun = value;
        autoConnectionMutex.release();
	}
	
	/*
	 * 
	 */
	private Boolean getAutoConnectionRun()
	{
		Boolean value;
		autoConnectionMutex.acquire();
        value = autoConnectionRun;
        autoConnectionMutex.release();
        return value;
	}
	
	/**
	 * Start the automatic connection/reconnection loop with the server.
	 * 
	 * @param level requested level of the client.
	 * @param name name of the client.
	 * @param passwd password of the client.
	 */
	public void autoConnect(Integer level, String name, String passwd)
	{
		if (getAutoConnectionRun())
		{
			return;
		}
		autoConnectionThread = new SThread((Object)this, "autoConnectionLoop", level, 
				name, passwd);
		autoConnectionThread.start();
		clientLevel = level;
	}
	
	/*
	 * 
	 */
	private void autoConnectionStop()
	{
		if (!getAutoConnectionRun())
		{
			return;
		}
		setAutoConnectionRun(false);
		TuxAPIMisc.sleep(autoConnectionLoopDelay);
		if (autoConnectionThread != null)
		{
			if (autoConnectionThread.isAlive())
			{
				autoConnectionThread.stop();
			}
		}
	}
	
	/*
	 * 
	 */
	protected void autoConnectionLoop(Integer level, String name, String passwd)
	{
		setAutoConnectionRun(true);
		while (getAutoConnectionRun())
		{
			if (!getConnected())
			{
				connect(level, name, passwd);
			}
			// Wait before the next cycle
			TuxAPIMisc.sleep(autoConnectionLoopDelay);
		}
	}
}

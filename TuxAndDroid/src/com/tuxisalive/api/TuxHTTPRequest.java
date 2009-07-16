package com.tuxisalive.api;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * TuxHTTPRequest is a sender of request to a Tuxdroid service server.
 * The resulting xml data is automatically parsed and returned to a
 * data structure.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxHTTPRequest
{

	private String baseUrl;
	private SLock mutex;
	
	/**
	 * Constructor of the class.
	 * 
	 * @param host host of the server.
	 * @param port port of the server.
	 */
	public TuxHTTPRequest(String host, int port)
	{
		baseUrl = String.format("http://%s:%d", host, port);
		mutex = new SLock();
	}
	
	/*
	 * Get a specific value from a hashtable structure.
	 */
	@SuppressWarnings("unchecked")
	private Object getValueFromStructure(Hashtable<Object,Object> struct,
			String valuePath)
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
					node = (Hashtable<Object,Object>)node.get(p);
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
	 * Make a request to the server.
	 * 
	 * @param cmd formated command in an url.
	 * @return a data structure.
	 */
	public Hashtable<Object,Object> request(String cmd)
	{
		return this.request(cmd, "GET");
	}
	
	/**
	 * Make a request to the server.
	 * 
	 * @param cmd formated command in an url.
	 * @param method method of the request.
	 * @return a data structure.
	 */
	public Hashtable<Object,Object> request(String cmd, String method)
	{
		cmd = String.format("/%s", cmd);
		Hashtable<Object,Object> xmlStruct = new Hashtable<Object,Object>();
		String cCmd = String.format("%s%s", baseUrl, cmd);
		URL url;
		InputSource s;
		
		// Set default values in the xml dictionary
		xmlStruct.put("result", "Failed");
		xmlStruct.put("data_count", 0);
		xmlStruct.put("server_run", "Failed");
		
		mutex.acquire();
		// Try to connect to the server
		try
		{
			url = new URL(cCmd);
			Reader r = new InputStreamReader(url.openStream(), "ISO-8859-1");
			s = new InputSource(r);
		}
		catch (Exception e)
		{
			mutex.release();
			return xmlStruct;
		}
		
		// Try to parse the xml structure
		xmlStruct = (Hashtable<Object,Object>)this.parseXml(s);
		mutex.release();

		return xmlStruct;
	}
	
	/**
	 * Make a request to the server.
	 * 
	 * @param cmd formated command in an url.
	 * @param varStruct structure definition of the requested values.
	 * @param varResult returned values in a structure.
	 * @return the success of the request.
	 */
	public Boolean request(String cmd, Hashtable<Object,Object> varStruct, 
			Hashtable<Object,Object> varResult)
	{
		// Send the request and get the xml structure
		Hashtable<Object,Object> xmlStruct = this.request(cmd);
		// Check server run and the command success
		if (!xmlStruct.get("server_run").equals("Success"))
		{
			return false;
		}
		if (!xmlStruct.get("result").equals("Success"))
		{
			return false;
		}
		// Get values from paths
		if (varStruct.size() > 0)
		{	
			for (Enumeration<Object> e = varStruct.keys(); e.hasMoreElements(); )
			{
				String valueName = (String)e.nextElement();
				String valuePath = (String)varStruct.get(valueName);
				Object value = getValueFromStructure(xmlStruct, valuePath);
				varResult.put(valueName, value);
			}
		}
		else
		{
			for (Enumeration<Object> e = xmlStruct.keys(); e.hasMoreElements(); )
			{
				Object value = e.nextElement();
				varResult.put(value, xmlStruct.get(value));
			}
		}
		
		return true;
	}
	
	/*
	 * Parse the xml string to a data structure
	 */
	private Hashtable<Object,Object> parseXml(InputSource s)
	{
		Hashtable<Object,Object> struct = new Hashtable<Object,Object>();
		int dataCount = 0;
		String dataNodeName = "";
		
		// Set default values in the xml dictionary
		struct.put("result", "Failed");
		struct.put("data_count", 0);
		struct.put("server_run", "Success");
		
		// Get the response structure in a dictionary
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(s);
			doc.getDocumentElement().normalize();
			Node root = doc.getFirstChild();
			root.getChildNodes().getLength();
			for (int iNode = 0; iNode < root.getChildNodes().getLength(); iNode++)
			{
				Node node = root.getChildNodes().item(iNode);
				if (node.getFirstChild().getNodeValue() != null)
				{
					struct.put(node.getNodeName(), node.getFirstChild().getNodeValue());
				}
				else
				{
					Hashtable<Object,Object> sub_struct = new Hashtable<Object,Object>();
					for (int jNode = 0; jNode < node.getChildNodes().getLength(); jNode++)
					{
						Node node1 = node.getChildNodes().item(jNode);
						sub_struct.put(node1.getNodeName(), node1.getFirstChild().getNodeValue());
					}
					if (node.getNodeName().equals("data"))
					{
						dataNodeName = String.format("data%d", dataCount);
						dataCount++;
					}
					else
					{
						dataNodeName = node.getNodeName();
					}
					struct.put(dataNodeName, sub_struct);
				}
			}
			struct.put("data_count", dataCount);
			struct.put("server_run", "Success");
		}
		catch (Exception e) {}
		
		return struct;
	}
}

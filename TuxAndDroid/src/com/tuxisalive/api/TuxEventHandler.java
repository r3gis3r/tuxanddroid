package com.tuxisalive.api;

import java.lang.reflect.Method;
import java.util.*;

/**
 * TuxEventHandler is an event controller which give to you the mechanisms to
 * make interactive an asynchronous signal.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxEventHandler
{
	private List<Object> functStructList;
	private List<Object> fifoList;
	private SLock listMutex;
	private SLock lockMutex;
	private List<Object> lockList;
	
	/**
	 * Constructor of class.
	 */
	public TuxEventHandler()
	{
		functStructList = new ArrayList<Object>();
		fifoList = new ArrayList<Object>();
		listMutex = new SLock();
		lockList = new ArrayList<Object>();
		lockMutex = new SLock();
	}
	
	/**
	 * Destructor of class.
	 */
	public void destroy()
	{
		this.clearPending();
	}
	
	/**
	 * Register a callback function.
	 * 
	 * @param 	sender	parent object of the method.
	 * @param 	method	method name as string.
	 * @return	the index of the callback in the handler.
	 */
	public Integer register(Object sender, String method)
	{
		Object[] condition = new Object[0];
		
		return register(sender, method, condition);
	}
	
	/**
	 * Register a callback function.
	 * The "condition" is the rule to match the callback event with
	 * a specific set of parameters when a signal is emitted.
	 * The number of objects from the condition need to be the same
	 * than the parameters of the emitted signal.
	 * 
	 * @param 	sender		parent object of the method.
	 * @param 	method		method name as string.
	 * @param 	condition	object array of the condition.
	 * @return	the index of the callback in the handler.
	 */
	public Integer register(Object sender, String method, Object... condition)
	{
		return register(-1, sender, method, condition);
	}
	
	/**
	 * Register another callback at the place of a previous index.
	 * The "condition" is the rule to match the callback event with
	 * a specific set of parameters when a signal is emitted.
	 * The number of objects from the condition need to be the same
	 * than the parameters of the emitted signal.
	 * 
	 * @param	idx			the index of the callback
	 * @param 	sender		parent object of the method.
	 * @param 	method		method name as string.
	 * @param 	condition	object array of the condition.
	 * @return	the index of the callback in the handler.
	 */
	public Integer register(Integer idx, Object sender, String method, 
			Object... condition)
	{
		Integer result = -1;
		Boolean ret = false;
		
		if (idx == -1)
		{
			result = this.pRegister(sender, method, condition);
		}
		else
		{
			ret = this.pUpdateRegister(idx, sender, method, condition);
			if (!ret)
			{
				result = this.pRegister(sender, method, condition);
			}
			else
			{
				result = idx;
			}
		}
		return result;
	}
	
	/*
	 * Called by the public connect methods.
	 */
	private Integer pRegister(Object sender, String method, Object... condition)
	{
		Integer result = -1;
		Hashtable<Object,Object> nFunct = new Hashtable<Object,Object>();
		List<Object> mCondition = new ArrayList<Object>();
		
		for (Object c : condition)
		{
			mCondition.add(c);
		}
		
		listMutex.acquire();
		result = functStructList.size();
		nFunct.put("sender", sender);
		nFunct.put("method", method);
		nFunct.put("condition", mCondition);
		functStructList.add(nFunct);
		listMutex.release();
		
		return result;
	}
	
	/*
	 * Called by the public connect methods
	 */
	private Boolean pUpdateRegister(Integer idx, Object sender, String method, Object... condition)
	{
		Boolean result = false;
		Hashtable<Object,Object> nFunct = new Hashtable<Object,Object>();
		List<Object> mCondition = new ArrayList<Object>();
		
		for (Object c : condition)
		{
			mCondition.add(c);
		}
		
		listMutex.acquire();
		if ((idx < functStructList.size()) && (idx >= 0))
		{
			nFunct.put("sender", sender);
			nFunct.put("method", method);
			nFunct.put("condition", mCondition);
			functStructList.set(idx, nFunct);
			result = true;
		}
		else
		{
			result = false;
		}
		listMutex.release();
		
		return result;
	}
	
	/**
	 * Unregister a callback from the event handler.
	 * 
	 * @param idx	index of the callback.
	 */
	public void unregister(Integer idx)
	{
		listMutex.acquire();
		if ((idx < functStructList.size()) && (idx >= 0))
		{
			functStructList.set(idx, null);
		}
		listMutex.release();
	}
	
	/**
	 * This method store in a stack the configuration of the linked callbacks.
	 * In addition with the "restoreContext" and "clearContext" methods, you can manages the context
	 * of the event handler.
	 */
	public void storeContext()
	{
		listMutex.acquire();
		List<Object> tmp = new ArrayList<Object>();
		for (Object fsl : functStructList)
		{
			tmp.add(fsl);
		}
		fifoList.add(tmp);
		listMutex.release();
		this.clearContext();
	}
	
	/**
	 * This method restore from a stack the configuration of the linked callbacks.
	 * In addition with the "storeContext" and "clearContext" methods, you can manages the context
	 * of the event handler.
	 */
	@SuppressWarnings("unchecked")
	public void restoreContext()
	{
		listMutex.acquire();
		if (fifoList.size() > 0)
		{
			functStructList = (List)fifoList.remove(fifoList.size() - 1);
		}
		listMutex.release();
	}
	
	/**
	 * This method clear the configuration of the linked callbacks.
	 * In addition with the "storeContext" and "restoreContext" methods, you can manages the context
	 * of the event handler.
	 */
	public void clearContext()
	{
		listMutex.acquire();
		functStructList.clear();
		listMutex.release();
	}
	
	/*
	 * Run a callback method.
	 */
	private void run(Hashtable<Object,Object> nFunct, Integer idx, List<Object> fArgs)
	{
		Object sender = nFunct.get("sender");
		String method = (String)nFunct.get("method");
		
		Class<?> targetClass = sender.getClass();
		Object mArgs[] =  new Object[fArgs.size()];
		Class<?> args[] = new Class[fArgs.size()];
		
		for (int i = 0; i < fArgs.size(); i++)
		{
			mArgs[i] = fArgs.get(i);
			
			if (fArgs.get(i) != null)
			{
				args[i] = fArgs.get(i).getClass();
			}
			else
			{
				args[i] = Object.class;
			}
		}
		
		try
		{
			Method targetMethod = targetClass.getDeclaredMethod(method, args);
			targetMethod.invoke(sender, mArgs);
		}
		catch(Exception e)
		{
			this.unregister(idx);
		}
	}
	
	/**
	 * Emit a signal on the event handler with a set of parameters.
	 * 
	 * @param args	parameters.
	 */
	public void emit(Object... args)
	{
		this.notify(args);
	}
	
	/**
	 * Emit a signal on the event handler with a set of parameters.
	 * 
	 * @param args	parameters.
	 */
	@SuppressWarnings("unchecked")
	public void notify(Object... args)
	{
		List<Object> lFunctStructList;
		Boolean conditionMatched = true;
		List<Object> condition;
		List<Object> fArgs = new ArrayList<Object>();
		
		for (Object c : args)
		{
			fArgs.add(c);
		}
		
		this.lockListProcess(fArgs);
		
		listMutex.acquire();
		if (functStructList.size() == 0)
		{
			listMutex.release();
			return;
		}
		lFunctStructList = new ArrayList<Object>(functStructList);
		listMutex.release();
		
		for (int idx = 0; idx < lFunctStructList.size(); idx++)
		{
			Hashtable<Object,Object> nFunct = (Hashtable)lFunctStructList.get(idx);
			
			if (nFunct != null)
			{
				conditionMatched = true;
				condition = (List)nFunct.get("condition");
				
				if (condition.size() > 0)
				{
					if (condition.size() != fArgs.size())
					{
						conditionMatched = false;
					}
					else
					{
						for (int i = 0; i < condition.size(); i++)
						{
							if (condition.get(i) != null)
							{
								if (!condition.get(i).toString().equals(fArgs.get(i).toString()))
								{
									conditionMatched = false;
									break;
								}
							}
						}
					}
				}
				
				if (conditionMatched)
				{
					this.run(nFunct, idx, fArgs);
				}
			}
		}
	}
	
	/*
	 * Check if the locks needs to be released.
	 */
	@SuppressWarnings("unchecked")
	private void lockListProcess(List<Object> fArgs)
	{
		Double currentTime = System.currentTimeMillis() / 1000.0;
		Boolean conditionMatched = true;
		
		lockMutex.acquire();
		for (int i = 0; i < lockList.size(); i++)
		{
			Hashtable lockStruct = (Hashtable)lockList.get(i);
			Double timeout = (Double)lockStruct.get("timeout");
			Double startTime = (Double)lockStruct.get("startTime");
			SLock mutex = (SLock)lockStruct.get("mutex");
			List<Object> condition = (List)lockStruct.get("condition");
			
			if (timeout <= (currentTime - startTime))
			{
				lockStruct.put("result", false);
				mutex.release();
				continue;
			}
			
			conditionMatched = true;
			
			if (condition != null)
			{
				if (condition.size() != fArgs.size())
				{
					continue;
				}
				for (int j = 0; j < condition.size(); j++)
				{
					if ((condition.get(j) == null) ||
						(fArgs.get(j) == null))
					{
						continue;
					}
					String c1 = condition.get(j).toString();
					String a1 = fArgs.get(j).toString();
					if (!c1.equals(a1))
					{
						conditionMatched = false;
						break;
					}
				}
			}
			
			if (!conditionMatched)
			{
				continue;
			}
			else
			{
				lockStruct.put("result", true);
				mutex.release();
			}
		}
		lockMutex.release();
	}
	
	/**
	 * Synchronize a condition with a specific event.
	 * 
	 * @param timeout		maximal delay to wait.
	 * @param condition		object array of the condition.
	 * @return	the success of the waiting.
	 */
	public Boolean waitCondition(Double timeout, Object... condition)
	{
		Boolean result = false;
		Double currentTime = System.currentTimeMillis() / 1000.0;
		SLock mutex = new SLock();
		Hashtable<Object,Object> lockStruct = new Hashtable<Object,Object>();
		List<Object> mCondition = new ArrayList<Object>();
		
		for (Object c : condition)
		{
			mCondition.add(c);
		}
		
		lockStruct.put("condition", mCondition);
		lockStruct.put("result", result);
		lockStruct.put("mutex", mutex);
		lockStruct.put("startTime", currentTime);
		lockStruct.put("timeout", timeout);
		
		lockMutex.acquire();
		lockList.add(lockStruct);
		lockMutex.release();
		
		mutex.acquireTimeout(timeout);
		
		lockMutex.acquire();
		try
		{
			lockList.remove(lockStruct);
		}
		catch (Exception e) {}
		lockMutex.release();
		
		return (Boolean)lockStruct.get("result");
	}
	
	/**
	 * Clear all pending wait.
	 */
	@SuppressWarnings("unchecked")
	public void clearPending()
	{
		lockMutex.acquire();
		for (int i = 0; i < lockList.size(); i++)
		{
			Hashtable lockStruct = (Hashtable)lockList.get(i);
			SLock mutex = (SLock)lockStruct.get("mutex");
			lockStruct.put("result", false);
			mutex.release();
		}
		lockList.clear();
		lockMutex.release();
	}
}

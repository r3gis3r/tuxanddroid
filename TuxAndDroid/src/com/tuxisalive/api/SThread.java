package com.tuxisalive.api;

import java.lang.reflect.Method;

public class SThread extends Thread 
{
	private Object pSender;
	private String pMethod;
	private Object[] pArgs;
	
	public SThread (Object sender, String method, Object... args)
	{
		pSender = sender;
		pMethod = method;
		pArgs = args;
	}
	
	public void run()
	{
		Class<?> targetClass = pSender.getClass();
		Class<?> args[] = new Class[pArgs.length];
		
		for (int i = 0; i < pArgs.length; i++)
		{
			args[i] = pArgs[i].getClass();
			
			if (pArgs[i] != null)
			{
				args[i] = pArgs[i].getClass();
			}
			else
			{
				args[i] = Object.class;
			}
		}
		
		try
		{
			Method targetMethod = targetClass.getDeclaredMethod(pMethod, args);
			targetMethod.invoke(pSender, pArgs);
		}
		catch(Exception e)
		{
			System.out.println(String.format("Error when starting the thread : (%s)",
					pMethod));
		}
	}
}

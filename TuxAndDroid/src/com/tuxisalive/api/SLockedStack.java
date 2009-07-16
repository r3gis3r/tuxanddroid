package com.tuxisalive.api;

public class SLockedStack
{
	public SLock mutexPre;
	public SLock mutexIn;
	
	private SLock stackCounterMutex;
	private Integer stackCounter = 0;
	
	public SLockedStack()
	{
		stackCounterMutex = new SLock();
		mutexPre = new SLock();
		mutexIn = new SLock();
	}
	
	public void incStackCounter()
	{
		stackCounterMutex.acquire();
		stackCounter++;
		stackCounterMutex.release();
	}
	
	public void decStackCounter()
	{
		stackCounterMutex.acquire();
		stackCounter--;
		stackCounterMutex.release();
	}
	
	public Integer getStackCounter()
	{
		stackCounterMutex.acquire();
		Integer result = stackCounter;
		stackCounterMutex.release();
		return result;
	}

}

package com.tuxisalive.api;

public class SLock
{
	private Object mutex;
	private Boolean locked;
	
	public SLock()
	{
		locked = false;
		mutex = new Object();
	}
	
	public void acquire()
	{
		synchronized (mutex)
		{
			while (locked)
			{
				try
				{
					mutex.wait();
				}
				catch (Exception e) {}
			}
			locked = true;
		}
	}
	
	public void acquireTimeout(Double timeout)
	{
		Double startTime = System.currentTimeMillis() / 1000.0;
		Double currentTime;

		synchronized (mutex)
		{
			locked = true;
			while (locked)
			{
				currentTime = System.currentTimeMillis() / 1000.0;
				if (timeout <= (currentTime - startTime))
				{
					break;
				}
				else
				{
					try
					{
						mutex.wait(100);
					}
					catch (Exception e) {}
				}
			}
		}		
	}
	
	public void release()
	{
		synchronized (mutex)
		{
			mutex.notifyAll();
			locked = false;
		}
	}
}

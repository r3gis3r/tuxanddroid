package com.tuxisalive.api;

/**
 * Main module class to control Tuxdroid.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public class TuxAPI 
{
	/*
	 * Event handlers field
	 */
	private TuxEventHandlers eventHandlers;
	
	/*
	 * Public field
	 */
	public TuxAPIServer server;
	public TuxAPIEvent event;
	public TuxAPIAccess access;
	public TuxAPIStatus status;
	public TuxAPIMouthEyes mouth;
	public TuxAPIMouthEyes eyes;
	public TuxAPIFlippers flippers;
	public TuxAPISpinning spinning;
	public TuxAPIDongleRadio dongle;
	public TuxAPIDongleRadio radio;
	public TuxAPIAttitune attitune;
	public TuxAPILed led;
	public TuxAPITTS tts;
	public TuxAPISoundFlash soundFlash;
	public TuxAPIButton button;
	public TuxAPIWav wav;
	
	/**
	 * Constructor of the class.
	 * 
	 * @param host host of the server.
	 * @param port port of the server.
	 */
	public TuxAPI(String host, Integer port)
	{
		// Event handlers
		eventHandlers = new TuxEventHandlers();
		fillEventHandlers();
		// Client of server
		server = new TuxAPIServer(this, host, port);
		server.registerEventOnConnected(this, "onServerConnected");
		server.registerEventOnDisconnected(this, "onServerDisconnected");
		// Event
		event = new TuxAPIEvent(this);
		// Access
		access = new TuxAPIAccess(this);
		// Status
		status = new TuxAPIStatus(this);
		// Mouth
		mouth = new TuxAPIMouthEyes(this, TuxAPIConst.ST_NAME_MOUTH_POSITION,
				TuxAPIConst.ST_NAME_MOUTH_RM, "mouth");
		// Eyes
		eyes = new TuxAPIMouthEyes(this, TuxAPIConst.ST_NAME_EYES_POSITION,
				TuxAPIConst.ST_NAME_EYES_RM, "eyes");
		// Flippers
		flippers = new TuxAPIFlippers(this);
		// Spinning
		spinning = new TuxAPISpinning(this);
		// Dongle
		dongle = new TuxAPIDongleRadio(this, TuxAPIConst.ST_NAME_DONGLE_PLUG);
		// Radio
		radio = new TuxAPIDongleRadio(this, TuxAPIConst.ST_NAME_RADIO_STATE);
		// Attitune
		attitune = new TuxAPIAttitune(this);
		// Led
		led = new TuxAPILed(this);
		// TTS
		tts = new TuxAPITTS(this);
		// Sound flash
		soundFlash = new TuxAPISoundFlash(this);
		// Button
		button = new TuxAPIButton(this);
		// Wav
		wav = new TuxAPIWav(this);
	}
	
	/**
	 * Destructor of the class.
	 */
	public void destroy()
	{
		wav.destroy();
		button.destroy();
		soundFlash.destroy();
		tts.destroy();
		led.destroy();
		attitune.destroy();
		radio.destroy();
		dongle.destroy();
		spinning.destroy();
		flippers.destroy();
		eyes.destroy();
		mouth.destroy();
		status.destroy();
		access.destroy();
		event.destroy();
		server.destroy();
		eventHandlers.destroy();
	}
	
	/*
	 * 
	 */
	private void fillEventHandlers()
	{
		int i;
		
		for (i = 0; i < TuxAPIConst.SW_NAME_DRIVER.length; i++)
		{
			eventHandlers.insert(TuxAPIConst.SW_NAME_DRIVER[i]);
		}
		for (i = 0; i < TuxAPIConst.SW_NAME_OSL.length; i++)
		{
			eventHandlers.insert(TuxAPIConst.SW_NAME_OSL[i]);
		}
		for (i = 0; i < TuxAPIConst.SW_NAME_API.length; i++)
		{
			eventHandlers.insert(TuxAPIConst.SW_NAME_API[i]);
		}
		for (i = 0; i < TuxAPIConst.SW_NAME_EXTERNAL.length; i++)
		{
			eventHandlers.insert(TuxAPIConst.SW_NAME_EXTERNAL[i]);
		}
	}
	
	/**
	 * Get the event handlers of the API.
	 * 
	 * @return the event handlers object.
	 */
	public TuxEventHandlers getEventHandlers()
	{
		return eventHandlers;
	}
	
	/*
	 * 
	 */
	protected void onServerConnected(Boolean value, Double delay)
	{
		event.start();
	}
	
	/*
	 * 
	 */
	protected void onServerDisconnected(Boolean value, Double delay)
	{
		event.stop();
	}
}

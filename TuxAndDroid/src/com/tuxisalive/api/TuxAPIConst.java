package com.tuxisalive.api;

/**
 * Interface which contains all constants of the API.
 * 
 * @author 		Remi Jocaille
 * @version		0.0.3
 */
public interface TuxAPIConst {
	/*
	 * Client levels
	 */
	public static final Integer CLIENT_LEVEL_ANONYME        = -1;
	public static final Integer CLIENT_LEVEL_FREE           = 0;
	public static final Integer CLIENT_LEVEL_RESTRICTED     = 1;
	public static final Integer CLIENT_LEVEL_ROOT           = 2;
	public static final Integer[] CLIENT_LEVELS = {
		CLIENT_LEVEL_ANONYME,
		CLIENT_LEVEL_FREE,
		CLIENT_LEVEL_RESTRICTED,
		CLIENT_LEVEL_ROOT
	};
	/*
	 * Access priority levels
	 */
	public static final Integer ACCESS_PRIORITY_LOW         = 0;
	public static final Integer ACCESS_PRIORITY_NORMAL      = 1;
	public static final Integer ACCESS_PRIORITY_HIGH        = 2;
	public static final Integer ACCESS_PRIORITY_CRITICAL    = 3;
	public static final Integer[] ACCESS_PRIORITIES = {
		ACCESS_PRIORITY_LOW,
		ACCESS_PRIORITY_NORMAL,
		ACCESS_PRIORITY_HIGH,
		ACCESS_PRIORITY_CRITICAL
	};
	/*
	 * Statuses declaration
	 */
	// From libtuxdriver
	public static final String ST_NAME_HEAD_BUTTON         = "head_button";
	public static final String ST_NAME_LEFT_BUTTON         = "left_wing_button";
	public static final String ST_NAME_RIGHT_BUTTON        = "right_wing_button";
	public static final String ST_NAME_REMOTE_BUTTON       = "remote_button";
	public static final String ST_NAME_MOUTH_POSITION      = "mouth_position";
	public static final String ST_NAME_MOUTH_RM            = "mouth_remaining_movements";
	public static final String ST_NAME_EYES_POSITION       = "eyes_position";
	public static final String ST_NAME_EYES_RM             = "eyes_remaining_movements";
	public static final String ST_NAME_FLIPPERS_POSITION   = "flippers_position";
	public static final String ST_NAME_FLIPPERS_RM         = "flippers_remaining_movements";
	public static final String ST_NAME_SPINNING_DIRECTION  = "spinning_direction";
	public static final String ST_NAME_SPINNING_RM         = "spinning_remaining_movements";
	public static final String ST_NAME_DONGLE_PLUG         = "dongle_plug";
	public static final String ST_NAME_RADIO_STATE         = "radio_state";
	public static final String ST_NAME_LEFT_LED            = "left_led_state";
	public static final String ST_NAME_RIGHT_LED           = "right_led_state";
	public static final String ST_NAME_AUDIO_FLASH_PLAY    = "audio_flash_play";
	public static final String ST_NAME_SOUND_REFLASH_END   = "sound_reflash_end";
	public static final String ST_NAME_EYES_MOTOR_ON       = "eyes_motor_on";
	public static final String ST_NAME_MOUTH_MOTOR_ON      = "mouth_motor_on";
	public static final String ST_NAME_FLIPPERS_MOTOR_ON   = "flippers_motor_on";
	public static final String ST_NAME_SPIN_LEFT_MOTOR_ON  = "spin_left_motor_on";
	public static final String ST_NAME_SPIN_RIGHT_MOTOR_ON = "spin_right_motor_on";
	public static final String ST_NAME_FLASH_SOUND_COUNT   = "sound_flash_count";
	public static final String[] SW_NAME_DRIVER = {
		ST_NAME_FLIPPERS_POSITION,
		ST_NAME_FLIPPERS_RM,
		ST_NAME_SPINNING_DIRECTION,
		ST_NAME_SPINNING_RM,
	    ST_NAME_LEFT_BUTTON,
	    ST_NAME_RIGHT_BUTTON,
	    ST_NAME_HEAD_BUTTON,
	    ST_NAME_REMOTE_BUTTON,
	    ST_NAME_MOUTH_POSITION,
	    ST_NAME_MOUTH_RM,
	    ST_NAME_EYES_POSITION ,
	    ST_NAME_EYES_RM,
	    "descriptor_complete",
	    ST_NAME_RADIO_STATE,
	    ST_NAME_DONGLE_PLUG,
	    "charger_state",
	    "battery_level",
	    "battery_state",
	    "light_level",
	    ST_NAME_LEFT_LED ,
	    ST_NAME_RIGHT_LED ,
	    "connection_quality",
	    ST_NAME_AUDIO_FLASH_PLAY,
	    "audio_general_play",
	    "flash_programming_current_track",
	    "flash_programming_last_track_size",
	    "tuxcore_symbolic_version",
	    "tuxaudio_symbolic_version",
	    "fuxusb_symbolic_version",
	    "fuxrf_symbolic_version",
	    "tuxrf_symbolic_version",
	    "driver_symbolic_version",
	    "sound_reflash_begin",
	    ST_NAME_SOUND_REFLASH_END,
	    ST_NAME_EYES_MOTOR_ON,
	    ST_NAME_MOUTH_MOTOR_ON,
	    ST_NAME_FLIPPERS_MOTOR_ON,
	    ST_NAME_SPIN_LEFT_MOTOR_ON,
	    ST_NAME_SPIN_RIGHT_MOTOR_ON,
	    ST_NAME_FLASH_SOUND_COUNT
	};
	// From libtuxosl
	public static final String ST_NAME_SPEAK_STATUS        = "tts_0_speak_status";
	public static final String ST_NAME_TTS_SOUND_STATE     = "tts_0_sound_state";
	public static final String ST_NAME_VOICE_LIST          = "tts_0_voice_list";
	public static final String ST_NAME_WAV_CHANNEL_START   = "tts_wav_channel_start";
	public static final String ST_NAME_WAV_0_SOUND_STATE   = "wav_0_sound_state";
	public static final String ST_NAME_WAV_1_SOUND_STATE   = "wav_1_sound_state";
	public static final String ST_NAME_WAV_2_SOUND_STATE   = "wav_2_sound_state";
	public static final String ST_NAME_WAV_3_SOUND_STATE   = "wav_3_sound_state";
	public static final String[] SW_NAME_OSL = {
		"osl_symbolic_version",
	    "general_sound_state",
	    "wav_volume",
	    "tts_volume",
	    "tts_pitch",
	    "tts_locutor",
	    ST_NAME_WAV_0_SOUND_STATE,
	    "wav_0_pause_state",
	    "wav_0_stop",
	    ST_NAME_WAV_1_SOUND_STATE,
	    "wav_1_pause_state",
	    "wav_1_stop",
	    ST_NAME_WAV_2_SOUND_STATE,
	    "wav_2_pause_state",
	    "wav_2_stop",
	    ST_NAME_WAV_3_SOUND_STATE,
	    "wav_3_pause_state",
	    "wav_3_stop",
	    ST_NAME_TTS_SOUND_STATE,
	    "tts_0_pause_state",
	    "tts_0_stop",
	    "tts_0_voice_loaded",
	    ST_NAME_SPEAK_STATUS,
	    ST_NAME_VOICE_LIST,
	    ST_NAME_WAV_CHANNEL_START,
	};
	// From TuxAPI
	public static final String ST_NAME_API_CONNECT         = "api_connect";
	public static final String[] SW_NAME_API = {
		ST_NAME_API_CONNECT,
	};
	// From External
	public static final String ST_NAME_EXTERNAL_STATUS     = "external_status";
	public static final String[] SW_NAME_EXTERNAL = {
		ST_NAME_EXTERNAL_STATUS,
	};
	/*
	 * Possible string values of statuses.
	 */
	public static final String SSV_NDEF            = "NDEF";
	public static final String SSV_OPEN            = "OPEN";
	public static final String SSV_CLOSE           = "CLOSE";
	public static final String SSV_UP              = "UP";
	public static final String SSV_DOWN            = "DOWN";
	public static final String SSV_LEFT            = "LEFT";
	public static final String SSV_RIGHT           = "RIGHT";
	public static final String SSV_ON              = "ON";
	public static final String SSV_OFF             = "OFF";
	public static final String SSV_CHANGING        = "CHANGING";
	// Mouth and eyes positions
	public static final String[] SSV_MOUTHEYES_POSITIONS = {
		SSV_NDEF,
		SSV_OPEN,
	    SSV_CLOSE,
	};
	// Flippers positions
	public static final String[] SSV_FLIPPERS_POSITIONS = {
		SSV_NDEF,
	    SSV_UP,
	    SSV_DOWN,
	};
	// Spinning directions
	public static final String[] SSV_SPINNING_DIRECTIONS = {
	    SSV_NDEF,
	    SSV_LEFT,
	    SSV_RIGHT,
	};
	// Led states
	public static final String[] SSV_LED_STATES = {
	    SSV_ON,
	    SSV_OFF,
	    SSV_CHANGING,
	};

	/*
	 * Speed values
	 */
	public static final Integer SPV_VERYSLOW     = 1;
	public static final Integer SPV_SLOW         = 2;
	public static final Integer SPV_NORMAL      = 3;
	public static final Integer SPV_FAST        = 4;
	public static final Integer SPV_VERYFAST    = 5;
	public static final Integer[] SPV_SPEED_VALUES = {
	    SPV_VERYSLOW,
	    SPV_SLOW,
	    SPV_NORMAL,
	    SPV_FAST,
	    SPV_VERYFAST,
	};
	
	/*
	 * Led effects
	 */
	// Simples
	public static final Integer LFX_NONE        = 0;
	public static final Integer LFX_FADE        = 1;
	public static final Integer LFX_STEP        = 2;
	public static final Integer[] LED_EFFECT_TYPE = {
	    LFX_NONE,
	    LFX_FADE,
	    LFX_STEP,
	};
	// Extended
	public static final String LFXEX_UNAFFECTED        = "UNAFFECTED";
	public static final String LFXEX_LAST              = "LAST";
	public static final String LFXEX_NONE              = "NONE";
	public static final String LFXEX_DEFAULT           = "DEFAULT";
	public static final String LFXEX_FADE_DURATION     = "FADE_DURATION";
	public static final String LFXEX_FADE_RATE         = "FADE_RATE";
	public static final String LFXEX_GRADIENT_NBR      = "GRADIENT_NBR";
	public static final String LFXEX_GRADIENT_DELTA    = "GRADIENT_DELTA";
	public static final String[] LED_EFFECT_TYPE_EX_NAMES = {
		LFXEX_UNAFFECTED,
		LFXEX_LAST,
		LFXEX_NONE,
		LFXEX_DEFAULT,
		LFXEX_FADE_DURATION,
		LFXEX_FADE_RATE,
		LFXEX_GRADIENT_NBR,
		LFXEX_GRADIENT_DELTA,
	};
	/*
	 * Led names
	 */
	public static final String LED_NAME_BOTH           = "LED_BOTH";
	public static final String LED_NAME_RIGHT          = "LED_RIGHT";
	public static final String LED_NAME_LEFT           = "LED_LEFT";
	
	/*
	 * Sound reflash errors
	 */
	public static final String SOUND_REFLASH_NO_ERROR              = "NO_ERROR";
	public static final String SOUND_REFLASH_ERROR_RF_OFFLINE      = "ERROR_RF_OFFLINE";
	public static final String SOUND_REFLASH_ERROR_WAV             = "ERROR_WAV";
	public static final String SOUND_REFLASH_ERROR_USB             = "ERROR_USB";
	public static final String SOUND_REFLASH_ERROR_PARAMETERS      = "ERROR_PARAMETERS";
	public static final String SOUND_REFLASH_ERROR_BUSY            = "ERROR_BUSY";
	public static final String SOUND_REFLASH_ERROR_BADWAVFILE      = "ERROR_BADWAVFILE";
	public static final String SOUND_REFLASH_ERROR_WAVSIZEEXCEDED  = "ERROR_WAVSIZEEXCEDED";

	/*
	 * Wav channels
	 */
	public static final String[] WAV_CHANNELS_NAME_LIST = {
	    ST_NAME_WAV_0_SOUND_STATE,
	    ST_NAME_WAV_1_SOUND_STATE,
	    ST_NAME_WAV_2_SOUND_STATE,
	    ST_NAME_WAV_3_SOUND_STATE,
	};
	
	/*
	 * Remote keys
	 */
    public static final String K_0             = "K_0";
    public static final String K_1             = "K_1";
    public static final String K_2             = "K_2";
    public static final String K_3             = "K_3";
    public static final String K_4             = "K_4";
    public static final String K_5             = "K_5";
    public static final String K_6             = "K_6";
    public static final String K_7             = "K_7";
    public static final String K_8             = "K_8";
    public static final String K_9             = "K_9";
    public static final String K_STANDBY       = "K_STANDBY";
    public static final String K_MUTE          = "K_MUTE";
    public static final String K_VOLUMEPLUS    = "K_VOLUMEPLUS";
    public static final String K_VOLUMEMINUS   = "K_VOLUMEMINUS";
    public static final String K_ESCAPE        = "K_ESCAPE";
    public static final String K_YES           = "K_YES";
    public static final String K_NO            = "K_NO";
    public static final String K_BACKSPACE     = "K_BACKSPACE";
    public static final String K_STARTVOIP     = "K_STARTVOIP";
    public static final String K_RECEIVECALL   = "K_RECEIVECALL";
    public static final String K_HANGUP        = "K_HANGUP";
    public static final String K_STAR          = "K_STAR";
    public static final String K_SHARP         = "K_SHARP";
    public static final String K_RED           = "K_RED";
    public static final String K_GREEN         = "K_GREEN";
    public static final String K_BLUE          = "K_BLUE";
    public static final String K_YELLOW        = "K_YELLOW";
    public static final String K_CHANNELPLUS   = "K_CHANNELPLUS";
    public static final String K_CHANNELMINUS  = "K_CHANNELMINUS";
    public static final String K_UP            = "K_UP";
    public static final String K_DOWN          = "K_DOWN";
    public static final String K_LEFT          = "K_LEFT";
    public static final String K_RIGHT         = "K_RIGHT";
    public static final String K_OK            = "K_OK";
    public static final String K_FASTREWIND    = "K_FASTREWIND";
    public static final String K_FASTFORWARD   = "K_FASTFORWARD";
    public static final String K_PLAYPAUSE     = "K_PLAYPAUSE";
    public static final String K_STOP          = "K_STOP";
    public static final String K_RECORDING     = "K_RECORDING";
    public static final String K_PREVIOUS      = "K_PREVIOUS";
    public static final String K_NEXT          = "K_NEXT";
    public static final String K_MENU          = "K_MENU";
    public static final String K_MOUSE         = "K_MOUSE";
    public static final String K_ALT           = "K_ALT";
    public static final String K_RELEASED      = "RELEASED";
    
    public static final String[] REMOTE_KEY_LIST = {
        K_0,
        K_1,
        K_2,
        K_3,
        K_4,
        K_5,
        K_6,
        K_7,
        K_8,
        K_9,
        K_STANDBY,
        K_MUTE,
        K_VOLUMEPLUS,
        K_VOLUMEMINUS,
        K_ESCAPE,
        K_YES,
        K_NO,
        K_BACKSPACE,
        K_STARTVOIP,
        K_RECEIVECALL,
        K_HANGUP,
        K_STAR,
        K_SHARP,
        K_RED,
        K_GREEN,
        K_BLUE,
        K_YELLOW,
        K_CHANNELPLUS,
        K_CHANNELMINUS,
        K_UP,
        K_DOWN,
        K_LEFT,
        K_RIGHT,
        K_OK,
        K_FASTREWIND,
        K_FASTFORWARD,
        K_PLAYPAUSE,
        K_STOP,
        K_RECORDING,
        K_PREVIOUS,
        K_NEXT,
        K_MENU,
        K_MOUSE,
        K_ALT,
        K_RELEASED,
    };
}

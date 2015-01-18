package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;


enum Sounds { 
	WELCOME,
	
	BRICK_HIT, 
	SOLID_BRICK_HIT, 
	PADDLE_HIT, 
	WALL_HIT,	
	BALL_LOST,
	BONUS_BALL,
	POWERUP_SPAWNED,	
	BRICK_DESTROYED,
	BULLET_FIRED,
	GRAPPLING_HOOK_LOOP,
	SPACEBOMB_LAUNCH,
	SPACEBOMB_EXPLOSION,
	
	ACTION_DENIED,
	QUIT,
}

enum Musics { 
//	INTRO, 
	BACKGROUND,
	BACKGROUND_LO,
	BACKGROUND_HI,
//	OUTRO 
}

enum MusicPitch {
	NORMAL,
	LOW,
	HIGH
}

public final class SoundLayer {
	private static final SoundLayer instance = new SoundLayer();
	
	private final Map<Sounds, SoundSprite> soundMap = new HashMap<Sounds, SoundSprite>();
	private final Map<Musics, MusicStream> musicMap = new HashMap<Musics, MusicStream>();

//	private MusicPitch currentMusicPitch = MusicPitch.NORMAL;
	
	private static MusicStream currentMusic;
	
	private static boolean musicPlaying = false;
	
	
	public SoundLayer() {
		
	}
	
	public static SoundLayer getInstance() {
		return instance;
	}

	private void loadSounds() {
		soundMap.put(Sounds.WELCOME, SoundLoader.getInstance().getSound(ResourceMapper.getPath("welcome.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.BRICK_HIT, SoundLoader.getInstance().getSound(ResourceMapper.getPath("brick_hit.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.SOLID_BRICK_HIT, SoundLoader.getInstance().getSound(ResourceMapper.getPath("solid_brick_hit.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.PADDLE_HIT, SoundLoader.getInstance().getSound(ResourceMapper.getPath("paddle_hit.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.WALL_HIT, SoundLoader.getInstance().getSound(ResourceMapper.getPath("wall_hit.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.BALL_LOST, SoundLoader.getInstance().getSound(ResourceMapper.getPath("ball_lost.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.BONUS_BALL, SoundLoader.getInstance().getSound(ResourceMapper.getPath("bonus_ball.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.POWERUP_SPAWNED, SoundLoader.getInstance().getSound(ResourceMapper.getPath("powerup_spawned.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.BRICK_DESTROYED, SoundLoader.getInstance().getSound(ResourceMapper.getPath("brick_destroyed.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.BULLET_FIRED, SoundLoader.getInstance().getSound(ResourceMapper.getPath("bullet_fired.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.GRAPPLING_HOOK_LOOP, SoundLoader.getInstance().getSound(ResourceMapper.getPath("grappling_hook.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.SPACEBOMB_LAUNCH, SoundLoader.getInstance().getSound(ResourceMapper.getPath("spacebomb_launch.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.SPACEBOMB_EXPLOSION, SoundLoader.getInstance().getSound(ResourceMapper.getPath("spacebomb_explosion.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.ACTION_DENIED, SoundLoader.getInstance().getSound(ResourceMapper.getPath("denied.ogg", ResourceType.SOUND)));
		soundMap.put(Sounds.QUIT, SoundLoader.getInstance().getSound(ResourceMapper.getPath("quit.ogg", ResourceType.SOUND)));
	}
	
	private void loadMusics() {
		musicMap.put(Musics.BACKGROUND, MusicLoader.getInstance().getMusic(ResourceMapper.getPath("music.ogg", ResourceType.MUSIC)));
		musicMap.put(Musics.BACKGROUND_LO, MusicLoader.getInstance().getMusic(ResourceMapper.getPath("music_lo_pitch.ogg", ResourceType.MUSIC)));
		musicMap.put(Musics.BACKGROUND_HI, MusicLoader.getInstance().getMusic(ResourceMapper.getPath("music_hi_pitch.ogg", ResourceType.MUSIC)));
	}
	
	public void reloadSoundsAndMusics() {
		stopAllMusic();
		
//		dispose();
		
		soundMap.clear();
		musicMap.clear();
		
		loadSounds();
		loadMusics();
	}

	public static void playSound(Sounds sound) {
		playSound(sound, 1.0f, 1.0f, false);
	}
	
	public static void playSound(Sounds sound, float pitch) {
		playSound(sound, pitch, 1.0f, false);
	}
	
	public static void loopSound(Sounds sound) {
		playSound(sound, 1.0f, 1.0f, true);
	}
	
	public static void loopSound(Sounds sound, float pitch) {
		playSound(sound, pitch, 1.0f, true);
	}
	
	public static void stopLoop(Sounds sound) {
		Logger.debug("Stopping sound: " + sound);
		
		final SoundSprite s = SoundLayer.getInstance().soundMap.get(sound);
		if (s != null)
			s.stop();
	}
	
	public static void playSound(Sounds sound, float pitch, float gain, boolean loop) {
		Logger.debug("Playing sound: " + sound);
		
		if (!Config.getInstance().isSoundMuted()) {
			final SoundSprite s = SoundLayer.getInstance().soundMap.get(sound);
			if (s != null) 
	//			if (loop)
	//				s.loop(pitch, gain);
	//			else
	//				s.play(pitch, gain);
				
				s.play(pitch, gain);
		}
	}
	
	public static void playMusic(Musics music) {
		Logger.debug("Playing music: " + music);
		
		if (!Config.getInstance().isMusicMuted()) {
			final MusicStream m = SoundLayer.getInstance().musicMap.get(music);
			if (m != null) {
				m.play();
				
				currentMusic = m;
				
				musicPlaying = true;
			}
		}
	}
	
	public static boolean isMusicPlaying() {
		return musicPlaying;
	}
	
	public void stopMusic(Musics music) {
		final MusicStream m = SoundLayer.getInstance().musicMap.get(music);
		if (m != null) {
			m.stop();
			
			currentMusic = null;
			
			musicPlaying = false;
		}
	}
	
	public void stopAllMusic()
	{
		for (final MusicStream m : musicMap.values()) {
			m.stop();
		}
		
		currentMusic = null;
		
		musicPlaying = false;
	}

	public void changeMusicPitch(MusicPitch toPitch) {
		if (!Config.getInstance().isMusicMuted() && isMusicPlaying()) {
			float pos = 0.0f; 		
			if (currentMusic != null) {
				
	//			switch (currentMusicPitch) {
	//			case NORMAL:
	//				pos = currentMusic.getPosition();
	//				break;
	//			
	//			case HIGH:
	//				pos = currentMusic.getPosition() * 0.25926f;
	//				break;
	//				
	//			case LOW:
	//				pos = currentMusic.getPosition() * 0.35f;
	//				break;
	//
	//			default:
	//				throw new RuntimeException("Unsupported Music Pitch!");
	//			}
				
				pos = currentMusic.getPosition();
				
				stopAllMusic();
			}
			
			MusicStream m;		
			
			switch (toPitch) {
			case NORMAL:
				m = SoundLayer.getInstance().musicMap.get(Musics.BACKGROUND);
				m.playAt(pos);
				
				musicPlaying = true;
				break;
				
			case LOW:
				m = SoundLayer.getInstance().musicMap.get(Musics.BACKGROUND_LO);
				m.playAt(pos);
				
				musicPlaying = true;
				break;
				
			case HIGH:
				m = SoundLayer.getInstance().musicMap.get(Musics.BACKGROUND_HI);
				m.playAt(pos);
				
				musicPlaying = true;
				break;
				
			default:
				throw new RuntimeException("Unsupported Music Pitch!");
			}
			
	//		currentMusicPitch = toPitch;
			currentMusic = m;
		}
	}

	public void dispose() {
		for (final SoundSprite ss : soundMap.values()) {
			ss.dispose();
//			soundMap.remove(ss);
		}
		
		for (final MusicStream ms : musicMap.values()) {
			ms.dispose();
//			musicMap.remove(ms);
		}
	}	
}
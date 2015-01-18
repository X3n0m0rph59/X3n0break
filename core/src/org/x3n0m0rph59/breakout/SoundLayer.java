package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


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

final class SoundSprite {
	private final Sound sound;
	
	public SoundSprite(String filename) {		
		sound = Gdx.audio.newSound(Gdx.files.internal(ResourceMapper.getPath(filename, ResourceType.SOUND)));
	}
	
	public void play(float pitch, float volume) {
		sound.play(volume, pitch, 0);
	}
	
	public void stop() {		
		sound.stop();
	}

	public void dispose() {
		sound.dispose();
	}
}

final class MusicStream {
	private final Music music;
	
	public MusicStream(String filename) {		
		music = Gdx.audio.newMusic(Gdx.files.internal(ResourceMapper.getPath(filename, ResourceType.MUSIC)));		
	}
	
	public void play() {
		music.play();
		music.setLooping(true);
	}
	
	public void playAt(float pos) {				
		this.play();
		
		// TODO: Bug in libGDX, why is this needed ??
//		while (!music.isPlaying())
//			;
		
		music.setPosition(pos);
		
		Logger.debug("Music pos: " + pos);
	}
	
	public void stop() {
		if (music.isPlaying())
			music.stop();
	}
	
	public boolean isPlaying() {
		return music.isPlaying();
	}
	
	public float getPosition() {
		final float pos = music.getPosition();
		return pos;
	}

	public void dispose() {
		music.dispose();
	}	
}

public final class SoundLayer {
	private static final SoundLayer instance = new SoundLayer();
	
	private final Map<Sounds, SoundSprite> soundMap = new HashMap<Sounds, SoundSprite>();
	private final Map<Musics, MusicStream> musicMap = new HashMap<Musics, MusicStream>();

//	private MusicPitch currentMusicPitch = MusicPitch.NORMAL;
	
	private static MusicStream currentMusic;
	
	
	public SoundLayer() {
		loadSounds();
		loadMusics();
	}
	
	public static SoundLayer getInstance() {
		return instance;
	}

	private void loadSounds() {
		soundMap.put(Sounds.WELCOME, new SoundSprite("welcome.ogg"));
		soundMap.put(Sounds.BRICK_HIT, new SoundSprite("brick_hit.ogg"));
		soundMap.put(Sounds.SOLID_BRICK_HIT, new SoundSprite("solid_brick_hit.ogg"));
		soundMap.put(Sounds.PADDLE_HIT, new SoundSprite("paddle_hit.ogg"));
		soundMap.put(Sounds.WALL_HIT, new SoundSprite("wall_hit.ogg"));
		soundMap.put(Sounds.BALL_LOST, new SoundSprite("ball_lost.ogg"));
		soundMap.put(Sounds.BONUS_BALL, new SoundSprite("bonus_ball.ogg"));
		soundMap.put(Sounds.POWERUP_SPAWNED, new SoundSprite("powerup_spawned.ogg"));
		soundMap.put(Sounds.BRICK_DESTROYED, new SoundSprite("brick_destroyed.ogg"));
		soundMap.put(Sounds.BULLET_FIRED, new SoundSprite("bullet_fired.ogg"));
		soundMap.put(Sounds.GRAPPLING_HOOK_LOOP, new SoundSprite("grappling_hook.ogg"));
		soundMap.put(Sounds.SPACEBOMB_LAUNCH, new SoundSprite("spacebomb_launch.ogg"));
		soundMap.put(Sounds.SPACEBOMB_EXPLOSION, new SoundSprite("spacebomb_explosion.ogg"));
		soundMap.put(Sounds.ACTION_DENIED, new SoundSprite("denied.ogg"));
		soundMap.put(Sounds.QUIT, new SoundSprite("quit.ogg"));
	}
	
	private void loadMusics() {
		musicMap.put(Musics.BACKGROUND, new MusicStream("music.ogg"));
		musicMap.put(Musics.BACKGROUND_LO, new MusicStream("music_lo_pitch.ogg"));
		musicMap.put(Musics.BACKGROUND_HI, new MusicStream("music_hi_pitch.ogg"));
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
			}
		}
	}
	
	public void stopMusic(Musics music) {
		final MusicStream m = SoundLayer.getInstance().musicMap.get(music);
		if (m != null) {
			m.stop();
			
			currentMusic = null;
		}
	}
	
	public void stopAllMusic()
	{
		for (final MusicStream m : musicMap.values()) {
			m.stop();
		}
		
		currentMusic = null;
	}

	public void changeMusicPitch(MusicPitch toPitch) {
		if (!Config.getInstance().isMusicMuted()) {
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
				break;
				
			case LOW:
				m = SoundLayer.getInstance().musicMap.get(Musics.BACKGROUND_LO);
				m.playAt(pos);
				break;
				
			case HIGH:
				m = SoundLayer.getInstance().musicMap.get(Musics.BACKGROUND_HI);
				m.playAt(pos);
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
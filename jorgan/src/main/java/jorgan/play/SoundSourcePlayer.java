/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.play;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiUnavailableException;

import jorgan.disposition.*;
import jorgan.disposition.event.*;
import jorgan.play.sound.*;

public class SoundSourcePlayer extends Player {

  /**
   * The factory for sounds.
   */
  private SoundFactory factory;

  private Map sounds = new HashMap();
  
  public SoundSourcePlayer(SoundSource soundSource) {
    super(soundSource);
  }

  /**
   * Aquire soundFactory.
   */
  protected void openImpl() {
    SoundSource soundSource = (SoundSource)getElement();

    if (soundSource.getDevice() != null) {
      PlayerProblem errorDevice = new PlayerProblem(PlayerProblem.ERROR, "device", soundSource.getDevice()); 
      PlayerProblem errorType   = new PlayerProblem(PlayerProblem.ERROR, "type", soundSource.getType()); 
      try {
        factory = SoundFactory.instance(soundSource.getDevice(), soundSource.getType());
        factory.setBank(soundSource.getBank());
        factory.setSamples(soundSource.getSamples());
        
        removeProblem(errorDevice);
        removeProblem(errorType);
      } catch (MidiUnavailableException ex) {
        addProblem   (errorDevice);
        removeProblem(errorType);
      } catch (IllegalArgumentException ex) {
        addProblem   (errorType);
        removeProblem(errorDevice);
      }
    }
  }

  /**
   * Release soundFactory.
   */
  protected void closeImpl() {
    if (factory != null) {
      factory.close();
      factory = null;
      
      sounds.clear();
    }
  }

  public void elementChanged(OrganEvent event) {
    SoundSource soundSource = (SoundSource)getElement();

    PlayerProblem problem = new PlayerProblem(PlayerProblem.WARNING, "device", null); 
    if (soundSource.getDevice() == null && Configuration.instance().getWarnSoundSourceWithoutDevice()) {
      addProblem(problem);
    } else {
      removeProblem(problem);
    }
  }

  /**
   * Create a sound from the aquired soundFactory.
   * 
   * @param program		the program to use for the sound
   */
  public Sound createSound(int program) {

    SoundSource soundSource = (SoundSource)getElement();

    Sound sound = null;
    if (factory != null) {
      SoundSourceSound soundSourceSound = (SoundSourceSound)sounds.get(new Integer(program));
      if (soundSourceSound == null) {
        sound = factory.createSound();
        if (sound != null) {
          soundSourceSound = new SoundSourceSound(program, sound);
        }
      }
        
      if (soundSourceSound != null) {
        soundSourceSound.init();
      }
      sound = soundSourceSound;
      
      if (sound != null && soundSource.getDelay() != 0) {
        sound = new DelayedSound(sound, soundSource.getDelay()); 
      }
    }
    return sound;
  }

  private class SoundSourceSound extends SoundWrapper {

    private int program;
    private int initCount = 0;
    
    public SoundSourceSound(int program, Sound sound) {
      super(sound);
      
      this.program = program;          
            
      sounds.put(new Integer(program), this);            
    }
    
    public void init() {
      initCount++;
    }
    
    public void stop() {
      initCount--;

      if (initCount == 0){
        super.stop();
        
        sounds.remove(new Integer(program));
      }
    }
  }
}
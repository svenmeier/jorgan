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

import java.util.*;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import jorgan.config.ConfigurationEvent;
import jorgan.config.ConfigurationListener;
import jorgan.disposition.*;
import jorgan.disposition.event.*;
import jorgan.play.event.*;
import jorgan.sound.midi.BugFix;

/**
 * A play of an organ.
 */
public class OrganPlay  {

  public final Object LOCK = new Object();
  
  private boolean output = false;
  private boolean input  = false;

  private boolean open;
  
  /**
   * Element to player mapping.
   */
  private Map players = new HashMap();

  /**
   * The handler of organ and configuration events.
   */
  private EventHandler eventHandler = new EventHandler();
  
  /**
   * All registered playerListeners.
   */
  private List listeners = new ArrayList();
  
  private Organ organ;
  
  /**
   * Creates a new organ player.
   *
   * @param organ   the organ to play
   */
  public OrganPlay(Organ organ) {
    this.organ = organ;

    organ.addOrganListener(eventHandler);

    for (int e = 0; e < organ.getElementCount(); e++) {
        createPlayer(organ.getElement(e));
    }

    Configuration.instance().addConfigurationListener(eventHandler);
  }

  public Organ getOrgan() {
    return organ;
  }
  
  public void addPlayerListener(PlayListener listener) {
    listeners.add(listener);
  }

  public void removePlayerListener(PlayListener listener) {
    listeners.remove(listener);
  }

  public void dispose() {

    organ.removeOrganListener(eventHandler);
    organ = null;

    Configuration.instance().removeConfigurationListener(eventHandler);
    
    listeners.clear();
  }

  protected void firePlayerAdded(Player player) {
    assertNoLock();

    if (listeners != null) {
      PlayEvent event = new PlayEvent(this, player.getElement());
      for (int l = 0; l < listeners.size(); l++) {
        PlayListener listener = (PlayListener)listeners.get(l);
        listener.playerAdded(event);
      }
    }
  }

  protected void firePlayerRemoved(Player player) {
    assertNoLock();
    
    if (listeners != null) {
      PlayEvent event = new PlayEvent(this, player.getElement());
      for (int l = 0; l < listeners.size(); l++) {
        PlayListener listener = (PlayListener)listeners.get(l);
        listener.playerRemoved(event);
      }
    }
  }

  protected void fireProblemAdded(Player player, PlayerProblem problem) {
    assertNoLock();
    
    if (listeners != null) {
      PlayEvent event = new PlayEvent(this, player.getElement(), problem);
      for (int l = 0; l < listeners.size(); l++) {
        PlayListener listener = (PlayListener)listeners.get(l);
        listener.problemAdded(event);
      }
    }
  }

  protected void fireProblemRemoved(Player player, PlayerProblem problem) {
    assertNoLock();
    
    if (listeners != null) {
      PlayEvent event = new PlayEvent(this, player.getElement(), problem);
      for (int l = 0; l < listeners.size(); l++) {
        PlayListener listener = (PlayListener)listeners.get(l);
        listener.problemRemoved(event);
      }
    }
  }

  /**
   * Mark input to be notified to listeners on next call to
   * {@link #flushIO()}.
   */
  protected void markInput() {
    input = true;
  }
  
  /**
   * Mark output to be notified to listeners on next call to
   * {@link #flushIO()}.
   */
  protected void markOutput() {
    output = true;
  }

  /**
   * Flush possible input and output to registered listeners.
   */
  private void flushIO() {
    if (input || output) {
      if (listeners != null) {
        for (int l = 0; l < listeners.size(); l++) {
          PlayListener listener = (PlayListener)listeners.get(l);
          listener.io(input, output);
        }      
      }
      output = false;
      input  = false;
    }
  }

  private void assertNoLock() {
    if (Thread.holdsLock(LOCK)) {
      throw new Error("illegal hold of lock");
    };
  }
  
  protected Player getPlayer(Element element) {
    return (Player)players.get(element);
  }
  
  public boolean hasErrors(Element element) {
  	Player player = getPlayer(element);
  	if (player == null) {
  	  return false;
  	}
    
    return player.hasErrors();
  }

  public boolean hasWarnings(Element element) {
    Player player = getPlayer(element);
    if (player == null) {
      return false;
    }
   
    return player.hasWarnings();
  }

  public List getProblems(Element element) {
    Player player = getPlayer(element);
    if (player == null) {
      return null;
    }
     
    return player.getProblems();
  }
  
  public void open() {
    open = true;

    Iterator iterator = players.values().iterator();
    while (iterator.hasNext()) {
      Player player = (Player)iterator.next();
      player.open();
    }
    
    iterator = players.values().iterator();
    while (iterator.hasNext()) {
      Player player = (Player)iterator.next();
      player.elementChanged(null);
    }
  }
  
  public boolean isOpen() {
    return open; 
  }
  
  public void close() {
    Iterator iterator = players.values().iterator();
    while (iterator.hasNext()) {
      Player player = (Player)iterator.next();
      player.close();
    }
    
    open = false;
  }

  protected void createPlayer(Element element) {
    Player player = null;

    if (element instanceof Keyboard) {
      player = new KeyboardPlayer((Keyboard)element);
    }
    if (element instanceof SoundSource) {
      player = new SoundSourcePlayer((SoundSource)element);
    }
    if (element instanceof Console) {
      player = new ConsolePlayer((Console)element);
    }
    if (element instanceof Stop) {
      player = new StopPlayer((Stop)element);
    }
    if (element instanceof Coupler) {
      player = new CouplerPlayer((Coupler)element);
    }
    if (element instanceof Swell) {
      player = new SwellPlayer((Swell)element);
    }
    if (element instanceof Tremulant) {
      player = new TremulantPlayer((Tremulant)element);
    }
    if (element instanceof Variation) {
      player = new VariationPlayer((Variation)element);
    }
    if (element instanceof Piston) {
      player = new PistonPlayer((Piston)element);
    }

    if (player != null) {
        player.setOrganPlay(this);
        players.put(element, player);
        
        firePlayerAdded(player);
        
        player.elementChanged(null);
    }
  }

  protected void dropPlayer(Element element) {
    Player player = (Player)players.get(element);
    if (player != null) {
      players.remove(element);
  
      firePlayerRemoved(player);
    }
  }
  
  /**
   * Create a receiver that forwards all received messages to
   * the given player.<br>
   * All forwarding is synchronized on this organPlay to
   * avoid race conditions.
   *   
   * @param player		player to forward messages to
   * @return			receiver
   * @see    jorgan.play.Player#input(javax.sound.midi.ShortMessage)
   */
  protected Receiver createReceiver(final Player player) {
  	return new Receiver() {
      public void close() { }

      public void send(MidiMessage message, long timestamp) {
        if (message instanceof ShortMessage) {
          ShortMessage shortMessage = (ShortMessage)message;

          int status = BugFix.getStatus(shortMessage);
          if (status != ShortMessage.ACTIVE_SENSING &&
              status != ShortMessage.TIMING_CLOCK) {

            markInput();

            player.input(shortMessage);

            flushIO();
          }
        }
      }
    };
  }

  private class EventHandler extends OrganAdapter implements ConfigurationListener {

    public void elementChanged(OrganEvent event) {
      assertNoLock();
      
      Player player = getPlayer(event.getElement());
      if (player != null) {
        player.elementChanged(event);
      }
      
      flushIO();
    }

    public void elementAdded(OrganEvent event) {
      assertNoLock();

      createPlayer(event.getElement());

      flushIO();
    }

    public void elementRemoved(OrganEvent event) {
      assertNoLock();

      dropPlayer(event.getElement());

      flushIO();
    }
    
    public void configurationChanged(ConfigurationEvent ev) {
        Iterator iterator = players.values().iterator();
        while (iterator.hasNext()) {
          Player player = (Player)iterator.next();
          player.elementChanged(null);
        }
      }
      
    public void configurationBackup(ConfigurationEvent event) { }
  }
}
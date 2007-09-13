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
package jorgan.play.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sound that delays all messages.
 */
public class DelayedSound extends SoundWrapper {
    
    private static final Logger logger = Logger.getLogger(DelayedSound.class.getName());

    private static DelayedInvoker invoker = new DelayedInvoker();

    private int delay;

    /**
     * Create a delayed sound.
     * 
     * @param sound
     *            sound to wrap
     * @param delay
     *            delay of sound
     */
    public DelayedSound(Sound sound, int delay) {
        super(sound);

        this.delay = delay;
    }

    @Override
	public void noteOff(final int pitch) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.noteOff(pitch);
            }
        };
    }

    @Override
	public void noteOn(final int pitch, final int velocity) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.noteOn(pitch, velocity);
            }
        };
    }

    @Override
	public void setCutoff(final int cutoff) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.setCutoff(cutoff);
            }
        };
    }

    @Override
	public void setModulation(final int amplitude, final int frequency) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.setModulation(amplitude, frequency);
            }
        };
    }

    @Override
	public void setPan(final int pan) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.setPan(pan);
            }
        };
    }

    public void setBend(final int bend) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.setPitchBend(bend);
            }
        };
    }

    @Override
	public void setProgram(final int program) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.setProgram(program);
            }
        };
    }

    @Override
	public void setVolume(final int volume) {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.setVolume(volume);
            }
        };
    }

    @Override
	public void stop() {
        new DelayedInvocation() {
            @Override
			public void now() {
                sound.stop();
            }
        };
    }

    private abstract class DelayedInvocation implements Comparable {

        private long when;

        public DelayedInvocation() {
            when = System.currentTimeMillis() + delay;

            invoker.delay(this);
        }

        public int compareTo(Object object) {
            DelayedInvocation invocation = (DelayedInvocation) object;

            if (this.when < invocation.when) {
                return -1;
            } else if (this.when > invocation.when) {
                return 1;
            }
            return 0;
        }

        public abstract void now();
    }

    private static class DelayedInvoker implements Runnable {

        private List<DelayedInvocation> invocations = new ArrayList<DelayedInvocation>();

        public DelayedInvoker() {
            Thread thread = new Thread(this, "DelayedSoundInvoker");
            thread.setDaemon(true);
            thread.start();
        }

        /**
         * Schedule an invocation for delayed invocation.
         * 
         * @param invocation
         *            invocation to schedule
         */
        public synchronized void delay(DelayedInvocation invocation) {
            int index = 0;
            while (index < invocations.size()
                    && invocations.get(index)
                            .compareTo(invocation) <= 0) {
                index++;
            }
            invocations.add(index, invocation);
            notify();
        }

        public synchronized void run() {
            while (true) {
                try {
                    if (invocations.size() == 0) {
                        wait();
                    } else {
                        DelayedInvocation invocation = invocations
                                .get(0);

                        long timeout = invocation.when
                                - System.currentTimeMillis();
                        if (timeout <= 0) {
                            invocations.remove(0);

                            try {
                                invocation.now();
                            } catch (RuntimeException ex) {
                                logger.log(Level.INFO, "invocation failed", ex);
                            }
                        } else {
                            wait(timeout);
                        }
                    }
                } catch (InterruptedException ex) {
                    throw new Error("unexpected interruption", ex);
                }
            }
        }
    }
}
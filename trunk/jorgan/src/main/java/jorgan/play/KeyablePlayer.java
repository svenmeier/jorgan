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

import jorgan.disposition.Keyable;
import jorgan.disposition.event.OrganEvent;

/**
 * A player for a keyable.
 */
public abstract class KeyablePlayer<E extends Keyable> extends SwitchPlayer<E> {

	private static final int KEY_MAX = 127;
	
    private static final int ACTIVATE_VELOCITY = 100;

    /**
     * The currently pressed keys.
     */
    private int[] pressedKeys = new int[KEY_MAX + 1];

    private Action action;

    protected KeyablePlayer(E keyable) {
        super(keyable);
    }

    @Override
	protected void openImpl() {
        Keyable keyable = getElement();

        switch (keyable.getAction()) {
        case Keyable.ACTION_STRAIGHT:
            action = new Action();
            break;
        case Keyable.ACTION_PITCH_CONSTANT:
            action = new ConstantPitchAction();
            break;
        case Keyable.ACTION_PITCH_HIGHEST:
            action = new HighestPitchAction();
            break;
        case Keyable.ACTION_PITCH_LOWEST:
            action = new LowestPitchAction();
            break;
        case Keyable.ACTION_SUSTAIN:
            action = new SustainAction();
            break;
        case Keyable.ACTION_SOSTENUTO:
            action = new SostenutoAction();
            break;
        default:
            throw new IllegalStateException("unexpected keyable action '" + keyable.getAction()
                    + "'");
        }
    }

    @Override
	protected void closeImpl() {
        super.closeImpl();

        action = null;

        for (int p = 0; p < pressedKeys.length; p++) {
            pressedKeys[p] = 0;
        }
    }

    protected abstract void activateKey(int pitch, int velocity);

    protected abstract void deactivateKey(int pitch);

    public void keyDown(int pitch, int velocity) {

        Keyable keyable = getElement();

        pitch += keyable.getTranspose();

        if (pitch >= 0 && pitch <= 127) {
            if (pressedKeys[pitch] == 0) {
                action.keyDown(pitch, velocity);
            }
            pressedKeys[pitch]++;
        }
    }

    public void keyUp(int pitch) {
        Keyable keyable = getElement();

        pitch += keyable.getTranspose();
        if (pitch >= 0 && pitch <= 127) {
            pressedKeys[pitch]--;
            if (pressedKeys[pitch] == 0) {
                action.keyUp(pitch);
            }
        }
    }

    @Override
	public void elementChanged(OrganEvent event) {
        super.elementChanged(event);

        if (isOpen()) {
            action.changed();
        }
    }

    private class Action {
        protected boolean activated = false;

        public void changed() {
            if (getElement().isEngaged()) {
                if (!activated) {
                    activated = true;
                    activated();
                }
            } else {
                if (activated) {
                    deactivated();
                    activated = false;
                }
            }
        }

        public void keyDown(int pitch, int velocity) {
            if (activated) {
                KeyablePlayer.this.activateKey(pitch, velocity);
            }
        }

        public void keyUp(int pitch) {
            if (activated) {
                KeyablePlayer.this.deactivateKey(pitch);
            }
        }

        public void activated() {
            for (int p = 0; p < pressedKeys.length; p++) {
                if (pressedKeys[p] > 0) {
                    KeyablePlayer.this.activateKey(p, ACTIVATE_VELOCITY);
                }
            }
        }

        public void deactivated() {
            for (int p = 0; p < pressedKeys.length; p++) {
                if (pressedKeys[p] > 0) {
                    KeyablePlayer.this.deactivateKey(p);
                }
            }
        }
    }

    private class HighestPitchAction extends Action {
        @Override
		public void keyDown(int pitch, int velocity) {
            if (activated) {
                int highest = getHighestPitch();
                if (highest == -1 || pitch > highest) {
                    if (highest != -1) {
                        KeyablePlayer.this.deactivateKey(highest);
                    }
                    KeyablePlayer.this.activateKey(pitch, velocity);
                }
            }
        }

        @Override
		public void keyUp(int pitch) {
            if (activated) {
                int highest = getHighestPitch();
                if (pitch > highest || highest == -1) {
                    KeyablePlayer.this.deactivateKey(pitch);
                    if (highest != -1) {
                        KeyablePlayer.this.activateKey(highest,
                                ACTIVATE_VELOCITY);
                    }
                }
            }
        }

        @Override
		public void activated() {
            int highest = getHighestPitch();
            if (highest != -1) {
                KeyablePlayer.this.activateKey(highest, ACTIVATE_VELOCITY);
            }
        }

        @Override
		public void deactivated() {
            int highest = getHighestPitch();
            if (highest != -1) {
                KeyablePlayer.this.deactivateKey(highest);
            }
        }

        private int getHighestPitch() {
            for (int p = pressedKeys.length - 1; p >= 0; p--) {
                if (pressedKeys[p] > 0) {
                    return p;
                }
            }
            return -1;
        }
    }

    private class LowestPitchAction extends Action {
        @Override
		public void keyDown(int pitch, int velocity) {
            if (activated) {
                int lowest = getLowestPitch();
                if (lowest == -1 || pitch < lowest) {
                    if (lowest != -1) {
                        KeyablePlayer.this.deactivateKey(lowest);
                    }
                    KeyablePlayer.this.activateKey(pitch, velocity);
                }
            }
        }

        @Override
		public void keyUp(int pitch) {
            if (activated) {
                int lowest = getLowestPitch();
                if (pitch < lowest || lowest == -1) {
                    KeyablePlayer.this.deactivateKey(pitch);
                    if (lowest != -1) {
                        KeyablePlayer.this.activateKey(lowest,
                                ACTIVATE_VELOCITY);
                    }
                }
            }
        }

        @Override
		public void activated() {
            int lowest = getLowestPitch();
            if (lowest != -1) {
                KeyablePlayer.this.activateKey(lowest, ACTIVATE_VELOCITY);
            }
        }

        @Override
		public void deactivated() {
            int lowest = getLowestPitch();
            if (lowest != -1) {
                KeyablePlayer.this.deactivateKey(lowest);
            }
        }

        private int getLowestPitch() {
            for (int p = 0; p < pressedKeys.length; p++) {
                if (pressedKeys[p] > 0) {
                    return p;
                }
            }
            return -1;
        }
    }

    private class ConstantPitchAction extends Action {
        @Override
		public void keyDown(int pitch, int velocity) {
            if (activated) {
                if (!hasPitch()) {
                    Keyable keyable = getElement();

                    KeyablePlayer.this.activateKey(60 + keyable.getTranspose(),
                            velocity);
                }
            }
        }

        @Override
		public void keyUp(int pitch) {
            if (activated) {
                if (!hasPitch()) {
                    Keyable keyable = getElement();

                    KeyablePlayer.this.deactivateKey(60 + keyable
                            .getTranspose());
                }
            }
        }

        @Override
		public void activated() {
            if (hasPitch()) {
                Keyable keyable = getElement();
                KeyablePlayer.this.activateKey(60 + keyable.getTranspose(),
                        ACTIVATE_VELOCITY);
            }
        }

        @Override
		public void deactivated() {
            if (hasPitch()) {
                Keyable keyable = getElement();
                KeyablePlayer.this.deactivateKey(60 + keyable.getTranspose());
            }
        }

        private boolean hasPitch() {
            for (int p = 0; p < pressedKeys.length; p++) {
                if (pressedKeys[p] > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    private class SustainAction extends SostenutoAction {

        @Override
		public void keyDown(int pitch, int velocity) {
            super.keyDown(pitch, velocity);

            if (activated) {
                stuckKeys[pitch] = true;
            }
        }
    }

    private class SostenutoAction extends Action {
        protected boolean[] stuckKeys = new boolean[128];

        @Override
		public void keyDown(int pitch, int velocity) {
            if (!activated || !stuckKeys[pitch]) {
                KeyablePlayer.this.activateKey(pitch, velocity);
            }
        }

        @Override
		public void keyUp(int pitch) {
            if (!activated || !stuckKeys[pitch]) {
                KeyablePlayer.this.deactivateKey(pitch);
            }
        }

        @Override
		public void activated() {
            for (int k = 0; k < pressedKeys.length; k++) {
                stuckKeys[k] = pressedKeys[k] > 0;
            }
        }

        @Override
		public void deactivated() {
            for (int k = 0; k < pressedKeys.length; k++) {
                if (stuckKeys[k] && pressedKeys[k] == 0) {
                    KeyablePlayer.this.deactivateKey(k);
                }
            }
        }
    }
}
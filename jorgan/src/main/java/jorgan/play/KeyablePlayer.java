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
import jorgan.util.Null;

/**
 * A player for a keyable.
 */
public abstract class KeyablePlayer<E extends Keyable> extends SwitchPlayer<E> {

	private int[] pressedKeys = new int[128];

	private int[] velocities = new int[128];

	private Action action;

	private Boolean engaged;

	protected KeyablePlayer(E keyable) {
		super(keyable);
	}

	@Override
	protected void openImpl() {
		super.openImpl();

		this.engaged = null;

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
		case Keyable.ACTION_INVERSE:
			action = new InverseAction();
			break;
		default:
			throw new IllegalStateException("unexpected keyable action '"
					+ keyable.getAction() + "'");
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

	protected abstract void onKeyDown(int pitch, int velocity);

	protected abstract void onKeyUp(int pitch);

	public final void keyDown(int pitch, int velocity) {

		Keyable keyable = getElement();

		pitch += keyable.getTranspose();

		if (pitch >= 0 && pitch <= 127) {
			velocities[pitch] = velocity;

			if (pressedKeys[pitch] == 0) {
				action.keyDown(pitch, velocity);
			}
			pressedKeys[pitch]++;
		}
	}

	public final void keyUp(int pitch) {
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
	public void update() {
		super.update();

		if (isOpen()) {
			boolean engaged = getElement().isEngaged();
			if (!Null.safeEquals(this.engaged, engaged)) {
				if (engaged) {
					action.engaged();
				} else {
					action.disengaged();
				}
				this.engaged = engaged;
			}
		}
	}

	private class Action {

		public void keyDown(int pitch, int velocity) {
			if (engaged) {
				KeyablePlayer.this.onKeyDown(pitch, velocity);
			}
		}

		public void keyUp(int pitch) {
			if (engaged) {
				KeyablePlayer.this.onKeyUp(pitch);
			}
		}

		public void engaged() {
			for (int p = 0; p < pressedKeys.length; p++) {
				if (pressedKeys[p] > 0) {
					KeyablePlayer.this.onKeyDown(p, velocities[p]);
				}
			}
		}

		public void disengaged() {
			for (int p = 0; p < pressedKeys.length; p++) {
				if (pressedKeys[p] > 0) {
					KeyablePlayer.this.onKeyUp(p);
				}
			}
		}
	}

	private class InverseAction extends Action {
		public void keyDown(int pitch, int velocity) {
			if (!engaged) {
				KeyablePlayer.this.onKeyDown(pitch, velocity);
			}
		}

		public void keyUp(int pitch) {
			if (!engaged) {
				KeyablePlayer.this.onKeyUp(pitch);
			}
		}

		public void engaged() {
			super.disengaged();
		}

		public void disengaged() {
			super.engaged();
		}
	}

	private class HighestPitchAction extends Action {
		@Override
		public void keyDown(int pitch, int velocity) {
			if (engaged) {
				int highest = getHighestPitch();
				if (highest == -1 || pitch > highest) {
					if (highest != -1) {
						KeyablePlayer.this.onKeyUp(highest);
					}
					KeyablePlayer.this.onKeyDown(pitch, velocity);
				}
			}
		}

		@Override
		public void keyUp(int pitch) {
			if (engaged) {
				int highest = getHighestPitch();
				if (pitch > highest || highest == -1) {
					KeyablePlayer.this.onKeyUp(pitch);
					if (highest != -1) {
						KeyablePlayer.this.onKeyDown(highest,
								velocities[highest]);
					}
				}
			}
		}

		@Override
		public void engaged() {
			int highest = getHighestPitch();
			if (highest != -1) {
				KeyablePlayer.this.onKeyDown(highest, velocities[highest]);
			}
		}

		@Override
		public void disengaged() {
			int highest = getHighestPitch();
			if (highest != -1) {
				KeyablePlayer.this.onKeyUp(highest);
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
			if (engaged) {
				int lowest = getLowestPitch();
				if (lowest == -1 || pitch < lowest) {
					if (lowest != -1) {
						KeyablePlayer.this.onKeyUp(lowest);
					}
					KeyablePlayer.this.onKeyDown(pitch, velocity);
				}
			}
		}

		@Override
		public void keyUp(int pitch) {
			if (engaged) {
				int lowest = getLowestPitch();
				if (pitch < lowest || lowest == -1) {
					KeyablePlayer.this.onKeyUp(pitch);
					if (lowest != -1) {
						KeyablePlayer.this
								.onKeyDown(lowest, velocities[lowest]);
					}
				}
			}
		}

		@Override
		public void engaged() {
			int lowest = getLowestPitch();
			if (lowest != -1) {
				KeyablePlayer.this.onKeyDown(lowest, velocities[lowest]);
			}
		}

		@Override
		public void disengaged() {
			int lowest = getLowestPitch();
			if (lowest != -1) {
				KeyablePlayer.this.onKeyUp(lowest);
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

		private int velocity;

		@Override
		public void keyDown(int pitch, int velocity) {
			if (engaged) {
				if (!hasPitch()) {
					this.velocity = velocity;

					Keyable keyable = getElement();

					KeyablePlayer.this.onKeyDown(60 + keyable.getTranspose(),
							velocity);
				}
			}
		}

		@Override
		public void keyUp(int pitch) {
			if (engaged) {
				if (!hasPitch()) {
					Keyable keyable = getElement();

					pitch = 60 + keyable.getTranspose();
					KeyablePlayer.this.onKeyUp(pitch);
				}
			}
		}

		@Override
		public void engaged() {
			if (hasPitch()) {
				Keyable keyable = getElement();

				int pitch = 60 + keyable.getTranspose();
				KeyablePlayer.this.onKeyDown(pitch, velocity);
			}
		}

		@Override
		public void disengaged() {
			if (hasPitch()) {
				Keyable keyable = getElement();
				KeyablePlayer.this.onKeyUp(60 + keyable.getTranspose());
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

			if (engaged) {
				stuckKeys[pitch] = true;
			}
		}
	}

	private class SostenutoAction extends Action {
		protected boolean[] stuckKeys = new boolean[128];

		@Override
		public void keyDown(int pitch, int velocity) {
			if (!engaged || !stuckKeys[pitch]) {
				KeyablePlayer.this.onKeyDown(pitch, velocity);
			}
		}

		@Override
		public void keyUp(int pitch) {
			if (!engaged || !stuckKeys[pitch]) {
				KeyablePlayer.this.onKeyUp(pitch);
			}
		}

		@Override
		public void engaged() {
			for (int k = 0; k < pressedKeys.length; k++) {
				stuckKeys[k] = pressedKeys[k] > 0;
			}
		}

		@Override
		public void disengaged() {
			for (int k = 0; k < pressedKeys.length; k++) {
				if (stuckKeys[k] && pressedKeys[k] == 0) {
					KeyablePlayer.this.onKeyUp(k);
				}
			}
		}
	}
}
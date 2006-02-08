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

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Console;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganEvent;
import jorgan.sound.midi.DevicePool;

/**
 * A player of an console.
 */
public class ConsolePlayer extends Player {

    private static final Problem warningDevice = new Problem(Problem.WARNING,
            "device");

    private static final Problem errorDevice = new Problem(Problem.ERROR,
            "device");

    /**
     * The midiDevice to receive input from.
     */
    private MidiDevice in;

    /**
     * The transmitter of the opened midiDevice.
     */
    private Transmitter transmitter;

    public ConsolePlayer(Console console) {
        super(console);
    }

    protected void openImpl() {
        Console console = (Console) getElement();

        removeProblem(errorDevice);

        String device = console.getDevice();
        if (device != null) {
            try {
                in = DevicePool.getMidiDevice(device, false);
                in.open();

                transmitter = in.getTransmitter();
                transmitter.setReceiver(getOrganPlay().createReceiver(this));
            } catch (MidiUnavailableException ex) {
                addProblem(errorDevice.value(device));
            }
        }
    }

    protected void closeImpl() {
        if (transmitter != null) {
            transmitter.close();
            in.close();

            transmitter = null;
            in = null;
        }
    }

    public void elementChanged(OrganEvent event) {
        Console console = (Console) getElement();

        if (console.getDevice() == null
                && Configuration.instance().getWarnWithoutDevice()) {
            removeProblem(errorDevice);
            addProblem(warningDevice.value(null));
        } else {
            removeProblem(warningDevice);
        }
    }

    protected void input(ShortMessage message) {
        Console console = (Console) getElement();

        for (int r = 0; r < console.getReferenceCount(); r++) {
            Reference reference = console.getReference(r);

            Player player = getOrganPlay().getPlayer(reference.getElement());
            if (player != null) {
                player.messageReceived(message);
            }
        }
    }
}
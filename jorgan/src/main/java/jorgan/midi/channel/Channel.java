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
package jorgan.midi.channel;

import javax.sound.midi.ShortMessage;

/**
 * A channel.
 * 
 * <table>
 * <tr>
 * <td>UNUSED_DATA</td>
 * <td>0</td>
 * </tr>
 * <tr>
 * <td>CONTROL_BANK_SELECT_MSB</td>
 * <td>0</td>
 * </tr>
 * <tr>
 * <td>CONTROL_MODULATION</td>
 * <td>1</td>
 * </tr>
 * <tr>
 * <td>CONTROL_NRPN_MSB_DATA</td>
 * <td>6</td>
 * </tr>
 * <tr>
 * <td>CONTROL_VOLUME</td>
 * <td>7</td>
 * </tr>
 * <tr>
 * <td>CONTROL_PAN</td>
 * <td>10</td>
 * </tr>
 * <tr>
 * <td>CONTROL_GENERAL_PURPOSE_1</td>
 * <td>16</td>
 * </tr>
 * <tr>
 * <td>CONTROL_GENERAL_PURPOSE_2</td>
 * <td>17</td>
 * </tr>
 * <tr>
 * <td>CONTROL_GENERAL_PURPOSE_3</td>
 * <td>18</td>
 * </tr>
 * <tr>
 * <td>CONTROL_GENERAL_PURPOSE_4</td>
 * <td>19</td>
 * </tr>
 * <tr>
 * <td>CONTROL_BANK_SELECT_LSB</td>
 * <td>32</td>
 * </tr>
 * <tr>
 * <td>CONTROL_NRPN_LSB_DATA</td>
 * <td>38</td>
 * </tr>
 * <tr>
 * <td>CONTROL_BRIGHTNESS</td>
 * <td>74</td>
 * </tr>
 * <tr>
 * <td>CONTROL_REVERB</td>
 * <td>91</td>
 * </tr>
 * <tr>
 * <td>CONTROL_CHORUS</td>
 * <td>93</td>
 * </tr>
 * <tr>
 * <td>CONTROL_RESET_ALL</td>
 * <td>121</td>
 * </tr>
 * <tr>
 * <td>CONTROL_NRPN_MSB</td>
 * <td>99</td>
 * </tr>
 * <tr>
 * <td>CONTROL_NRPN_LSB</td>
 * <td>98</td>
 * </tr>
 * <tr>
 * <td>CONTROL_ALL_NOTES_OFF</td>
 * <td>123</td>
 * </tr>
 * <tr>
 * <td>NOTE_OFF</td>
 * <td>128</td>
 * </tr>
 * <tr>
 * <td>NOTE_ON</td>
 * <td>144</td>
 * </tr>
 * <tr>
 * <td>CONTROL_CHANGE</td>
 * <td>176</td>
 * </tr>
 * <tr>
 * <td>PROGRAM_CHANGE</td>
 * <td>192</td>
 * </tr>
 * <tr>
 * <td>PITCH_BEND</td>
 * <td>224</td>
 * </tr>
 * </table>
 */
public interface Channel {

	/**
	 * Send a message.
	 * 
	 * @param message
	 *            message
	 */
	public void sendMessage(ShortMessage message);

	public void release();
}
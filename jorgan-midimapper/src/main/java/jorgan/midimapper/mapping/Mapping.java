package jorgan.midimapper.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;

import jorgan.midi.Direction;

public class Mapping {

	private String name;

	private String device;

	private Direction direction;

	private List<Mapper> mappers = new ArrayList<Mapper>();

	public Mapping(String name, Direction direction, String device) {
		this.name = name;
		this.direction = direction;
		this.device = device;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDevice() {
		return device;
	}

	public List<Mapper> getMappers() {
		return mappers;
	}

	public void map(MidiMessage message, Callback callback) {
		for (Mapper mapper : mappers) {
			mapper.match(message, callback);
		}
	}
}
package jorgan.midimapper.mapping;

import java.util.ArrayList;
import java.util.List;

import jorgan.midi.Direction;
import jorgan.midi.mpl.ProcessingException;

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

	public void addMapper(Mapper mapper) {
		this.mappers.add(mapper);
	}

	public void map(byte[] datas, Callback callback) throws ProcessingException {
		for (Mapper mapper : mappers) {
			mapper.map(datas, callback);
		}
	}
}
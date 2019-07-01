package jorgan.customizer.builder;

import java.util.Arrays;

import jorgan.midi.mpl.Tuple;

public class MomentaryBuilder implements TupleBuilder {

	private byte[] datas = null;

	@Override
	public boolean analyse(byte[] newDatas) {
		datas = Arrays.copyOf(newDatas, newDatas.length);

		return false;
	}

	private boolean isDecided() {
		return datas != null;
	}

	@Override
	public Tuple decide() {
		if (isDecided()) {
			return Tuple.equal(datas);
		} else {
			return null;
		}
	}
}

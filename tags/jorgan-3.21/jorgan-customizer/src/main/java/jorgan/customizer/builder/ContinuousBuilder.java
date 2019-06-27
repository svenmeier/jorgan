package jorgan.customizer.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jorgan.disposition.Continuous.Change;
import jorgan.midi.mpl.Chain;
import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Div;
import jorgan.midi.mpl.Get;
import jorgan.midi.mpl.GreaterEqual;
import jorgan.midi.mpl.LessEqual;
import jorgan.midi.mpl.Sub;
import jorgan.midi.mpl.Tuple;

public class ContinuousBuilder implements TupleBuilder {

	private byte[] datas = null;

	private int index = -1;

	private int min = 127;

	private int max = 0;

	@Override
	public boolean analyse(byte[] newDatas) {
		analyseImpl(newDatas);

		return true;
	}

	public void analyseImpl(byte[] newDatas) {

		if (datas != null) {
			if (datas.length != newDatas.length) {
				// incompatible
				return;
			}

			if (isDecided()) {
				for (int d = 0; d < datas.length; d++) {
					if (datas[d] != newDatas[d]) {
						if (index != d) {
							// incompatible
							return;
						}
					}
				}
			} else {
				int newIndex = -1;

				for (int d = 0; d < datas.length; d++) {
					if (datas[d] != newDatas[d]) {
						if (newIndex != -1) {
							// inconclusive
							return;
						}
						newIndex = d;
					}
				}

				if (newIndex == -1) {
					// no index (i.e. identical message)
					return;
				}
				index = newIndex;
			}

			min = Math.min(min, newDatas[index] & 0xff);
			max = Math.max(max, newDatas[index] & 0xff);
		}

		datas = Arrays.copyOf(newDatas, newDatas.length);
	}

	private boolean isDecided() {
		return index != -1;
	}

	@Override
	public Tuple decide() {
		if (isDecided()) {
			List<Command> commands = new ArrayList<Command>();
			if (min > 0) {
				commands.add(new GreaterEqual(min));
			}
			if (max < 127) {
				commands.add(new LessEqual(max));
			}
			if (min > 0) {
				commands.add(new Sub(min));
			}
			commands.add(new Div(max - min));
			commands.add(new Get(Change.VALUE));

			return Tuple.equal(datas).set(index, new Chain(commands));
		} else {
			return null;
		}
	}
}

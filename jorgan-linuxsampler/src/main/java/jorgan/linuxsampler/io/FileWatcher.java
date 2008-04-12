package jorgan.linuxsampler.io;

import java.util.*;
import java.io.*;

public abstract class FileWatcher extends TimerTask {

	private static Timer timer = new Timer("FileWatcher", true);

	private long timeStamp;

	private File file;

	public FileWatcher(File file) {
		this.file = file;
		this.timeStamp = file.lastModified();

		// repeat the check every second
		timer.schedule(this, new Date(), 1000);
	}

	public final void run() {
		long timeStamp = file.lastModified();

		if (this.timeStamp != timeStamp) {
			this.timeStamp = timeStamp;
			onChange(file);
		}
	}

	protected abstract void onChange(File file);
}
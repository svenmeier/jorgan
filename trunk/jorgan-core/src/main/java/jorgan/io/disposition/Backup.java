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
package jorgan.io.disposition;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A backup utility.
 */
public class Backup {

	private File file;

	private File parent;

	private List<Integer> existingNumbers;

	private int size;

	/**
	 * Create a history for the given file.
	 * 
	 * @param file
	 *            file to create history for
	 */
	public Backup(File file) {
		this.file = file;

		this.parent = file.getAbsoluteFile().getParentFile();

		existingNumbers = getExistingNumbers();
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	/**
	 * Write this backup.
	 */
	public void write(int max) {

		if (file.exists()) {

			deleteExceedingHistories(Math.max(0, max - 1));

			if (max > 0) {
				addHistory();
			}
		}
	}

	/**
	 * Clear the history.
	 */
	public void clear() {

		deleteExceedingHistories(0);
	}

	private List<Integer> getExistingNumbers() {
		final List<Integer> numbers = new ArrayList<Integer>();

		final Pattern pattern = Pattern.compile("\\."
				+ Pattern.quote(file.getName()) + "~(\\d*)");

		parent.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				Matcher matcher = pattern.matcher(name);

				if (matcher.matches()) {
					numbers.add(Integer.valueOf(matcher.group(1)));
				}

				return false;
			}
		});

		return numbers;
	}

	private File getHistory(Integer number) {
		return new File(parent, "." + file.getName() + "~" + number);
	}

	private void deleteExceedingHistories(int max) {
		Collections.sort(existingNumbers, new Comparator<Integer>() {
			public int compare(Integer integer1, Integer integer2) {
				File file1 = getHistory(integer1);
				File file2 = getHistory(integer2);

				return (int) (file2.lastModified() - file1.lastModified());
			}
		});

		for (int n = existingNumbers.size() - 1; n >= max; n--) {
			getHistory(existingNumbers.get(n)).delete();
			existingNumbers.remove(n);
		}
	}

	protected void addHistory() {

		Collections.sort(existingNumbers);

		int free = 1;
		for (int n = 0; n < existingNumbers.size(); n++) {
			Integer number = existingNumbers.get(n);
			if (number.intValue() == free) {
				free++;
			} else {
				break;
			}
		}

		file.renameTo(getHistory(new Integer(free)));
	}
}
package jorgan.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public enum LAF {

	DEFAULT {
		@Override
		public String getClassName() {
			return null;
		}
	},
	SYSTEM {
		@Override
		public String getClassName() {
			return UIManager.getSystemLookAndFeelClassName();
		}
	},
	CROSS_PLATFORM {
		@Override
		public String getClassName() {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (info.getClassName().toLowerCase().contains("nimbus")) {
					return info.getClassName();
				}
			}

			return UIManager.getCrossPlatformLookAndFeelClassName();
		}
	};

	private static Logger log = Logger.getLogger(LAF.class.getName());

	public abstract String getClassName();

	public void install() {
		String clazz = getClassName();
		try {
			if (clazz != null) {
				log.log(Level.INFO, "setting look and feel '" + clazz + "'");
				UIManager.setLookAndFeel(clazz);
			}
		} catch (Exception ex) {
			log.log(Level.WARNING, "unable to set look and feel '" + clazz
					+ "'", ex);
		}
	}
}
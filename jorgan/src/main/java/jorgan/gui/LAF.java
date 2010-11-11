package jorgan.gui;

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

	public abstract String getClassName();
}
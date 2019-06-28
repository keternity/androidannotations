package org.androidannotations.internal.x2c;

import javax.annotation.processing.Filer;

import org.androidannotations.internal.x2c.xml.LayoutManager;

public final class X2CManager {

	public static String savePackageName;
	public static final String SAVE_LAYOUT_NAME = "LayoutWithX2c_%s_%s";
	public static final String SAVE_VIEW_NAME = "ViewWithX2c_%s";

	private X2CManager() {

	}

	public static void process(Filer filer, String layoutName) {
		LayoutManager layoutManager = LayoutManager.instance();
		layoutManager.setFiler(filer);
		int mGroupId = 0;
		if (layoutManager.getLayoutId(layoutName) != null) {
			mGroupId = layoutManager.getLayoutId(layoutName) >> 24;
		}
		layoutManager.setGroupId(mGroupId);
		layoutManager.translate(layoutName);
	}

	public static String getSaveLayoutName(int groupId, String name) {
		return getJavaName(SAVE_LAYOUT_NAME, groupId, name);
	}

	public static String getSaveViewName(String name) {
		return getJavaName(SAVE_VIEW_NAME, 0, name);
	}

	private static String getJavaName(String formatString, int groupId, String name) {
		String[] ss = name.split("_");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < ss.length; i++) {
			stringBuilder.append(ss[i].substring(0, 1).toUpperCase()).append(ss[i].substring(1));
			if (i < ss.length - 1) {
				stringBuilder.append("_");
			}
		}
		name = stringBuilder.toString();
		if (name.endsWith(".xml")) {
			name = name.substring(0, name.length() - 4);
		}
		if (groupId == 0) {
			return String.format(formatString, name);
		}
		return String.format(formatString, groupId, name);
	}
}

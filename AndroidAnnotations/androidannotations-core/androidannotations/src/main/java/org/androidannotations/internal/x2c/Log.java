package org.androidannotations.internal.x2c;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * @author chengwei 2018/8/7
 */
public final class Log {
	private static Messager sMessager;

	private Log() {

	}

	public static void init(Messager msger) {
		sMessager = msger;
	}

	public static void w(String msg) {
		if (sMessager != null) {
			sMessager.printMessage(Diagnostic.Kind.OTHER, msg);
		}
	}

	public static void e(String msg) {
		if (sMessager != null) {
			sMessager.printMessage(Diagnostic.Kind.ERROR, msg);
		}
	}
}

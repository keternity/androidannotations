package org.androidannotations.internal.x2c.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chengwei 2018/8/25
 */
public class Attr implements Comparable<Attr> {

	public String format;
	public String name;
	public Map<String, String> enums = new HashMap<>();
	public Func toFunc;

	@Override
	public String toString() {
		return "Attr{" + "format='" + format + '\'' + ", name='" + name + '\'' + ", enums=" + enums + ", toFunc='" + toFunc + '\'' + '}';
	}

	@Override
	public int compareTo(Attr attr) {
		if (attr == null) {
			return 1;
		}
		if (attr.format == null && this.format != null) {
			return 1;
		}
		if (attr.enums.size() == 0 && this.enums.size() > 0) {
			return 1;
		}

		return 0;
	}
}

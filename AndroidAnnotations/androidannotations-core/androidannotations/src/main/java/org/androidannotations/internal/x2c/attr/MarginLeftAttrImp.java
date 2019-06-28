package org.androidannotations.internal.x2c.attr;

public class MarginLeftAttrImp extends AutoAttr {

	public MarginLeftAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "marginLeft";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return true;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.leftMargin = %s;", view, dataVal);
	}
}

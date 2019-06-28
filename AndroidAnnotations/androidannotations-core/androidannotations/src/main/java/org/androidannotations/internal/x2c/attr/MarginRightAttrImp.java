package org.androidannotations.internal.x2c.attr;

public class MarginRightAttrImp extends AutoAttr {

	public MarginRightAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "marginRight";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return true;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.rightMargin = %s;", view, dataVal);
	}
}

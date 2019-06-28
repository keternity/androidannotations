package org.androidannotations.internal.x2c.attr;

public class MarginBottomAttrImp extends AutoAttr {

	public MarginBottomAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "marginBottom";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return true;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.bottomMargin = %s;", view, dataVal);
	}
}

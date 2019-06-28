package org.androidannotations.internal.x2c.attr;

public class PaddingTopAttrImp extends AutoAttr {

	public PaddingTopAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "paddingTop";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return false;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.setPadding(%s,%s,%s,%s);", view, String.format("%s.getPaddingLeft()", view), dataVal, String.format("%s.getPaddingRight()", view),
				String.format("%s.getPaddingBottom()", view));
	}
}

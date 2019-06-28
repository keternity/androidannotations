package org.androidannotations.internal.x2c.attr;

public class PaddingRightAttrImp extends AutoAttr {

	public PaddingRightAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "paddingRight";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return true;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.setPadding(%s,%s,%s,%s);", view, String.format("%s.getPaddingLeft()", view), String.format("%s.getPaddingTop()", view), dataVal,
				String.format("%s.getPaddingBottom()", view));
	}
}

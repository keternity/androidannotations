package org.androidannotations.internal.x2c.attr;

public class MaxWidthAttrImp extends AutoAttr {

	public MaxWidthAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "maxWidth";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return true;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.setMaxWidth(%s);", view, dataVal);
	}
}

package org.androidannotations.internal.x2c.attr;

public class MinWidthAttrImp extends AutoAttr {

	public MinWidthAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "minWidth";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return true;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.setMinWidth(%s);\n", view, dataVal);
	}
}

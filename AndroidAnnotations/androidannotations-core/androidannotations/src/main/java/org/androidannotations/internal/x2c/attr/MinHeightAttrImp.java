package org.androidannotations.internal.x2c.attr;

public class MinHeightAttrImp extends AutoAttr {

	public MinHeightAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "minHeight";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return false;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.setMinHeight(%s);\n", view, dataVal);
	}
}

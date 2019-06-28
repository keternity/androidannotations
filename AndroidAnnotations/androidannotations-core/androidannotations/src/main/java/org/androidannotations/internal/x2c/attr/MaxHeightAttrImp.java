package org.androidannotations.internal.x2c.attr;

public class MaxHeightAttrImp extends AutoAttr {

	public MaxHeightAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "maxHeight";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return false;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.setMaxHeight(%s);", view, dataVal);
	}
}

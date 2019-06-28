package org.androidannotations.internal.x2c.attr;

public class TextSizeAttrImp extends AutoAttr {

	public TextSizeAttrImp(String pxVal, String baseWidth, String baseHeight) {
		super(pxVal, baseWidth, baseHeight);
	}

	@Override
	protected String attrVal() {
		return "textSize";
	}

	@Override
	protected boolean defaultBaseWidth() {
		return true;
	}

	@Override
	protected String execute(String view, String dataVal) {
		return String.format("%s.setTextSize(TypedValue.COMPLEX_UNIT_PX,%s);", view, dataVal);
	}
}

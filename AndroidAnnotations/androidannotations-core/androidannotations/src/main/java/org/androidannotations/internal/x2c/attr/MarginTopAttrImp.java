package org.androidannotations.internal.x2c.attr;

public class MarginTopAttrImp extends AutoAttr {

    public MarginTopAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "marginTop";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return true;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.topMargin = %s;", view, dataVal);
    }
}

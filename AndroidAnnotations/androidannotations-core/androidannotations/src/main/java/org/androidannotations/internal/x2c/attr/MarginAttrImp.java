package org.androidannotations.internal.x2c.attr;

public class MarginAttrImp extends AutoAttr {

    public MarginAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "margin";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return false;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.setMargins(%s,%s,%s,%s);", view, dataVal, dataVal, dataVal, dataVal);
    }
}

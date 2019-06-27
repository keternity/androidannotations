package org.androidannotations.internal.x2c.attr;

public class PaddingAttrImp extends AutoAttr {

    public PaddingAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "padding";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return false;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.setPadding(%s,%s,%s,%s);", view, dataVal, dataVal, dataVal, dataVal);
    }
}

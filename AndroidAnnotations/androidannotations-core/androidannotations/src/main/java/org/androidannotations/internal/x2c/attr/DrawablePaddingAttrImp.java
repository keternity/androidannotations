package org.androidannotations.internal.x2c.attr;


public class DrawablePaddingAttrImp extends AutoAttr {

    public DrawablePaddingAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "drawablePadding";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return true;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.setCompoundDrawablePadding(%s);", view, dataVal);
    }
}

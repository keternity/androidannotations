package org.androidannotations.internal.x2c.attr;

public class WidthAttrImp extends AutoAttr {

    public WidthAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "layout_width";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return true;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.width = %s;", view, dataVal);
    }
}

package org.androidannotations.internal.x2c.attr;

public class HeightAttrImp extends AutoAttr {

    public HeightAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "layout_height";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return false;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.height = %s;", view, dataVal);
    }
}

package org.androidannotations.internal.x2c.attr;

public class PaddingBottomAttrImp extends AutoAttr {

    public PaddingBottomAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "paddingBottom";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return false;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.setPadding(%s,%s,%s,%s);", view, String.format("%s.getPaddingLeft()", view),
                String.format("%s.getPaddingTop()", view),
                String.format("%s.getPaddingRight()", view),
                dataVal
        );
    }
}

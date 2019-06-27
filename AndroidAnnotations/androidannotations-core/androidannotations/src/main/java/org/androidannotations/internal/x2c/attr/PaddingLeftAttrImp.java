package org.androidannotations.internal.x2c.attr;

public class PaddingLeftAttrImp extends AutoAttr {

    public PaddingLeftAttrImp(String pxVal, String baseWidth, String baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    @Override
    protected String attrVal() {
        return "paddingLeft";
    }

    @Override
    protected boolean defaultBaseWidth() {
        return true;
    }

    @Override
    protected String execute(String view, String dataVal) {
        return String.format("%s.setPadding(%s,%s,%s,%s);", view, dataVal,
                String.format("%s.getPaddingTop()", view),
                String.format("%s.getPaddingRight()", view),
                String.format("%s.getPaddingBottom()", view));
    }
}

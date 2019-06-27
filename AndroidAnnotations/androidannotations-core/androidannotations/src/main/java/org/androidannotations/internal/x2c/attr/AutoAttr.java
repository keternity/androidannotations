package org.androidannotations.internal.x2c.attr;

public abstract class AutoAttr {

    protected String pxVal;
    protected String baseWidth;
    protected String baseHeight;

    public AutoAttr(String pxVal, String baseWidth, String baseHeight) {
        this.pxVal = pxVal;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
    }

    public String build(String view) {
        StringBuilder autoCodeBuilder = new StringBuilder();
        String execute;
        switch (pxVal) {
            case "fill_parent":
                execute = execute(view, "ViewGroup.LayoutParams.FILL_PARENT");
                break;
            case "match_parent":
                execute = execute(view, "ViewGroup.LayoutParams.MATCH_PARENT");
                break;
            case "wrap":
            case "wrap_content":
                execute = execute(view, "ViewGroup.LayoutParams.WRAP_CONTENT");
                break;
            default:
                if (pxVal.startsWith("@")) {
                    execute = execute(view, String.format("(int)res.getDimension(R.dimen.%s)", pxVal.substring(pxVal.indexOf("/") + 1)));
                    break;
                }
                String unit;
                String dim;
                if (pxVal.endsWith("dp") || pxVal.endsWith("dip")) {
                    unit = "TypedValue.COMPLEX_UNIT_DIP";
                    dim = pxVal.substring(0, pxVal.indexOf("d"));
                } else if (pxVal.endsWith("sp")) {
                    unit = "TypedValue.COMPLEX_UNIT_SP";
                    dim = pxVal.substring(0, pxVal.indexOf("s"));
                } else {
                    dim = pxVal.substring(0, pxVal.indexOf("p"));
                    autoCodeBuilder.append("densityConfig = DensityConfig.getInstance();");
                    autoCodeBuilder.append("\n");
                    String val;
                    if (useDefault()) {
                        val = defaultBaseWidth() ? getPercentWidthSize(dim) : getPercentHeightSize(dim);
                    } else if (baseWidth()) {
                        val = getPercentWidthSize(dim);
                    } else {
                        val = getPercentHeightSize(dim);
                    }
                    autoCodeBuilder.append(val);
                    autoCodeBuilder.append("\n");
                    autoCodeBuilder.append("if(val > 0)");
                    autoCodeBuilder.append("\n");
                    autoCodeBuilder.append("val = Math.max(val, 1);");//for very thin divider
                    autoCodeBuilder.append("\n");
                    execute = execute(view, "val");
                    break;
                }
                execute = execute(view, String.format("(int)(TypedValue.applyDimension(%s,%s,res.getDisplayMetrics()))", unit, dim));
                break;
        }
        if (execute != null) {
            autoCodeBuilder.append(execute);
            autoCodeBuilder.append("\n");
        }
        return autoCodeBuilder.toString();
    }

    protected String getPercentWidthSize(String dim) {
        return String.format("val = densityConfig.getReallyWidth(ctx,%s);", dim);
    }

    protected String getPercentHeightSize(String dim) {
        return String.format("val = densityConfig.getReallyHeight(ctx,%s);", dim);
    }


    protected boolean baseWidth() {
        return contains(baseWidth, attrVal());
    }

    protected boolean useDefault() {
        return !contains(baseHeight, attrVal()) && !contains(baseWidth, attrVal());
    }

    protected boolean contains(String baseVal, String flag) {
        return baseVal != null && baseVal.contains(flag);
    }

    protected abstract String attrVal();

    protected abstract boolean defaultBaseWidth();

    protected abstract String execute(String view, String dataVal);


    @Override
    public String toString() {
        return "AutoAttr{" +
                "pxVal=" + pxVal +
                ", baseWidth=" + baseWidth() +
                ", defaultBaseWidth=" + defaultBaseWidth() +
                '}';
    }
}

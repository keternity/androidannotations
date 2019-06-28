package org.androidannotations.internal.x2c.view;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.androidannotations.internal.x2c.Log;
import org.androidannotations.internal.x2c.attr.AutoAttr;
import org.androidannotations.internal.x2c.attr.DrawablePaddingAttrImp;
import org.androidannotations.internal.x2c.attr.HeightAttrImp;
import org.androidannotations.internal.x2c.attr.MarginAttrImp;
import org.androidannotations.internal.x2c.attr.MarginBottomAttrImp;
import org.androidannotations.internal.x2c.attr.MarginLeftAttrImp;
import org.androidannotations.internal.x2c.attr.MarginRightAttrImp;
import org.androidannotations.internal.x2c.attr.MarginTopAttrImp;
import org.androidannotations.internal.x2c.attr.MaxHeightAttrImp;
import org.androidannotations.internal.x2c.attr.MaxWidthAttrImp;
import org.androidannotations.internal.x2c.attr.MinHeightAttrImp;
import org.androidannotations.internal.x2c.attr.MinWidthAttrImp;
import org.androidannotations.internal.x2c.attr.PaddingAttrImp;
import org.androidannotations.internal.x2c.attr.PaddingBottomAttrImp;
import org.androidannotations.internal.x2c.attr.PaddingLeftAttrImp;
import org.androidannotations.internal.x2c.attr.PaddingRightAttrImp;
import org.androidannotations.internal.x2c.attr.PaddingTopAttrImp;
import org.androidannotations.internal.x2c.attr.TextSizeAttrImp;
import org.androidannotations.internal.x2c.attr.WidthAttrImp;
import org.androidannotations.internal.x2c.xml.LayoutManager;
import org.androidannotations.internal.x2c.xml.Style;
import org.xml.sax.Attributes;

/**
 * @author chengwei 2018/8/8
 */
public class View implements ITranslator {
	private View mParent;
	private ArrayList<View> mChilds;
	private String mViewStr;
	private String mTagName;
	private String mName;
	private String mObjName;
	private String mLayoutParams;
	protected String mLayoutParamsObj;
	private Attributes mAttributes;
	private String mPackageName;
	protected Set<String> mImports;
	private Map<String, String> mStyleAttributes;
	private int mIndex;
	private String mId;
	private String mAndroidName;
	private boolean isDataBinding;
	private String mDirName;
	private String mLayoutName;
	private int mDataBindingIndex;
	private boolean isMerge = false;

	private String autoBaseWidth = "";
	private String autoBaseHeight = "";

	public View(String packageName, String name, Attributes attributes) {
		if ("merge".equals(name)) {
			isMerge = true;
		}
		this.mImports = new TreeSet<>();
		this.mPackageName = packageName;
		this.mName = getName(name);
		this.mTagName = name;
		this.mAttributes = attributes;

		this.mImports.add("android.content.res.Resources");
		this.mImports.add("android.view.View");
		this.mImports.add("android.util.TypedValue");
		this.mImports.add("android.graphics.Color");
		this.mImports.add("android.view.ViewGroup");
		this.mImports.add(String.format("%s.R", mPackageName));
		this.mImports.add("org.androidannotations.api.x2c.DensityConfig");
	}

	public boolean isMerge() {
		return isMerge;
	}

	public void setDirName(String dirName) {
		this.mDirName = dirName;
	}

	public void setIsDataBinding(boolean isDataBinding) {
		this.isDataBinding = isDataBinding;
	}

	public void setLayoutName(String layoutName) {
		this.mLayoutName = layoutName;
	}

	public void setParent(View parent) {
		this.mParent = parent;
		if (parent != null) {
			parent.addChildren(this);
		}
		mViewStr = generateView(mAttributes);
	}

	public void addChildren(View child) {
		if (mChilds == null) {
			mChilds = new ArrayList<>();
		}
		mChilds.add(child);
	}

	public void translate(StringBuilder stringBuilder) {
		stringBuilder.append(mViewStr);
		if (mChilds != null) {
			for (View view : mChilds) {
				view.translate(stringBuilder);
				mImports.addAll(view.mImports);
			}
		}
	}

	public Set<String> getImports() {
		return mImports;
	}

	private String getName(String name) {
		if (!name.contains(".")) {
			switch (name) {
			case "include":
			case "View":
				name = "android.view.View";
				break;
			case "ViewStub":
				name = "android.view.ViewStub";
				break;
			case "WebView":
				name = "android.webkit.WebView";
				break;
			case "fragment":
				name = "android.widget.FrameLayout";
				break;
			default:
				name = "android.widget." + name;
				break;
			}
		}
		if (!isMerge) {
			mImports.add(name);
		}
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public String getObjName() {
		if (mObjName == null) {
			View root = getRootView();
			if (!isMerge) {
				mObjName = mName.substring(0, 1).toLowerCase() + mName.substring(1) + root.mIndex;
			} else {
				mObjName = "viewGroup";
			}
			mLayoutParamsObj = "layoutParam" + root.mIndex;
			root.mIndex++;
		}
		return mObjName;
	}

	public String getLayoutParams() {
		if (mLayoutParams == null) {
			if (isMerge) {
				mLayoutParams = "ViewGroup" + ".LayoutParams";
			} else {
				mLayoutParams = mName + ".LayoutParams";
			}
		}
		return mLayoutParams;
	}

	private String generateView(Attributes attributes) {
		getAutoLayoutAttribute();
		StringBuilder stringBuilder = new StringBuilder();
		if (mParent == null) {
			if (isMerge) {
				stringBuilder.append("\tContext ctx = viewGroup.getContext();\n");
				stringBuilder.append("DensityConfig densityConfig = null;\n");
				stringBuilder.append("int val = 0;\n\n");
			}
			stringBuilder.append("Resources res = ctx.getResources();\n\n");
		}
		String obj = getObjName();
		if ("include".equals(mTagName)) {
			String javaName = LayoutManager.instance().translate(getIncludeLayout());
			stringBuilder.append(String.format("%s %s =(View) new %s().createView(ctx);\n", mName, obj, javaName));
		} else if (!isMerge) {
			stringBuilder.append(String.format("%s %s = new %s(ctx);\n", mName, obj, mName));
		} else {
			stringBuilder.append("\n");
			return stringBuilder.toString();
		}
		mStyleAttributes = getStyleAttribute();
		if (mParent != null) {
			String paramsName = mParent.getLayoutParams();
			mImports.add("android.view.ViewGroup");
			stringBuilder.append(String.format("%s %s = new %s(%s,%s);\n", paramsName, mLayoutParamsObj, paramsName, "ViewGroup.LayoutParams.MATCH_PARENT", "ViewGroup.LayoutParams.MATCH_PARENT"));
			AutoAttr autoAttr = new WidthAttrImp(getWidthStr(), autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(mLayoutParamsObj));
			autoAttr = new HeightAttrImp(getHeightStr(), autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(mLayoutParamsObj));
		}

		ArrayList<ITranslator> translators = createTranslator();
		if (mStyleAttributes != null) {
			for (String styleKey : mStyleAttributes.keySet()) {
				for (ITranslator translator : translators) {
					if (translator.translate(stringBuilder, styleKey, mStyleAttributes.get(styleKey))) {
						break;
					}
				}
			}
		}

		String key;
		String value;
		int n = attributes.getLength();
		for (int i = 0; i < n; i++) {
			key = attributes.getQName(i);
			value = attributes.getValue(i);
			if (value.startsWith("@{")) {
				isDataBinding = true;
				break;
			}
			for (ITranslator translator : translators) {
				if (translator.translate(stringBuilder, key, value)) {
					break;
				}
			}
		}

		for (ITranslator translator : translators) {
			translator.onAttributeEnd(stringBuilder);
		}

		if (mParent != null) {
			stringBuilder.append(String.format("%s.setLayoutParams(%s);\n", obj, mLayoutParamsObj));
			stringBuilder.append(String.format("%s.addView(%s);\n", mParent.getObjName(), obj));
		}

		if ("fragment".equals(mTagName)) {

			if (mId == null) {
				Log.e("fragment label must set android:id");
			}

			if (mAndroidName == null) {
				Log.e("fragment label must set android:name");
			}

			mImports.add("android.app.FragmentManager");
			mImports.add("android.app.FragmentTransaction");
			mImports.add("android.app.Activity");
			mImports.add("java.lang.reflect.Method");
			mImports.add(mAndroidName);

			stringBuilder.append(String.format("((Activity) ctx).getFragmentManager()" + "\n\t\t\t\t.beginTransaction()" + "\n\t\t\t\t.replace(%s, new %s())" + "\n\t\t\t\t.commit();\n", mId,
					mAndroidName.substring(mAndroidName.lastIndexOf(".") + 1)));

			String fm = "fm" + getRootView().mIndex;

			stringBuilder.append(String.format("FragmentManager %s = ((Activity)ctx).getFragmentManager();\n", fm));
			stringBuilder.append(String.format("Class clz = %s.getClass();\n", fm));
			stringBuilder.append("Method method;\n");
			stringBuilder.append("while (clz != null) {\n");
			stringBuilder.append("\ttry {\n");
			stringBuilder.append("\t\tmethod = clz.getDeclaredMethod(\"execPendingActions\");\n ");
			stringBuilder.append("\t\tif (method != null) {\n");
			stringBuilder.append("\t\t\tmethod.setAccessible(true);\n");
			stringBuilder.append(String.format("\t\t\tmethod.invoke(%s);\n", fm));
			stringBuilder.append("\t\t\tbreak;\n");
			stringBuilder.append("\t\t} else {\n");
			stringBuilder.append("\t\t\tclz = clz.getSuperclass();\n");
			stringBuilder.append("\t\t }\n");
			stringBuilder.append("\t} catch (Exception e) {\n");
			stringBuilder.append("\t\tclz = clz.getSuperclass();\n");
			stringBuilder.append("\t}\n");
			stringBuilder.append("}\n");

			stringBuilder.append("\n");

		}

		if (isDataBinding) {
			if (mParent == null) {
				setTag(stringBuilder, mDirName + "/" + mLayoutName + "_" + mDataBindingIndex++);
			} else {
				setTag(stringBuilder, "binding_" + getRootView().mDataBindingIndex++);
			}
		}

		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	private String getWidthStr() {
		if (mAttributes == null) {
			return "";
		}
		String widthStr = mAttributes.getValue("android:layout_width");
		if (widthStr != null) {
			return widthStr;
		}
		if (mStyleAttributes != null) {
			return mStyleAttributes.get("android:layout_width");
		}
		return null;
	}

	private String getHeightStr() {
		if (mAttributes == null) {
			return "";
		}
		String widthStr = mAttributes.getValue("android:layout_height");
		if (widthStr != null) {
			return widthStr;
		}
		if (mStyleAttributes != null) {
			return mStyleAttributes.get("android:layout_height");
		}
		return null;
	}

	@Override
	public boolean translate(StringBuilder stringBuilder, String key, String value) {
		switch (key) {
		case "android:textSize":
			return setTextSize(stringBuilder, value);
		case "android:textColor":
			return setTextColor(stringBuilder, value);
		case "android:text":
			return setText(stringBuilder, value);
		case "android:background":
			return setBackground(stringBuilder, value);
		case "android:textStyle":
			return setTypeface(stringBuilder, value);
		case "android:layout_margin":
			return setMargin(stringBuilder, value);
		case "android:layout_marginLeft":
			return setMarginLeft(stringBuilder, value);
		case "android:layout_marginStart":
			return setMarginLeft(stringBuilder, value);
		case "android:tag":
			return setTag(stringBuilder, value);
		case "android:layout_marginTop":
			return setMarginTop(stringBuilder, value);
		case "android:layout_marginRight":
			return setMarginRight(stringBuilder, value);
		case "android:layout_marginEnd":
			return setMarginRight(stringBuilder, value);
		case "android:layout_marginBottom":
			return setMarginBottom(stringBuilder, value);
		case "android:paddingStart":
			return setPaddingLeft(stringBuilder, value);
		case "android:paddingEnd":
			return setPaddingRight(stringBuilder, value);
		case "android:paddingLeft":
			return setPaddingLeft(stringBuilder, value);
		case "android:paddingRight":
			return setPaddingRight(stringBuilder, value);
		case "android:paddingTop":
			return setPaddingTop(stringBuilder, value);
		case "android:paddingBottom":
			return setPaddingBottom(stringBuilder, value);
		case "android:padding":
			return setPadding(stringBuilder, value);
		case "android:gravity":
			return setGravity(stringBuilder, value);
		case "android:orientation":
			return setOrientation(stringBuilder, value);
		case "android:id":
			return setId(stringBuilder, value);
		case "android:scaleType":
			return setScaleType(stringBuilder, value);
		case "android:src":
			return setImageResource(stringBuilder, value);
		case "android:visibility":
			return setVisibility(stringBuilder, value);
		case "android:clipToPadding":
			return setClipToPadding(stringBuilder, value);
		case "android:ellipsize":
			return setEllipsize(stringBuilder, value);
		case "android:lineSpacingExtra":
			return setLineSpacing(stringBuilder, value);
		case "android:maxLines":
			return setMaxLines(stringBuilder, value);
		case "android:maxHeight":
			return setMaxHeight(stringBuilder, value);
		case "android:maxWidth":
			return setMaxWidth(stringBuilder, value);
		case "android:minWidth":
			return setMinWidth(stringBuilder, value);
		case "android:minHeight":
			return setMinHeight(stringBuilder, value);
		case "android:layout_weight":
			return setWeight(stringBuilder, value);
		case "android:layout_gravity":
			return setLayoutGravity(stringBuilder, value);
		case "android:alpha":
			return setAlpha(stringBuilder, value);
		case "android:name":
			mAndroidName = value;
			return true;
		case "android:drawablePadding":
			return setDrawablePadding(stringBuilder, value);
		case "android:drawableLeft":
		case "android:drawableStart":
			stringBuilder.append(String.format("%s.setCompoundDrawablesWithIntrinsicBounds(%s,0,0,0)", getObjName(), value));
			stringBuilder.append("\n");
			return true;
		case "android:drawableRight":
		case "android:drawableEnd":
			stringBuilder.append(String.format("%s.setCompoundDrawablesWithIntrinsicBounds(0,%s,0,0)", getObjName(), value));
			stringBuilder.append("\n");
			return true;
		case "android:drawableTop":
			stringBuilder.append(String.format("%s.setCompoundDrawablesWithIntrinsicBounds(0,0,%s,0)", getObjName(), value));
			stringBuilder.append("\n");
			return true;
		case "android:drawableBottom":
			stringBuilder.append(String.format("%s.setCompoundDrawablesWithIntrinsicBounds(0,0,0,%s)", getObjName(), value));
			stringBuilder.append("\n");
			return true;
		default:
			return false;
		}
	}

	private boolean setMargin(StringBuilder stringBuilder, String value) {
		if (mLayoutParamsObj != null) {
			AutoAttr autoAttr = new MarginAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(mLayoutParamsObj));
		}
		return true;
	}

	private boolean setTypeface(StringBuilder stringBuilder, String value) {
		mImports.add("android.graphics.Typeface");
		stringBuilder.append(String.format("%s.setTypeface(%s);\n", getObjName(), getTextStyle(value)));
		return true;
	}

	@Override
	public void onAttributeEnd(StringBuilder stringBuilder) {

	}

	private boolean setAlpha(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setAlpha(%s);\n", getObjName(), getFloat(value)));
		return true;
	}

	private boolean setLayoutGravity(StringBuilder stringBuilder, String value) {
		if (mLayoutParamsObj != null) {
			stringBuilder.append(String.format("%s.gravity= %s ;\n", mLayoutParamsObj, getGravity(value)));
		}
		return true;
	}

	private boolean setWeight(StringBuilder stringBuilder, String value) {
		if (mLayoutParamsObj != null) {
			stringBuilder.append(String.format("%s.weight= %s ;\n", mLayoutParamsObj, value));
		}
		return true;
	}

	private boolean setScaleType(StringBuilder stringBuilder, String value) {
		mImports.add("android.widget.ImageView.ScaleType");
		stringBuilder.append(String.format("%s.setScaleType(%s);\n", getObjName(), getScaleType(value)));
		return true;
	}

	private boolean setMaxLines(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setMaxLines(%s);\n", getObjName(), value));
		return true;
	}

	private boolean setMaxHeight(StringBuilder stringBuilder, String value) {
		AutoAttr autoAttr = new MaxHeightAttrImp(value, autoBaseWidth, autoBaseHeight);
		stringBuilder.append(autoAttr.build(getObjName()));
		return true;
	}

	private boolean setMaxWidth(StringBuilder stringBuilder, String value) {
		AutoAttr autoAttr = new MaxWidthAttrImp(value, autoBaseWidth, autoBaseHeight);
		stringBuilder.append(autoAttr.build(getObjName()));
		return true;
	}

	private boolean setMinWidth(StringBuilder stringBuilder, String value) {
		AutoAttr autoAttr = new MinWidthAttrImp(value, autoBaseWidth, autoBaseHeight);
		stringBuilder.append(autoAttr.build(getObjName()));
		return true;
	}

	private boolean setMinHeight(StringBuilder stringBuilder, String value) {
		AutoAttr autoAttr = new MinHeightAttrImp(value, autoBaseWidth, autoBaseHeight);
		stringBuilder.append(autoAttr.build(getObjName()));
		return true;
	}

	private boolean setLineSpacing(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setLineSpacing(%s,1);\n", getObjName(), getDimen(value)));
		return true;
	}

	private boolean setEllipsize(StringBuilder stringBuilder, String value) {
		mImports.add("android.text.TextUtils");
		stringBuilder.append(String.format("%s.setEllipsize(%s);\n", getObjName(), getEllipsize(value)));
		return true;
	}

	private boolean setClipToPadding(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setClipToPadding(%s);\n", getObjName(), value));
		return true;
	}

	private boolean setVisibility(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setVisibility(%s);\n", getObjName(), getVisibility(value)));
		return true;
	}

	private boolean setTextColor(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setTextColor(%s);\n", getObjName(), getColor(value)));
		return true;
	}

	private boolean setText(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setText(%s);\n", getObjName(), getString(value)));
		return true;
	}

	private boolean setTag(StringBuilder stringBuilder, String value) {
		if (value.startsWith("@id")) {
			stringBuilder.append(String.format("%s.setTag(R.id.%s);\n", getObjName(), value.substring(value.indexOf("/") + 1)));
		} else if (value.startsWith("@android:id")) {
			stringBuilder.append(String.format("%s.setTag(android.R.id.%s);\n", getObjName(), value.substring(value.indexOf("/") + 1)));
		} else {
			stringBuilder.append(String.format("%s.setTag(%s);\n", getObjName(), getString(value)));
		}
		return true;
	}

	private boolean setMarginLeft(StringBuilder stringBuilder, String value) {
		if (mLayoutParamsObj != null) {
			AutoAttr autoAttr = new MarginLeftAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(mLayoutParamsObj));
		}
		return true;
	}

	private boolean setMarginTop(StringBuilder stringBuilder, String value) {
		if (mLayoutParamsObj != null) {
			AutoAttr autoAttr = new MarginTopAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(mLayoutParamsObj));
		}
		return true;
	}

	private boolean setMarginRight(StringBuilder stringBuilder, String value) {
		if (mLayoutParamsObj != null) {
			AutoAttr autoAttr = new MarginRightAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(mLayoutParamsObj));
		}
		return true;
	}

	private boolean setMarginBottom(StringBuilder stringBuilder, String value) {
		if (mLayoutParamsObj != null) {
			AutoAttr autoAttr = new MarginBottomAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(mLayoutParamsObj));
		}
		return true;
	}

	private boolean setPadding(StringBuilder stringBuilder, String value) {
		if (getObjName() != null) {
			AutoAttr autoAttr = new PaddingAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(getObjName()));
		}
		return true;
	}

	private boolean setPaddingLeft(StringBuilder stringBuilder, String value) {
		if (getObjName() != null) {
			AutoAttr autoAttr = new PaddingLeftAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(getObjName()));
		}
		return true;
	}

	private boolean setPaddingTop(StringBuilder stringBuilder, String value) {
		if (getObjName() != null) {
			AutoAttr autoAttr = new PaddingTopAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(getObjName()));
		}
		return true;
	}

	private boolean setPaddingRight(StringBuilder stringBuilder, String value) {
		if (getObjName() != null) {
			AutoAttr autoAttr = new PaddingRightAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(getObjName()));
		}
		return true;
	}

	private boolean setPaddingBottom(StringBuilder stringBuilder, String value) {
		if (getObjName() != null) {
			AutoAttr autoAttr = new PaddingBottomAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(getObjName()));
		}
		return true;
	}

	private boolean setDrawablePadding(StringBuilder stringBuilder, String value) {
		if (getObjName() != null) {
			AutoAttr autoAttr = new DrawablePaddingAttrImp(value, autoBaseWidth, autoBaseHeight);
			stringBuilder.append(autoAttr.build(getObjName()));
		}
		return true;
	}

	private boolean setTextSize(StringBuilder stringBuilder, String value) {
		mImports.add("android.util.TypedValue");
		AutoAttr autoAttr = new TextSizeAttrImp(value, autoBaseWidth, autoBaseHeight);
		stringBuilder.append(autoAttr.build(getObjName()));
		return true;
	}

	private boolean setImageResource(StringBuilder stringBuilder, String value) {
		if (value.startsWith("#") || value.startsWith("@color")) {
			stringBuilder.append(String.format("%s.setBackgroundColor(%s);\n", getObjName(), getColor(value)));
		} else {
			stringBuilder.append(String.format("%s.setImageResource(%s);\n", getObjName(), getDrawable(value)));
		}
		return true;
	}

	private boolean setBackground(StringBuilder stringBuilder, String value) {
		if (value.startsWith("#") || value.startsWith("@color")) {
			stringBuilder.append(String.format("%s.setBackgroundColor(%s);\n", getObjName(), getColor(value)));
		} else if ("null".equals(value)) {
			stringBuilder.append(String.format("%s.setBackgroundDrawable(%s);\n", getObjName(), "null"));
		} else if (value.startsWith("?")) {
			String objName = getObjName();
			stringBuilder.append("try {\n");
			stringBuilder.append(String.format("    %s.setBackgroundColor(%s);\n", objName, getUnknown(value)));
			stringBuilder.append("} catch (Exception e) {\n");
			stringBuilder.append("    //这里再来异常就是不支持的资源类型\n");
			stringBuilder.append(String.format("    %s.setBackgroundResource(%s);\n", objName, getUnknown(value)));
			stringBuilder.append("}\n");
		} else {
			stringBuilder.append(String.format("%s.setBackgroundResource(%s);\n", getObjName(), getDrawable(value)));
		}
		return true;
	}

	private boolean setOrientation(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setOrientation(%s);\n", getObjName(), getOrientation(value)));
		return true;
	}

	private boolean setGravity(StringBuilder stringBuilder, String value) {
		stringBuilder.append(String.format("%s.setGravity(%s);\n", getObjName(), getGravity(value)));
		return true;
	}

	private boolean setId(StringBuilder stringBuilder, String value) {
		if (value.startsWith("@android:id")) {
			mId = "android.R.id." + value.substring(value.indexOf("/") + 1);
			stringBuilder.append(String.format("%s.setId(%s);\n", getObjName(), mId));
		} else {
			mId = "R.id." + value.substring(value.indexOf("/") + 1);
			stringBuilder.append(String.format("%s.setId(%s);\n", getObjName(), mId));
		}
		return true;
	}

	public static String getWH(String value) {
		if (value == null) {
			return "0";
		}
		switch (value) {
		case "fill_parent":
			return "ViewGroup.LayoutParams.FILL_PARENT";
		case "match_parent":
			return "ViewGroup.LayoutParams.MATCH_PARENT";
		case "wrap":
		case "wrap_content":
			return "ViewGroup.LayoutParams.WRAP_CONTENT";
		default:
			return getDimen(value);
		}
	}

	public static String getDimen(String value) {
		if (value.startsWith("@")) {
			return String.format("(int)res.getDimension(R.dimen.%s)", value.substring(value.indexOf("/") + 1));
		}
		String unit;
		String dim;

		if (value.endsWith("dp") || value.endsWith("dip")) {
			unit = "TypedValue.COMPLEX_UNIT_DIP";
			dim = value.substring(0, value.indexOf("d"));
		} else if (value.endsWith("sp")) {
			unit = "TypedValue.COMPLEX_UNIT_SP";
			dim = value.substring(0, value.indexOf("s"));
		} else {
			unit = "TypedValue.COMPLEX_UNIT_PX";
			dim = value.substring(0, value.indexOf("p"));
		}

		return String.format("(int)(TypedValue.applyDimension(%s,%s,res.getDisplayMetrics()))", unit, dim);
	}

	public static String getColor(String value) {
		if ("#000".equals(value)) {
			return "Color.parseColor(\"#000000\")";
		} else if ("#FFF".equals(value)) {
			return "Color.parseColor(\"#FFFFFF\")";
		} else if (value.startsWith("#")) {
			return "Color.parseColor(\"" + value + "\")";
		} else if (value.startsWith("@android:color")) {
			return "res.getColor(android.R.color." + value.substring(value.indexOf("/") + 1) + ")";
		} else if (value.startsWith("@")) {
			return "res.getColor(R.color." + value.substring(value.indexOf("/") + 1) + ")";
		} else {
			return "0";
		}
	}

	public static String getId(String value) {
		if (value.contains("id/")) {
			return "R.id." + value.substring(value.lastIndexOf("/") + 1);
		}
		return "0";
	}

	public static String getFloat(String value) {
		return value + "f";
	}

	public static String getBoolean(String value) {
		if (value.startsWith("@")) {
			return String.format("res.getBoolean(R.bool.%s)") + value.substring(value.indexOf("/") + 1);
		}
		return value;
	}

	public static String getString(String value) {
		if (value.startsWith("@")) {
			return "R.string." + value.substring(value.indexOf("/") + 1);
		}
		return String.format("\"%s\"", value);
	}

	public static String getDrawable(String value) {
		if (value.startsWith("@drawable")) {
			return "R.drawable." + value.substring(value.indexOf("/") + 1);
		} else if (value.startsWith("@mipmap")) {
			return "R.mipmap." + value.substring(value.indexOf("/") + 1);
		}
		return value;
	}

	public static String getUnknown(String value) {
		if (value.startsWith("?android:attr")) {
			return "android.R.attr." + value.substring(value.indexOf("/") + 1);
		} else {
			return "R.attr." + value.substring(value.indexOf("/") + 1);
		}
	}

	private String getGravity(String value) {
		String[] ss = value.split("\\|");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ss.length; i++) {
			sb.append(getGravitySingle(ss[i]));
			if (i < ss.length - 1) {
				sb.append("|");
			}
		}
		return sb.toString();
	}

	private String getGravitySingle(String value) {
		mImports.add("android.view.Gravity");
		switch (value) {
		case "end":
			return "Gravity.END";
		case "start":
			return "Gravity.START";
		case "left":
			return "Gravity.LEFT";
		case "top":
			return "Gravity.TOP";
		case "right":
			return "Gravity.RIGHT";
		case "bottom":
			return "Gravity.BOTTOM";
		case "center":
			return "Gravity.CENTER";
		case "center_vertical":
			return "Gravity.CENTER_VERTICAL";
		case "center_horizontal":
			return "Gravity.CENTER_HORIZONTAL";
		case "fill":
			return "Gravity.FILL";
		case "fill_horizontal":
			return "Gravity.FILL_HORIZONTAL";
		case "fill_vertical":
			return "Gravity.FILL_VERTICAL";
		case "clip_vertical":
			return "Gravity.CLIP_VERTICAL";
		case "clip_horizontal":
			return "Gravity.CLIP_HORIZONTAL";
		default:
			return "Gravity.LEFT";
		}
	}

	private String getOrientation(String value) {
		mImports.add("android.widget.LinearLayout");
		switch (value) {
		case "vertical":
			return "LinearLayout.VERTICAL";
		default:
			return "LinearLayout.HORIZONTAL";
		}
	}

	private String getIncludeLayout() {
		String layout = mAttributes.getValue("layout");
		return layout.substring(layout.lastIndexOf("/") + 1);
	}

	private View getRootView() {
		View root = this;
		while (root.mParent != null) {
			root = root.mParent;
		}
		return root;
	}

	private Map<String, String> getStyleAttribute() {
		if (mAttributes == null || mStyleAttributes != null) {
			return mStyleAttributes;
		}

		String styleName = mAttributes.getValue("style");

		if (styleName != null && styleName.startsWith("@")) {
			LayoutManager layoutMgr = LayoutManager.instance();
			Style style = layoutMgr.getStyle(styleName.substring(styleName.lastIndexOf("/") + 1));
			if (style != null) {
				mStyleAttributes = style.attribute;
				while (style.parent != null) {
					style = layoutMgr.getStyle(style.parent);
					if (style != null) {
						for (String key : style.attribute.keySet()) {
							if (!mStyleAttributes.containsKey(key)) {
								mStyleAttributes.put(key, style.attribute.get(key));
							}
						}
					} else {
						break;
					}
				}
			}
		}
		return mStyleAttributes;
	}

	private void getAutoLayoutAttribute() {
		if (mAttributes == null || (!"".equals(autoBaseWidth) && !"".equals(autoBaseHeight))) {
			return;
		}
		String key;
		String value;
		int n = mAttributes.getLength();
		for (int i = 0; i < n; i++) {
			key = mAttributes.getQName(i);
			value = mAttributes.getValue(i);
			if (key.contains("layout_auto_base_width")) {
				autoBaseWidth = value;
			} else if (key.contains("layout_auto_base_height")) {
				autoBaseHeight = value;
			} else if (key.contains("layout_auto_parent_view")) {
				mLayoutParams = value + ".LayoutParams";
				value = "android.widget." + value;
				mImports.add(value);
			}
		}
	}

	private String getVisibility(String value) {
		switch (value) {
		case "gone":
			return "View.GONE";
		case "invisible":
			return "View.INVISIBLE";
		default:
			return "View.VISIBLE";
		}
	}

	private String getEllipsize(String value) {
		switch (value) {
		case "start":
			return "TextUtils.TruncateAt.START";
		case "middle":
			return "TextUtils.TruncateAt.MIDDLE";
		case "marquee":
			return "TextUtils.TruncateAt.MARQUEE";
		default:
			return "TextUtils.TruncateAt.END";
		}
	}

	private String getTextStyle(String value) {
		switch (value) {
		case "bold":
			return "Typeface.DEFAULT_BOLD";
		case "italic":
			return "Typeface.ITALIC";
		default:
			return "Typeface.DEFAULT";
		}
	}

	private String getScaleType(String value) {
		switch (value) {
		case "matrix":
			return "ScaleType.MATRIX";
		case "fitStart":
			return "ScaleType.FIT_START";
		case "fitCenter":
			return "ScaleType.FIT_CENTER";
		case "fitEnd":
			return "ScaleType.FIT_END";
		case "center":
			return "ScaleType.CENTER";
		case "centerCrop":
			return "ScaleType.CENTER_CROP";
		case "centerInside":
			return "ScaleType.CENTER_INSIDE";
		default:
			return "ScaleType.FIT_XY";
		}
	}

	private ArrayList<ITranslator> createTranslator() {
		ArrayList<ITranslator> list = new ArrayList<>();
		list.add(this);
		list.add(new ConstraintLayout(mImports, mLayoutParamsObj));
		list.add(new RelativeLayout(mImports, mLayoutParamsObj));
		list.add(new CustomAttr(mImports, mLayoutParamsObj, mObjName));
		return list;
	}
}

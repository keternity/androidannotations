package org.androidannotations.internal.x2c.xml;

import org.androidannotations.api.x2c.IViewCreator;
import org.androidannotations.internal.x2c.X2CManager;
import org.androidannotations.internal.x2c.javapoet.ClassName;
import org.androidannotations.internal.x2c.javapoet.JavaFile;
import org.androidannotations.internal.x2c.javapoet.MethodSpec;
import org.androidannotations.internal.x2c.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.TreeSet;

/**
 * @author chengwei 2018/8/8
 */
public class LayoutWriter {
    private Filer mFiler;
    private String mName;
    private String mMethodSpec;
    private String mPkgName;
    private String mLayoutCategory;
    private String mLayoutName;
    private TreeSet<String> mImports;
    private boolean isMerge = false;

    public LayoutWriter(String methodSpec, Filer filer, String javaName
            , String pkgName
            , String layoutSort
            , String layoutName
            , TreeSet<String> imports) {
        this.mMethodSpec = methodSpec;
        this.mFiler = filer;
        this.mName = javaName;
        this.mPkgName = pkgName;
        this.mLayoutCategory = layoutSort;
        this.mLayoutName = layoutName;
        this.mImports = imports;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }

    public String write() {
        MethodSpec methodSpec = MethodSpec.methodBuilder("createView")
                .addParameter(ClassName.get("android.content", "Context"), "ctx")
                .addStatement(isMerge ? "return null" : mMethodSpec)
                .returns(ClassName.get("android.view", "View"))
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .build();
        MethodSpec methodSpecEx = MethodSpec.methodBuilder("createView")
                .addParameter(ClassName.get("android.view", "ViewGroup"), "viewGroup")
                .addStatement(isMerge ? mMethodSpec : "return viewGroup")
                .returns(ClassName.get("android.view", "View"))
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(mName)
                .addMethod(methodSpec)
                .addMethod(methodSpecEx)
                .addSuperinterface(IViewCreator.class)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(String.format("WARN!!! dont edit this file\ntranslate from {@link  %s.R.layout.%s}"
                        , mPkgName, mLayoutName))
                .build();

        String pkgName = X2CManager.savePackageName + ".x2c.layouts";
        if (mLayoutCategory != null && mLayoutCategory.length() > 0) {
            pkgName += ("." + mLayoutCategory);
        }
        JavaFile javaFile = JavaFile.builder(pkgName, typeSpec)
                .addImports(mImports)
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pkgName + "." + mName;
    }
}

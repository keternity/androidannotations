package org.androidannotations.internal.x2c.xml;


import com.eternity.android.annotation.extra.plugin.x2c.Util;
import com.eternity.android.annotation.extra.plugin.x2c.X2CManager;
import com.eternity.android.annotation.extra.plugin.x2c.view.View;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.Stack;

import javax.annotation.processing.Filer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author chengwei 2018/8/7
 */
public class LayoutReader {
    private Filer mFiler;
    private SAXParser mParser;
    private String mName;
    private String mFullName;
    private String mLayoutName;
    private String mPackageName;
    private File mFile;

    public LayoutReader(File file, String name, Filer filer, String packageName, int groupId) {
        mFile = file;
        mFiler = filer;
        mPackageName = packageName;
        mLayoutName = name;
        mName = X2CManager.getSaveLayoutName(groupId, name);
    }

    public String parse() {
        try {
            mParser = SAXParserFactory.newInstance().newSAXParser();
            mParser.parse(mFile, new XmlHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFullName;
    }

    private class XmlHandler extends DefaultHandler {
        private Stack<View> mStack;
        private View mRootView;
        private boolean isDataBinding;

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            mStack = new Stack<>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            View view = createView(qName, attributes);
            if (view != null) {
                if (mStack.size() > 0) {
                    view.setParent(mStack.get(mStack.size() - 1));
                } else {
                    view.setParent(null);
                }
                mStack.push(view);
            }
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if (mStack.size() > 0) {
                View view = mStack.pop();
                if (mStack.size() == 0) {
                    mRootView = view;
                    StringBuilder stringBuilder = new StringBuilder();
                    mRootView.translate(stringBuilder);
                    stringBuilder.append("return ").append(mRootView.getObjName());
                    LayoutWriter writer = new LayoutWriter(stringBuilder.toString(), mFiler, mName, mPackageName
                            , Util.getLayoutCategory(mFile), mLayoutName, mRootView.getImports());
                    writer.setMerge(mRootView.isMerge());
                    mFullName = writer.write();
                }
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        private View createView(String name, Attributes attributes) {
            if (name.equals("layout") || name.equals("data") || name.equals("variable") || name.equals("import")) {
                isDataBinding = true;
                return null;
            }
            View view = new View(mPackageName, name, attributes);
            if (mStack.size() == 0) {
                view.setDirName(Util.getDirName(mFile));
                view.setIsDataBinding(isDataBinding);
            }
            view.setLayoutName(mLayoutName);
            return view;
        }
    }
}

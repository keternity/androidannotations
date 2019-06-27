package org.androidannotations.api.x2c;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author Eternity
 * Date: 2019/6/24 0024 14:54
 * ClassName IViewCreator
 * Description 自动创建View
 */
public interface IViewCreator {
    /**
     * Author Eternity
     * Description 通用情况下使用该方法来自动生成View
     * Date: 2019/6/24 0024 14:55
     * MethodName createView
     * Param [context]
     * return android.view.View
     */
    View createView(Context context);

    /**
     * Author Eternity
     * Description 当XML开头为marge时使用该方法来生成View
     * Date: 2019/6/24 0024 14:56
     * MethodName bindView
     * Param [viewGroup]
     * return android.view.View
     */
    View createView(ViewGroup viewGroup);
}

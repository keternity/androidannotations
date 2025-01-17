/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.androidannotations.api.KotlinOpen;

/**
 * <p>
 * Should be used on {@link android.view.View} classes to enable usage of
 * AndroidAnnotations.
 * </p>
 * <p>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * Any view related code should happen in an {@link AfterViews} annotated
 * method.
 * </p>
 * <p>
 * If the class is abstract, the enhanced view will not be generated. Otherwise,
 * it will be generated as a final class. You can use AndroidAnnotations to
 * create Abstract classes that handle common code.
 * </p>
 * <p>
 * The annotation value should be one of R.layout.* fields. If not set, no
 * content view will be set, and you should inflate the layout yourself by
 * calling View.inflate() method
 * </p>
 *
 * <blockquote>
 * <p>
 * Example :
 *
 * <pre>
 * &#064;EViewGroup(R.layout.component)
 * public class CustomFrameLayout extends FrameLayout {
 *
 * 	&#064;ViewById
 * 	TextView titleView;
 *
 * 	&#064;AfterViews
 * 	void initViews() {
 * 		titleView.setText(&quot;test&quot;);
 * 	}
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see AfterInject
 * @see AfterViews
 * @see ViewById
 * @see android.view.View
 * @see <a href=
 *      "http://developer.android.com/guide/topics/ui/custom-components.html"
 *      >How to build a custom component.</a>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@KotlinOpen
public @interface EViewGroup {

	/**
	 * The R.layout.* field which refer to the layout.
	 *
	 * @return the id of the layout
	 */
	int value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name as a string which refer to the layout.
	 *
	 * @return the resource name of the layout
	 */
	String resName() default "";

	/**
	 * Use X2C.
	 *
	 * @return use X2C or not
	 */
	boolean useX2C() default false;
}

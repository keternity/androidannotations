package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Use on any native, {@link android.os.Parcelable Parcelable} or
 * {@link java.io.Serializable Serializable} field in an {@link EViewGroup}
 * annotated class to bind it with Android's arguments. If
 * <a href="http://parceler.org">Parceler</a> is on the classpath, extras
 * annotated with &#064;Parcel, or collections supported by Parceler will be
 * automatically marshaled using a {@link android.os.Parcelable Parcelable}
 * through the Parcels utility class.
 * </p>
 * <p>
 * The annotation value is the key used for argument. If not set, the field or
 * method name will be used as the key.
 * </p>
 * <p>
 * When {@link ViewArg} is used, the intent builder will hold dedicated methods
 * for each annotated fields.
 * </p>
 * <p>
 * Your code related to injected extra should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EViewGroup
 * public class MyView extends LinearLayout {
 *
 * 	&#064;ViewArg
 * 	String myMessage;
 *
 * 	&#064;ViewArg
 * 	void singleInjection(String myMessage) {
 * 		// do stuff
 * 	}
 *
 * 	void multiInjection(&#064;ViewArg String myMessage, &#064;ViewArg String myMessage2) {
 * 		// do stuff
 * 	}
 * }
 *
 * </pre>
 *
 * </blockquote>
 *
 * @see EViewGroup
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface ViewArg {

	/**
	 * The key of the injected View argument.
	 *
	 * @return the key of the argument
	 */
	String value() default "";
}

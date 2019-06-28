package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see EFragment
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnFragmentResult {
	/**
	 * The <b>requestCode</b> associated with the given result.
	 *
	 * @return the requestCode
	 */
	int value();

	/**
	 * @see OnFragmentResult
	 */
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.PARAMETER)
	@interface Extra {
		/**
		 * They key of the result data.
		 *
		 * @return the key
		 */
		String value() default "";
	}
}

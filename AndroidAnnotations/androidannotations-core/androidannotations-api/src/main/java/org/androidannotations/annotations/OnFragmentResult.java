package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnFragmentResult {

	int value();

	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.PARAMETER)
	@interface Extra {
		String value() default "";
	}
}

package de.greyshine.vuespringexample.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD}) 
public @interface Access {
	
	String RIGHT_ADMIN = "admin";

	/**
	 * When no value is declared a User must be in scope (logged in or provide proper credentials) to execute.
	 * 
	 * @return if specified a list of rights of which the user must have one.
	 */
	String[] value() default {};
}

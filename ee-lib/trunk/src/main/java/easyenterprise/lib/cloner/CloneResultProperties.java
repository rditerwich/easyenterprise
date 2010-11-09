package easyenterprise.lib.cloner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Parameter to the {@link CloneResultInterceptor} interceptor.
 *
 * @author Ruud Diterwich
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CloneResultProperties {
	String value();
}

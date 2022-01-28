package info.novatec.bpm.demo_executionlisteners.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to skip method calls if the first argument of the method is
 * a {@link DelegateExecution} which was canceled. Intended for use on methods
 * called by ExecutionListeners so they only get called when the execution
 * actually ends regularly.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface OnlyNonCanceledExecutions {

}

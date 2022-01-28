package info.novatec.bpm.demo_executionlisteners.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OnlyNonCanceledExecutionsImplementation {
    
    @Around("classesAnnotatedWithOnlyNonCanceledExecutions()")
    public Object aroundAnnotatedClass(ProceedingJoinPoint joinPoint) throws Throwable {
        if (executionWasCanceled(joinPoint)) {
            return null;
        }
        return joinPoint.proceed();
    }

    @Pointcut("@within(info.novatec.bpm.demo_executionlisteners.annotation.OnlyNonCanceledExecutions)")
    private void classesAnnotatedWithOnlyNonCanceledExecutions() {
        // defines pointcut for classes annotated with OnlyNonCanceledExecutions
    }
    
    private boolean executionWasCanceled(ProceedingJoinPoint joinPoint) {
        Object[] methodArguments = joinPoint.getArgs();
        if (methodArguments != null && methodArguments.length > 0) {
            if (methodArguments[0] instanceof DelegateExecution) {
                return ((DelegateExecution) methodArguments[0]).isCanceled();
            }
        }
        return false;
    }
}

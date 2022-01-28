package info.novatec.bpm.demo_executionlisteners.listeners;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import info.novatec.bpm.demo_executionlisteners.ProcessConstants;
import info.novatec.bpm.demo_executionlisteners.annotation.OnlyNonCanceledExecutions;

@Component("listenToExecution")
@ConditionalOnProperty(name = "listenerWithAnnotation", havingValue = "true")
@OnlyNonCanceledExecutions
public class ListenToExecutionWithAnnotation implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        if (!execution.hasVariable(ProcessConstants.VARIABLE_REQUIRED_FOR_COMPLETING_SEND_TASK)) {
            throw new RuntimeException("Must not complete Send Task without required variable!");
        }

    }

}

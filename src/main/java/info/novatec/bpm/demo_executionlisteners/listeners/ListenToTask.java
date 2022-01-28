package info.novatec.bpm.demo_executionlisteners.listeners;

import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.VARIABLE_REQUIRED_FOR_COMPLETING_USER_TASK;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

@Component
public class ListenToTask implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        if (!delegateTask.getExecution().hasVariable(VARIABLE_REQUIRED_FOR_COMPLETING_USER_TASK)) {
            throw new RuntimeException("Must not complete User Task without required variable!");
        }

    }

}

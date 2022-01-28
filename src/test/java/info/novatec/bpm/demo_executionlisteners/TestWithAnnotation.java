package info.novatec.bpm.demo_executionlisteners;

import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.CANCELLATION_MESSAGE_NAME;
import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.PROCESS_DEFINITION_KEY_WITH_CANCELLATION;
import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.SOME_MESSAGE_NAME;
import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.VARIABLE_REQUIRED_FOR_COMPLETING_SEND_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties="listenerWithAnnotation=true")
public class TestWithAnnotation {
    
    private static final String PROCESS_MODEL_PATH = "ExecutionListeners - with cancellation.bpmn";
    
    @Autowired
    RuntimeService runtimeService;
    
    @Autowired
    RepositoryService repositoryService;
    
    @BeforeEach
    public void setUp() {
        repositoryService
            .createDeployment()
            .addClasspathResource(PROCESS_MODEL_PATH)
            .deploy();
    }
    
    @Test
    void processInstance_waiting_at_receiveTask_should_be_cancellable_without_variable() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("ReceiveTaskWaitForMessage").execute();
        assertThat(processInstance).isWaitingAtExactly("ReceiveTaskWaitForMessage");
        
        runtimeService.createMessageCorrelation(CANCELLATION_MESSAGE_NAME).correlate();
        assertThat(processInstance).isEnded();
        
        cleanUp();
    }
    
    @Test
    void processInstance_deployment_should_be_deletable_when_waiting_at_receiveTask() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("ReceiveTaskWaitForMessage").execute();
        assertThat(processInstance).isWaitingAtExactly("ReceiveTaskWaitForMessage");
        
        cleanUp();
        assertThat(repositoryService.createDeploymentQuery().list().isEmpty());
    }
    
    @Test
    void processInstance_should_be_deletable_when_waiting_at_receiveTask() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("ReceiveTaskWaitForMessage").execute();
        assertThat(processInstance).isWaitingAtExactly("ReceiveTaskWaitForMessage");
        
        runtimeService.deleteProcessInstance(processInstance.getId(), "someReason");
        assertThat(processInstance).isEnded();
        
        cleanUp();
    }
    
    
    @Test
    void receiveTask_should_be_completeable_with_variable(){
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("ReceiveTaskWaitForMessage").execute();
        assertThat(processInstance).isWaitingAtExactly("ReceiveTaskWaitForMessage");
        
        runtimeService.createMessageCorrelation(SOME_MESSAGE_NAME)
            .setVariable(VARIABLE_REQUIRED_FOR_COMPLETING_SEND_TASK, "someValue")
            .correlate();
        assertThat(processInstance).isEnded();
        
        cleanUp();
    }
    
    @Test
    void receiveTask_should_not_be_completeable_without_variable(){
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("ReceiveTaskWaitForMessage").execute();
        assertThat(processInstance).isWaitingAtExactly("ReceiveTaskWaitForMessage");
        
        assertThrows(RuntimeException.class, () -> runtimeService.createMessageCorrelation(SOME_MESSAGE_NAME).correlate());        
        assertThat(processInstance).isWaitingAtExactly("ReceiveTaskWaitForMessage");
        
        cleanUp();
    }
    
    void cleanUp() {        
        repositoryService.createDeploymentQuery().list().forEach(
            deployment -> repositoryService.deleteDeployment(deployment.getId(), true));   
    }
}

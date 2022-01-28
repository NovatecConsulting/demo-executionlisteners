package info.novatec.bpm.demo_executionlisteners;

import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.CANCELLATION_MESSAGE_NAME;
import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.PROCESS_DEFINITION_KEY_WITH_CANCELLATION;
import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.SOME_MESSAGE_NAME;
import static info.novatec.bpm.demo_executionlisteners.ProcessConstants.VARIABLE_REQUIRED_FOR_COMPLETING_SEND_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties="listenerWithAnnotation=false")
@TestMethodOrder(OrderAnnotation.class)
public class TestWithoutAnnotation {
    
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
    @Order(1)
    void userTask_should_not_be_completeable_without_variable() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("UserTaksDoSomething").execute();
        assertThat(processInstance).isWaitingAtExactly("UserTaksDoSomething");
        
        assertThrows(ProcessEngineException.class, () -> complete(task()));
        assertThat(processInstance).isWaitingAtExactly("UserTaksDoSomething");
        
        cleanUp();
    }
    
    @Test
    @Order(2)
    void processInstance_waiting_at_userTask_should_be_cancellable_without_variable() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("UserTaksDoSomething").execute();
        assertThat(processInstance).isWaitingAtExactly("UserTaksDoSomething");
        
        runtimeService.createMessageCorrelation(CANCELLATION_MESSAGE_NAME).correlate();
        assertThat(processInstance).isEnded();
        
        cleanUp();
    }
    
    @Test
    @Order(3)
    void processInstance_deployment_should_be_deletable_when_waiting_at_usertask() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("UserTaksDoSomething").execute();
        assertThat(processInstance).isWaitingAtExactly("UserTaksDoSomething");
        
        cleanUp();
        assertThat(repositoryService.createDeploymentQuery().list().isEmpty());
    }
    
    @Test
    @Order(4)
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
    @Order(5)
    void process_instance_deployment_should_not_be_deletable_when_waiting_at_receiveTask() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(PROCESS_DEFINITION_KEY_WITH_CANCELLATION)
            .startBeforeActivity("ReceiveTaskWaitForMessage").execute();
        assertThat(processInstance).isWaitingAtExactly("ReceiveTaskWaitForMessage");
        
        assertThrows(RuntimeException.class, () -> cleanUp());
    }
    
    void cleanUp() {        
        repositoryService.createDeploymentQuery().list().forEach(
            deployment -> repositoryService.deleteDeployment(deployment.getId(), true));   
    }
}

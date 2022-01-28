package info.novatec.bpm.demo_executionlisteners;


public interface ProcessConstants {
    
    public static final String VARIABLE_REQUIRED_FOR_COMPLETING_USER_TASK = "userTaskVariable";
    public static final String VARIABLE_REQUIRED_FOR_COMPLETING_SEND_TASK = "sendTaskVariable";
    
    public static final String CANCELLATION_MESSAGE_NAME = "Cancellation";
    public static final String SOME_MESSAGE_NAME = "SomeMessage";
    
    public static final String PROCESS_DEFINITION_KEY_WITHOUT_CANCELLATION = "listeners";
    public static final String PROCESS_DEFINITION_KEY_WITH_CANCELLATION = "listenersWithCancellation";
}

package Workout.Rest;

public class RestResponse {
    private Object message;
    private Object data;

    public Object getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public enum Message {
        CREATED,
        RETRIEVED,
        INVALID_OBJECT,
        DATE_EXPIRE_INVALID,
        CONTRACT_PARTIAL_COE,
        UNCAUGHT_EXCEPTION,
        DATE_PASSED,
        CONTRACT_ACCOUNT_NOT_FOUND,
        NOT_FOUND,
        INVALID_DATE
    }
}

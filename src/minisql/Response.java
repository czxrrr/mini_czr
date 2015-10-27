package minisql;

/**
 * Created by HUBIN on 2015/10/24.
 */
public class Response {
    private boolean success;
    private String info;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Response(boolean success,String info){
        this.success = success;
        this.info = info;
    }

    public Response(boolean success){
        this.success = success;
        this.info = "";
    }
}

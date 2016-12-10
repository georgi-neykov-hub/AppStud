package appstud.neykov.com.appstudassigment.networking;

/**
 * Created by Georgi on 12/10/2016.
 */

public class HttpException extends Exception{

    private int errorCode;
    private String message;

    public HttpException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}

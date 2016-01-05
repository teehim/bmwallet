package customer.bmwallet.com.bangmodwallet;

public interface Config {

    // used to share GCM regId with application server - using php app server
    static final String APP_SERVER_URL = "https://secure.bm-wallet.com/GCM_PUSH/regis_regid.php";

    // GCM server using java
    // static final String APP_SERVER_URL =
    // "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "272069356922";
    static final String MESSAGE_KEY = "message";

}
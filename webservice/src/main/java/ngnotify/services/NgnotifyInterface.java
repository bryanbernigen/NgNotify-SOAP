package ngnotify.services;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface NgnotifyInterface {
    @WebMethod String newSubscription(@WebParam(name = "Auth",header = true) String auth,String ip, int creator_id, int subscriber_id);
    @WebMethod String[] getSubscriptionList(@WebParam(name = "Auth",header = true) String auth,String ip, String status);
    @WebMethod String[] getSingleUserSubscriptionList(@WebParam(name = "Auth",header = true) String auth,String ip, int subscriber_id);
    @WebMethod String acceptSubscription(@WebParam(name = "Auth",header = true) String auth,String ip, int creator_id, int subscriber_id);
    @WebMethod String rejectSubscription(@WebParam(name = "Auth",header = true) String auth,String ip, int creator_id, int subscriber_id);
    @WebMethod String checkStatus(@WebParam(name = "Auth",header = true) String auth,String ip, int creator_id, int subscriber_id);
}
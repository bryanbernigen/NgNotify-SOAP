package ngnotify.services;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface NgnotifyInterface {
    @WebMethod String newSubscription(String ip, int creator_id, int subscriber_id);
    @WebMethod String[] getSubscriptionList(String ip, String status);
    @WebMethod String acceptSubscription(String ip, int creator_id, int subscriber_id);
    @WebMethod String rejectSubscription(String ip, int creator_id, int subscriber_id);
}
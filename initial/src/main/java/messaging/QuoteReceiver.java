package messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

/**
 * Created by revlin on 2/25/17.
 */
@Service
public class QuoteReceiver implements Consumer<Event<HashMap<String, Object>>> {

    @Autowired
    CountDownLatch latch;

    @Autowired
    RestTemplate template;

    @Bean
    public RestTemplate getTemplate () {
        return new RestTemplate();
    }

    /* When implementing a Consumer this Setter constructor
     * creates circular dependency... so leave it out
     */
//    public QuoteReceiver(RestTemplate template) {
//        this.template = template;
//    }

//    public void accept (Event<Integer> evt) {
//        System.err.println("Get random quote...");
//        Quotation quotation = template.getForObject(
//            "http://gturnquist-quoters.cfapps.io/api/random",
//            Quotation.class
//        );
//        System.err.println("Quote "+ evt.getData() +": "+ quotation.getValue().getQuote());
//        latch.countDown();
//    }

    public void accept (Event<HashMap<String, Object>> evt) {
        System.err.println("Get random quote...");

        Quotation quotation = template.getForObject(
            "http://gturnquist-quoters.cfapps.io/api/random",
            Quotation.class
        );

        ArrayList<Quotation> quotations = (ArrayList<Quotation>) evt.getData().get("quotes");

        System.err.println("Quote "+ evt.getData().get("Count") +": "+ quotation.getValue().getQuote());

        quotations.add(quotation);

        latch.countDown();
    }

}

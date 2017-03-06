package messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.bus.EventBus;

import java.util.concurrent.CountDownLatch;

import static reactor.bus.selector.Selectors.$;

/**
 * Created by revlin on 2/25/17.
 */
@Component
@RestController
@RequestMapping("/")
public class QuotePublishController {
    
    private ListenableFuture<Quotation> fQuotation;
    
    @Autowired
    private EventBus eventBus;
    
    @Autowired
    private QuotePublisher publisher;
    
    @Autowired
    private QuotePublishListener listener;
    
    @Bean
    public QuotePublishListener createEventBus(QuotePublishListener listener) {
        return new QuotePublishListener(this);
    }
    @Autowired
    CountDownLatch latch;

    @RequestMapping(value="")
    public DeferredResult<Quotation> getDefaultUser () {
        DeferredResult<Quotation> result = new DeferredResult<Quotation>();
        long startTime = System.currentTimeMillis();

        try {
            eventBus.on($("restQuote"), listener);
            
            publisher.publishQuotes(1);
            
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        
        System.err.println(fQuotation);
    
        fQuotation.addCallback(new ListenableFutureCallback<Quotation>() {
        
            @Override
            public void onSuccess(Quotation quotation) {
                System.err.println("Finished publishing quote.");
                result.setResult(quotation);
            }
        
            @Override
            public void onFailure(Throwable throwable) {
                result.setErrorResult(throwable.getMessage());
            }
        });

        return result;
    }
    
    public ListenableFuture<Quotation> getFutureQuotation () {
        return this.fQuotation;
    }
    
    public void setFutureQuotation (ListenableFuture<Quotation> fq) {
        this.fQuotation = fq;
    }
}
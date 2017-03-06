package messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import reactor.Environment;
import reactor.bus.EventBus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static reactor.bus.selector.Selectors.$;

/**
 * Created by revlin on 2/25/17.
 */
@SpringBootApplication
//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
public class SpringApp implements CommandLineRunner {

    private static final int DEFAULT_NUMBER_OF_QUOTES = 10;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private QuotePublisher publisher;

    @Autowired
    private QuoteReceiver receiver;

    @Bean
    public CountDownLatch createLatch() {
        return new CountDownLatch(DEFAULT_NUMBER_OF_QUOTES);
    }

    @Bean
    public Environment getEnvironment() {
        return Environment.initializeIfEmpty()
            .assignErrorJournal();
    }

    @Bean
    public EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

    @Override
    public void run (String... args) throws InterruptedException {
        eventBus.on($("quotes"), receiver);
        publisher.publishQuotes(DEFAULT_NUMBER_OF_QUOTES);
    }

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext app = SpringApplication.run(SpringApp.class, args);

        app.getBean(CountDownLatch.class).await(1, TimeUnit.SECONDS);

        app.getBean(Environment.class).shutdown();
    }

}
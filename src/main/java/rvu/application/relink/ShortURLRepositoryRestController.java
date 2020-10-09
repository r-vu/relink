package rvu.application.relink;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class ShortURLRepositoryRestController implements ApplicationEventPublisherAware {

    @Autowired
    private ShortURLRepository repo;

    private ApplicationEventPublisher publisher;

    @PostMapping(value = "/shortURLs")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody ShortURL input) throws JsonProcessingException {

        if(!input.validateDestination()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(input);
        }

        while (input.getName().isEmpty() || repo.findByName(input.getName()) != null) {
            input.resetName();
        }

        publisher.publishEvent(new BeforeCreateEvent(input));
        ShortURL saved = repo.save(input);
        publisher.publishEvent(new AfterCreateEvent(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}

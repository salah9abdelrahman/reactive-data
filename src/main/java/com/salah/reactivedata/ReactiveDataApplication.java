package com.salah.reactivedata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveDataApplication.class, args);
    }

}

@Component
@RequiredArgsConstructor
@Log4j2
class SampleDataInit{
    private final ReservationRepository reservationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void ready(){
        Flux<Reservation> reservations = Flux.just("salah", "hussein", "nour", "lobna")
                .map(e -> new Reservation(null, e))
                .flatMap(e -> reservationRepository.save(e));


        this.reservationRepository.deleteAll()
                .thenMany(reservations)
                .thenMany(reservationRepository.findAll())
                .subscribe(log::info);

    }
}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {
    Flux<Reservation> findByName(String name);
}

@Document
@AllArgsConstructor
@Data
class Reservation {
    @Id
    private String id;
    private String name;
}
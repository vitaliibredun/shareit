package ru.practicum.booking.client;

import ru.practicum.booking.dto.BookingDto;
import ru.practicum.client.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> createBooking(Integer userId, BookingDto bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approvingBooking(Integer userId, Integer bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> findBooking(Integer userId, Integer bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllBookingsCustomer(Integer userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllBookingsOwner(Integer userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}

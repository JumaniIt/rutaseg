package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Author;
import com.jumani.rutaseg.domain.Note;
import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.dto.request.NoteRequest;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders/{orderId}/notes")
public class NoteController {
    private final OrderRepository orderRepo;

    @GetMapping
    public ResponseEntity<List<Note>> getAll(@PathVariable("orderId") long orderId,
                                             @Session SessionInfo session) {

        final Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", orderId)));

        if (!session.admin() && !Objects.equals(order.getClient().getUserId(), session.id())) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(order.getNotes());
    }

    @PostMapping
    public ResponseEntity<Note> addNoteToOrder(@PathVariable("orderId") long orderId,
                                               @RequestBody @Valid NoteRequest noteRequest,
                                               @Session SessionInfo session) {

        final Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", orderId)));

        if (!session.admin() && !Objects.equals(order.getClient().getUserId(), session.id())) {
            throw new ForbiddenException();
        }

        final Author author = session.admin() ? Author.ADMIN : Author.CLIENT;

        final Note note = new Note(author, noteRequest.getContent(), session.id());
        order.addNote(note);

        orderRepo.save(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable("orderId") long orderId,
                                           @PathVariable("noteId") long noteId,
                                           @RequestBody @Valid NoteRequest noteRequest,
                                           @Session SessionInfo session) {

        final Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", orderId)));

        if (!session.admin() && !Objects.equals(order.getClient().getUserId(), session.id())) {
            throw new ForbiddenException();
        }

        final Note note = order.findNote(noteId)
                .orElseThrow(() -> new NotFoundException(String.format("note with id [%s] not found in order [%s]", noteId, orderId)));

        if (!note.isClient() && !session.admin()) {
            throw new ForbiddenException();
        }

        note.update(noteRequest.getContent());

        orderRepo.save(order);

        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable("orderId") long orderId,
                                        @PathVariable("noteId") long noteId,
                                        @Session SessionInfo session) {
        final Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", orderId)));

        if (!session.admin() && !Objects.equals(order.getClient().getUserId(), session.id())) {
            throw new ForbiddenException();
        }

        order.removeNote(noteId)
                .orElseThrow(() -> new NotFoundException(String.format("note with id [%s] not found in order [%s]", noteId, orderId)));

        orderRepo.save(order);

        return ResponseEntity.noContent().build();
    }
}

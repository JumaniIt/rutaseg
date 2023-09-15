package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jumani.rutaseg.domain.OrderStatus.DRAFT;

@Getter
@Entity
@FieldNameConstants
@Table(name = "orders")
@Slf4j
public class Order implements DateGen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "fk_orders_clients"))
    private Client client;

    @Column(name = "pema")
    private boolean pema;

    @Column(name = "port")
    private boolean port;

    @Column(name = "transport")
    private boolean transport;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "finished_at")
    private ZonedDateTime finishedAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private ArrivalData arrivalData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private DriverData driverData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private CustomsData customsData;

    @Column(name = "created_by_user_id")
    private long createdByUserId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "containers",
            joinColumns = @JoinColumn(name = "order_id"))
    private List<Container> containers;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "free_loads",
            joinColumns = @JoinColumn(name = "order_id"))
    private List<FreeLoad> freeLoads;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private ConsigneeData consignee;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<Document> documents;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<Cost> costs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<Note> notes;

    @Column(name = "returned")
    private boolean returned;

    @Column(name = "billed")
    private boolean billed;

    //constructor
    public Order(String code, Client client,
                 boolean pema, boolean port, boolean transport,
                 ArrivalData arrivalData,
                 DriverData driverData,
                 CustomsData customsData,
                 long createdByUserId,
                 List<Container> containers,
                 List<FreeLoad> freeLoads,
                 ConsigneeData consignee) {

        this.code = code;
        this.client = client;
        this.pema = pema;
        this.port = port;
        this.transport = transport;
        this.status = DRAFT;
        this.createdAt = this.currentDateUTC();
        this.finishedAt = null;
        this.arrivalData = arrivalData;
        this.driverData = driverData;
        this.customsData = customsData;
        this.createdByUserId = createdByUserId;
        this.containers = containers;
        this.freeLoads = freeLoads;
        this.consignee = consignee;
        this.documents = new ArrayList<>();
        this.costs = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.returned = false;
        this.billed = false;
    }

    public Long getClientId() {
        return this.client.getId();
    }

    private Order() {
    }

    public void update(String code,
                       Client client,
                       boolean pema, boolean port, boolean transport,
                       ArrivalData arrivalData,
                       DriverData driverData,
                       CustomsData customsData,
                       List<Container> containers,
                       List<FreeLoad> freeLoads,
                       ConsigneeData consignee) {

        this.code = code;
        this.client = client;
        this.pema = pema;
        this.port = port;
        this.transport = transport;
        this.arrivalData = arrivalData;
        this.driverData = driverData;
        this.customsData = customsData;
        this.containers = containers;
        this.freeLoads = freeLoads;
        this.consignee = consignee;
    }

    public void addDocument(Document document) {
        documents.add(document);
    }

    public Optional<Document> removeDocument(long documentId) {
        Optional<Document> documentToRemove = this.findDocument(documentId);

        documentToRemove.ifPresent(doc -> documents.remove(doc));

        return documentToRemove;
    }

    public Optional<Document> findDocument(long documentId) {
        return documents.stream()
                .filter(doc -> doc.getId() == documentId)
                .findFirst();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public void updateCost(Cost cost) {
        costs.add(cost);
    }

    public Optional<Cost> removeCost(long costId) {
        Optional<Cost> costToRemove = this.findCost(costId);

        if (costToRemove.isPresent()) {
            Cost cost = costToRemove.get();
            costs.remove(cost);
            return costToRemove;
        }

        return Optional.empty();
    }

    public Optional<Cost> findCost(long costId) {
        return costs.stream()
                .filter(c -> c.getId() == costId)
                .findFirst();
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }

    public Optional<Note> findNote(long noteId) {
        return notes.stream()
                .filter(note -> note.getId() == noteId)
                .findFirst();
    }

    public Optional<Note> removeNote(long noteId) {
        Optional<Note> documentToRemove = this.findNote(noteId);

        documentToRemove.ifPresent(doc -> notes.remove(doc));

        return documentToRemove;
    }

    public void addSystemNote(String content) {
        final Note note = new Note(Author.SYSTEM, content, null);
        this.addNote(note);
    }

    public void setReturned(boolean returned) {
        this.returned = returned;

        if (this.returned) {
            this.finishedAt = this.currentDateUTC();
        }
    }

    public void setBilled(boolean billed) {
        this.billed = billed;
    }
}

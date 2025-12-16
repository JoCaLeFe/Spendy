package com.spendy.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "transactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    // -------- Vistas --------
    public interface ViewList {}
    public interface ViewDetail extends ViewList {}

    @Id
    @JsonView(ViewList.class)
    private String id;

    // 👇 NUEVO (NO SE SERIALIZA)
    @JsonIgnore
    private String userID;

    @JsonView(ViewList.class)
    private String type;

    @JsonView(ViewList.class)
    private double amount;

    @JsonView(ViewList.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonView(ViewDetail.class)
    private String currency;

    @JsonView(ViewDetail.class)
    private String categoryId;

    @JsonView(ViewDetail.class)
    private String method;

    @JsonView(ViewDetail.class)
    private String note;

    @JsonView(ViewDetail.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

    // -------- Constructores --------
    public Transaction() {}

    // Constructor actual (lo conservas)
    public Transaction(String id,
                       String type,
                       double amount,
                       String currency,
                       String categoryId,
                       String method,
                       LocalDate date,
                       String note,
                       Instant createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.categoryId = categoryId;
        this.method = method;
        this.date = date;
        this.note = note;
        this.createdAt = createdAt;
    }

    // 👇 NUEVO constructor con userID
    public Transaction(String id,
                       String userID,
                       String type,
                       double amount,
                       String currency,
                       String categoryId,
                       String method,
                       LocalDate date,
                       String note,
                       Instant createdAt) {
        this(id, type, amount, currency, categoryId, method, date, note, createdAt);
        this.userID = userID;
    }

    // -------- Getters --------
    public String getId() { return id; }
    public String getUserID() { return userID; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCategoryId() { return categoryId; }
    public String getMethod() { return method; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
    public Instant getCreatedAt() { return createdAt; }

    // -------- Setters --------
    public void setId(String id) { this.id = id; }
    public void setUserID(String userID) { this.userID = userID; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setMethod(String method) { this.method = method; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
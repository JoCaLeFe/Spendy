package com.spendy.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class TransactionCreateDTO {

    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "INCOME|EXPENSE", message = "type debe ser INCOME o EXPENSE")
    private String type;

    @NotNull(message = "El amount es requerido")
    @DecimalMin(value = "0.01", message = "El amount debe ser > 0")
    private Double amount;

    @NotBlank(message = "La moneda es requerida")
    @Size(min = 3, max = 3, message = "La moneda debe tener 3 letras (ISO-4217, p.e. EUR)")
    private String currency;

    @NotBlank(message = "categoryId es requerido")
    private String categoryId;

    @NotBlank(message = "El método es requerido")
    @Pattern(regexp = "CASH|CARD", message = "method debe ser CASH o CARD")
    private String method;

    @NotNull(message = "La fecha es requerida")
    private LocalDate date;

    @Size(max = 140, message = "La nota no debe exceder 140 caracteres")
    private String note;

    public String getType() { return type; }
    public Double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCategoryId() { return categoryId; }
    public String getMethod() { return method; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }

    public void setType(String type) { this.type = type; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setMethod(String method) { this.method = method; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
}
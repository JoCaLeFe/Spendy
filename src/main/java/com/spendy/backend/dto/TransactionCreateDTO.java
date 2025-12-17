package com.spendy.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "DTO para crear una transacción (ingreso o gasto)")
public class TransactionCreateDTO {

    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "INCOME|EXPENSE", message = "type debe ser INCOME o EXPENSE")
    @Schema(description = "Tipo de transacción", example = "EXPENSE", allowableValues = {"INCOME", "EXPENSE"})
    private String type;

    @NotNull(message = "El amount es requerido")
    @DecimalMin(value = "0.01", message = "El amount debe ser > 0")
    @Schema(description = "Monto de la transacción (debe ser > 0)", example = "12.50")
    private Double amount;

    @NotBlank(message = "La moneda es requerida")
    @Size(min = 3, max = 3, message = "La moneda debe tener 3 letras (ISO-4217, p.e. EUR)")
    @Schema(description = "Moneda ISO-4217 (3 letras)", example = "EUR")
    private String currency;

    @NotBlank(message = "categoryId es requerido")
    @Schema(description = "ID de la categoría asociada", example = "6941ddd868190c4b2a1d64ab")
    private String categoryId;

    @NotBlank(message = "El método es requerido")
    @Pattern(regexp = "CASH|CARD", message = "method debe ser CASH o CARD")
    @Schema(description = "Método de pago", example = "CASH", allowableValues = {"CASH", "CARD"})
    private String method;

    @NotNull(message = "La fecha es requerida")
    @Schema(description = "Fecha (yyyy-MM-dd)", example = "2025-12-16")
    private LocalDate date;

    @Size(max = 140, message = "La nota no debe exceder 140 caracteres")
    @Schema(description = "Nota opcional (máx 140 caracteres)", example = "Cena con amigos")
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
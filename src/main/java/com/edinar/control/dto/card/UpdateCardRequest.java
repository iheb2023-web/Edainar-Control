package com.edinar.control.dto.card;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class UpdateCardRequest {

    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String name;

    @Size(max = 30, message = "Le numéro masqué ne doit pas dépasser 30 caractères")
    private String maskedNumber;

    @Pattern(regexp = "\\d{16}", message = "Le numéro de carte doit contenir exactement 16 chiffres")
    private String cardNumber;
    @Size(max = 20)
    private String secretCode;
    @Size(max = 30)
    private String phoneNumber;
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}", message = "La date doit être au format MM/AA")
    private String expirationDate;
    @Size(max = 100)
    private String ownerName;

    @Size(max = 30)
    private String ownerCin;

    @Size(max = 50, message = "Le fournisseur ne doit pas dépasser 50 caractères")
    private String provider;

    private String notes;

    public UpdateCardRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getSecretCode() { return secretCode; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerCin() { return ownerCin; }
    public void setOwnerCin(String ownerCin) { this.ownerCin = ownerCin; }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

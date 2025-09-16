package com.FinTrace.smartWallet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Contact entity representing a contact in the smart wallet application")
public class Contact {
        @Getter
        @Setter
        @Schema(description = "Unique identifier of the contact", example = "1")
        private Long id;
        @Getter
        @Setter
        @Schema(description = "Name of the contact", example = "John Doe")
        private String name;
        @Getter
        @Setter
        @Schema(description = "Email address of the contact", example = "Jd@gmail.com")
        private String email;
        @Getter
        @Setter
        @Schema(description = "Phone number of the contact", example = "1234567890")
        private String PhoneNumber;

        public Contact(String s, String string, String number) {
        }

        public Contact(Long id, String name, String email, String PhoneNumber) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.PhoneNumber = PhoneNumber;
        }

}
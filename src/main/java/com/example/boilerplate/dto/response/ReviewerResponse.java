package com.example.boilerplate.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewerResponse {

    private UUID id;

    private String email;

    private String city;

    private String country;
}

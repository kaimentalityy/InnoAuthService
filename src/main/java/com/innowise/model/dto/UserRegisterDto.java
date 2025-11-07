package com.innowise.model.dto;

import java.time.LocalDate;

public record UserRegisterDto(
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        String username
) {}

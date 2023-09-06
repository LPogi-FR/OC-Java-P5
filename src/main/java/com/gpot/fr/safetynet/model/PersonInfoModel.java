package com.gpot.fr.safetynet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PersonInfoModel {
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private List<String> medication;
    private List<String> allergies;
}

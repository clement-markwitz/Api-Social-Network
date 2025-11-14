package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private String pseudo;
    private String bio;
    private String avatarUrl;
    private String location;
    private List<String> languages;
}
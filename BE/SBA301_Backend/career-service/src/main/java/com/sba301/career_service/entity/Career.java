package com.sba301.career_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "careers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Career {
    @Id
    private String id;
    private String name;
    private String description;
}

package com.example.onlinecinemabackend.web.model.response;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectorResponse {

    private UUID id;

    private String name;

    private Instant birthdate;

    private String information;

}

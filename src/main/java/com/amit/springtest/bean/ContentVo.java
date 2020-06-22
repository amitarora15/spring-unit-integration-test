package com.amit.springtest.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentVo implements Serializable {

    private Long id;

    @NotNull(message = "Title is mandatory")
    private String title;

    private String description;

    private Long yearOfRelease;

}

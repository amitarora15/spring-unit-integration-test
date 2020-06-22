package com.amit.springtest.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class ErrorVo {

    private String fieldName;

    private String message;

    @NonNull private String exceptionName;

}

package com.remember.core.responses.question;

import com.remember.core.domains.PracticeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;



@Getter
@NoArgsConstructor
public class QuestionPracticeStatusResponseDto extends RepresentationModel<QuestionPracticeStatusResponseDto> {
    private Long id;
    private String status;
    private String color;

    public QuestionPracticeStatusResponseDto(PracticeStatus ps) {
        this.id = ps.getId();
        this.status = ps.getStatus();
        this.color = ps.getColor();
    }
}
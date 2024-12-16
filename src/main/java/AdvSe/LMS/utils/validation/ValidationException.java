package AdvSe.LMS.utils.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationException {
    private Integer status;
    private List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }
}

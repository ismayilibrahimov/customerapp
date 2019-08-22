package customerapp.validation;

import customerapp.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, User> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context){
        boolean isValid = user.getPassword().equals(user.getConfirmPassword())
                || new BCryptPasswordEncoder().matches(user.getConfirmPassword(), user.getPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword").addConstraintViolation();
        }

        return isValid;
    }
}
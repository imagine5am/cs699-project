package pm9.trackingserver.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import pm9.trackingserver.entities.Organization;
import pm9.trackingserver.service.OrganizationService;

/**
 * Used to validate user when they register. It uses the constants defined in 'validation.properties'.
 */
@Component
public class UserValidator implements Validator {
    @Autowired
    private OrganizationService organizationService;

    /// This method specifies which class is being validated.
    @Override
    public boolean supports(Class<?> aClass) {
        return Organization.class.equals(aClass);
    }

    /**
     * Method to validate user.
     * @param object which needs to be validated.
     * @param errors Object which contains the errors in the object.
     */
    @Override
    public void validate(Object object, Errors errors) {
        Organization user = (Organization) object;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
        if (user.getName().length() < 6 || user.getName().length() > 32) {
            errors.rejectValue("name", "Size.userForm.name");
        }
        if (organizationService.findByName(user.getName()) != null) {
            errors.rejectValue("name", "Duplicate.userForm.name");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}

package pm9.trackingserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pm9.trackingserver.entities.Organization;
import pm9.trackingserver.service.OrganizationService;
import pm9.trackingserver.service.SecurityService;
import pm9.trackingserver.validator.UserValidator;

/**
 * It a controller which processes all security related things.
 * It contains all methods related to logging in and signing up.
 */
@Controller
public class AuthController {
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SecurityService securityService;

    /**
     * Processes model before signing up.
     * @param model which needs to be passed to the page.
     * @return registration page
     */
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new Organization());
        return "registration";
    }

    /**
     * Processes model after sign up form has been field.
     * @param userForm Details submitted during registration.
     * @param bindingResult Contains results from previous mapping.
     * @return 'welcome' page if registration is successful, else 'registration' page is returned.
     */
    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") Organization userForm,
                               BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        organizationService.createOrganization(userForm);
        securityService.autoLogin(userForm.getName(), userForm.getPasswordConfirm());
        return "redirect:/welcome";
    }

    /**
     * Processes model when you sign in.
     * @param model which needs to be passed to the page.
     * @param error Contains error message.
     * @param logout Option to logout.
     * @return model when you sign in.
     */
    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    /**
     * Returns welcome page when you log in or when you come to the website.
     * @param model which needs to be passed to the page.
     * @return 'welcome' page
     */
    @GetMapping({"/", "/welcome"})
    public String welcome(Model model) {
        String organizationName = securityService.findLoggedInUsername();
        Organization organization = organizationService.findByName(organizationName);
        model.addAttribute("organization", organization);
        return "welcome";
    }
}

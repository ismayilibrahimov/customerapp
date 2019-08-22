package customerapp.controllers;

import customerapp.models.User;
import customerapp.models.onCreate;
import customerapp.models.onUpdate;
import customerapp.repositories.UserRepository;
import customerapp.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String register(@Validated(onCreate.class) @ModelAttribute User user,
                           BindingResult result, Model model,
                           HttpServletRequest request) {
        if (result.hasErrors()) {
            return "registration";
        }

        try {
            String password = user.getPassword();
            if (userService.create(user)) {
                request.login(user.getUsername(), password);
            }
        } catch (ServletException servletException) {
            // authentication failed
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "registration";
        }

        return "redirect:/profile";

    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        User user = userRepository.findByUsername(request.getUserPrincipal().getName());
        model.addAttribute("user", user);
        return "profile";
    }


    @GetMapping("/admin")
    public String admin(Model model) {

        List<User> users = userRepository.findAll();
        Collections.reverse(users);
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/profile/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        if (userRepository.findById(id).isPresent()) {
            String username = userRepository.findById(id).get().getUsername();
            String authenticatedUsername = request.getUserPrincipal().getName();
            if (username.equals(authenticatedUsername) || request.isUserInRole("ROLE_ADMIN")) {
                model.addAttribute("user", userRepository.findById(id).get());
                return "edit";
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/{id}/edit")
    public String update(@Validated({onUpdate.class}) @ModelAttribute User user,
                         BindingResult result, @PathVariable("id") long id, HttpServletRequest request) {

        if (userRepository.findById(id).isPresent()) {
            String username = userRepository.findById(id).get().getUsername();
            String authenticatedUsername = request.getUserPrincipal().getName();
            if (username.equals(authenticatedUsername) || request.isUserInRole("ROLE_ADMIN")) {
                if (result.hasErrors()) {
                    return "edit";
                }
                if (userService.update(user, id, request.getParameter("role"))) {
                    if (request.isUserInRole("ROLE_ADMIN")) {
                        return "redirect:/admin";
                    }
                }
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/{id}/delete")
    public String delete(@PathVariable("id") long id, HttpServletRequest request) {
        String flag = "/profile";
        if (userRepository.findById(id).isPresent()) {

            String username = userRepository.findById(id).get().getUsername();
            String authenticatedUsername = request.getUserPrincipal().getName();
            if (username.equals(authenticatedUsername) || request.isUserInRole("ROLE_ADMIN")) {

                try {
                    if (request.isUserInRole("ROLE_ADMIN")) {
                        flag = "admin";
                    } else {
                        flag = "";
                        request.logout();
                    }
                } catch (ServletException e){
                    // failed
                }
                userRepository.delete(userRepository.findById(id).get());

            }



        }
        return "redirect:/" + flag;
    }




}

package com.cybertek.controller;

import com.cybertek.dto.UserDTO;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.service.RoleService;
import com.cybertek.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    //same path
    @GetMapping({"/create", "/initialize"})
    public String createUser(Model model){

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roleList", roleService.listAllRoles());
        model.addAttribute("userList", userService.listAllUsers());

        return "user/create";
    }

    @PostMapping("/create")
    public String employeeAdd(@ModelAttribute("user") UserDTO userDTO) {
        toString(userDTO);
        userService.save(userDTO);
        return "redirect:/user/create";
    }


    @GetMapping("/update/{username}")
    public String editUser(@PathVariable("username") String username, Model model){
        System.out.println(username);
        model.addAttribute("user", userService.findByUserName(username));
        model.addAttribute("userList", userService.listAllUsers());
        model.addAttribute("roleList", roleService.listAllRoles());
        return "user/update";
    }

    @PostMapping("/update/{username}")
    public String updateUser(UserDTO user){
        userService.update(user);
        return "redirect:/user/create";

    }

    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable("username") String username) throws TicketingProjectException {
        userService.delete(username);
        return "redirect:/user/create";
    }

    public void toString(UserDTO userDTO){
        try {
            ObjectMapper mapper =new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(mapper.writeValueAsString(userDTO));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

package management.personnel.controllers;

import management.personnel.models.User;
import management.personnel.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ScheduleController {

    @Autowired
    private ScheduleService service;

    @RequestMapping(value = "/users", headers = "AcceptVersion=v1", method = RequestMethod.GET)
    public List<User> getAllUsers_V1()
    {
        return service.getAllUsers();
    }


    @RequestMapping(value = "/users/{id}", headers = "AcceptVersion=v1", method = RequestMethod.GET)
    public Optional<User> getUser_V1(@PathVariable("id") UUID id)
    {
        return service.getUser(id);

    }

    @RequestMapping(value = "/users/newuser", headers = "AcceptVersion=v1", method = RequestMethod.POST)
    public String createUser_V1(@RequestBody User user)
    //public String createUserV1()
    {
        service.createUser(user.getFirstname(),user.getLastname(), user.getAddress());
        return "user created";
    }

    @RequestMapping(value = "/users/{id}", headers = "AcceptVersion=v1", method = RequestMethod.PUT)
    public String updateUser_V1(@PathVariable("id") UUID id, @RequestBody User user)
    {
        service.updateUser(id,user.getFirstname(), user.getLastname(), user.getAddress());
        return "user updated";
    }


    @RequestMapping(value = "/users/{id}", headers = "AcceptVersion=v1", method = RequestMethod.DELETE)
    public String deleteUser_V1(@PathVariable("id") UUID id)
    {
        service.deleteUser(id);
        return "user deleted";
    }

    /////////////////
    ////////////////


    //checking versioning is working//
    @RequestMapping(value = "/users", headers = "AcceptVersion=v2", method = RequestMethod.GET)
    public String getAllUsers_V2()
    {
        return "version 2.0";
    }


}

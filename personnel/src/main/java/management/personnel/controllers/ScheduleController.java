package management.personnel.controllers;

import management.personnel.DataStructures.AppointmentOutput;
import management.personnel.models.Appointment;
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


    /////////////////
    //Users API//
    /////////////////

    //get all users//
    @RequestMapping(value = "/users", headers = "AcceptVersion=v1", method = RequestMethod.GET)
    public List<User> getAllUsers_V1()
    {
        return service.getAllUsers();
    }


    //get a given user//
    @RequestMapping(value = "/users/{id}", headers = "AcceptVersion=v1", method = RequestMethod.GET)
    public Optional<User> getUser_V1(@PathVariable("id") Long id)
    {
        return service.getUser(id); //return null if no user with this id//
    }

    //create a user//
    @RequestMapping(value = "/users/newuser", headers = "AcceptVersion=v1", method = RequestMethod.POST)
    public String createUser_V1(@RequestBody User user)
    {
        return service.createUser(user);
    }

    //update a given user//
    @RequestMapping(value = "/users/{id}", headers = "AcceptVersion=v1", method = RequestMethod.PUT)
    public String updateUser_V1(@PathVariable("id") Long id, @RequestBody User user)
    {
        return service.updateUser(id,user.getFirstname(), user.getLastname(), user.getAddress());
    }


    //delete a given user//
    @RequestMapping(value = "/users/{id}", headers = "AcceptVersion=v1", method = RequestMethod.DELETE)
    public String deleteUser_V1(@PathVariable("id") Long id)
    {
        return service.deleteUser(id);
    }

    /////////////////
    //Appointments API//
    /////////////////

    //ruid stands for responsible user ID//
    //Assumption: ruid is sent by the API gateway, so there is no need to check if the user exists in the users DB//


    @RequestMapping(value = "/appointments", headers = "AcceptVersion=v1", method = RequestMethod.GET)
    public List<AppointmentOutput> getAllAppointments_V1()
    {
        return service.getAllAppointments();
    }

    //get appointments of a given user//
    @RequestMapping(value = "/users/{ruid}/appointments", headers = "AcceptVersion=v1", method = RequestMethod.GET)
    public List<AppointmentOutput> getUserAppointments_V1(@PathVariable("ruid") Long ruid)
    {
        return service.getUserAppointments(ruid);
    }

    //create an appointment//
    @RequestMapping(value = "/users/{ruid}/appointments/newappointment", headers = "AcceptVersion=v1", method = RequestMethod.POST)
    public String createAppointment_V1(@PathVariable("ruid") Long ruid, @RequestBody Appointment apmt)
    {
        //return service.createAppointment(ruid, apmt.getTitle(), apmt.getDay(), apmt.getStarttime(), apmt.getEndtime());
        return service.createAppointment(ruid, apmt);
    }


    //update a given appointment//
    @RequestMapping(value = "users/{ruid}/appointments/{appid}", headers = "AcceptVersion=v1", method = RequestMethod.PUT)
    public String updateAppointment_V1(@PathVariable("ruid") Long ruid, @PathVariable("appid") Long appid, @RequestBody Appointment apmt)
    {
        return service.updateAppointment(ruid, appid, apmt);
        //return service.updateAppointment(ruid, appid, apmt.getTitle(), apmt.getDay(), apmt.getStarttime(), apmt.getEndtime());
    }


    //delete a given appointment//
    @RequestMapping(value = "users/{ruid}/appointments/{appid}", headers = "AcceptVersion=v1", method = RequestMethod.DELETE)
    public String deleteAppointment_V1(@PathVariable("ruid") Long ruid, @PathVariable("appid") Long appid)
    {
        return service.deleteAppointment(ruid, appid);
    }


    @RequestMapping(value = "users/{ruid}/appointments/{appid}/users/{uid}", headers = "AcceptVersion=v1", method = RequestMethod.PUT)
    public String AddUserToAppointment_V1(@PathVariable("ruid") Long ruid, @PathVariable("appid") Long appid, @PathVariable("uid") Long uid)
    {
        return service.AddUserToAppointment(ruid, appid, uid);
    }

    @RequestMapping(value = "users/{ruid}/appointments/{appid}/users/{uid}", headers = "AcceptVersion=v1", method = RequestMethod.DELETE)
    public String DeleteUserFromAppointment_V1(@PathVariable("ruid") Long ruid, @PathVariable("appid") Long appid, @PathVariable("uid") Long uid)
    {
        return service.DeleteUserFromAppointment(ruid, appid, uid);
    }

    //later:
    //users/{user-ID}/appointment/{app-ID}/users/{user-ID}/newresponsible [PUT} <change the responsible user>



    /////////////////
    ////////////////


    //checking versioning is working//
    @RequestMapping(value = "/users", headers = "AcceptVersion=v2", method = RequestMethod.GET)
    public String getAllUsers_V2()
    {
        return "version 2.0";
    }


}

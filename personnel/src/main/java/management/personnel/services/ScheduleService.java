package management.personnel.services;

import management.personnel.models.Appointment;
import management.personnel.models.User;
import management.personnel.repositories.AppointmentRepository;
import management.personnel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleService {

    @Autowired
    private UserRepository users;

    @Autowired
    private AppointmentRepository appointments;

    /////////////////
    //Users functions//
    /////////////////

    public List<User> getAllUsers()
    {
        //sorted output. probably not efficient. for a real database, can have an index.//
        /*List<User> L = users.findAll();
        L.sort(Comparator.comparing(User::getLastname));
        return L;*/

        return users.findAll();

    }

    public Optional<User> getUser(UUID id)
    {
        return users.findById(id);
    }

    public String createUser(String fname, String lname, String address)
    {
        users.save(new User(UUID.randomUUID(),fname, lname, address));

        return "user created";
    }



    public String updateUser(UUID id, String fname, String lname, String address)
    {
        if(!users.existsById(id))
            return "user does not exist";

        User user=users.getById(id);
        user.setFirstname(fname);
        user.setLastname(lname);
        user.setAddress(address);
        users.save(user);
        return "user updated";
    }

    public String deleteUser(UUID id)
    {
        if(!users.existsById(id))
            return "user does not exist";

        users.deleteById(id);
        return "user deleted";
    }

    /////////////////
    //Appointments functions//
    /////////////////

    public List<Appointment> getAllAppointments()
    {
        return appointments.findAll();
    }

    public List<Appointment> getUserAppointments(UUID ruid)
    {
        return appointments.findAll(); //modify
    }

    public String createAppointment(UUID ruid, String day, String starttime, String endtime)
    {
        //get appointments of ruid
        //check no conflict with date and time

        appointments.save(new Appointment(UUID.randomUUID(), ruid, day, starttime, endtime));

        return "appointment created";

    }

    public String updateAppointment(UUID ruid, UUID appid, String day, String starttime, String endtime)
    {
        //get appointment with id appid
        //check ruid is the responsible user

        //get appointments of ruid
        //check no conflict with date and time


        //update

        return "appointment updated";

    }

    public String deleteAppointment(UUID ruid, UUID appid)
    {
        //get appointment with id appid
        //check ruid is the responsible user

        //delete

        return "appointment deleted";

    }


}

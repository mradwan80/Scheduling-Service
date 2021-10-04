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
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private UserRepository users;

    @Autowired
    private AppointmentRepository appointments;

    public Long getUsersNum()
    {
        return users.count();
    }

    public Long getAppointmentsNum()
    {
        return appointments.count();
    }

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

        Optional<User> optuser=users.findById(id);
        User user=optuser.get();
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
        List<Appointment> apmts = getAppointmentsByUserID(id);
        for (Appointment apm : apmts) {
            appointments.deleteById(apm.getId());
        }
        return "user deleted";
    }

    /////////////////
    //Appointments functions//
    /////////////////

    private List<Appointment> getAppointmentsByUserID(UUID ruid)
    {
        return appointments.findAll().stream().filter( apmt -> ruid.compareTo(apmt.getUser())==0).collect(Collectors.toList());
    }

    public List<Appointment> getAllAppointments()
    {
        return appointments.findAll();
    }

    public List<Appointment> getUserAppointments(UUID ruid)
    {
        return getAppointmentsByUserID(ruid);
        //return appointments.findAll().stream().filter( apmt -> ruid.compareTo(apmt.getUser())==0).collect(Collectors.toList());
        //return appointments.findAll(); //modify
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
        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optapmt=appointments.findById(appid);
        Appointment apmt=optapmt.get();
        if(ruid.compareTo(apmt.getUser())!=0)
            return "user is not authorized to update the appointment";


        //get appointments of ruid
        //check no conflict with date and time


        //update//
        apmt.setDay(day);
        apmt.setStarttime(starttime);
        apmt.setEndtime(endtime);
        appointments.save(apmt);
        return "appointment updated";

    }

    public String deleteAppointment(UUID ruid, UUID appid)
    {
        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optapmt=appointments.findById(appid);
        Appointment apmt=optapmt.get();
        if(ruid.compareTo(apmt.getUser())!=0)
            return "user is not authorized to delete the appointment";

        //delete//
        appointments.deleteById(appid);

        return "appointment deleted";

    }


}

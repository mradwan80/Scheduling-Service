package management.personnel.services;

import management.personnel.models.Appointment;
import management.personnel.models.User;
import management.personnel.repositories.AppointmentRepository;
import management.personnel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalTime;
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

    private boolean ConflictExists(LocalDate d1, LocalTime starttime1, LocalTime endtime1,LocalDate d2, LocalTime starttime2, LocalTime endtime2)
    {
        if(d1.compareTo(d2)!=0)
            return false;

        if(starttime1.compareTo(starttime2) > 0 && starttime1.compareTo(endtime2) < 0)
            return true;

        if(endtime1.compareTo(starttime2) > 0 && endtime1.compareTo(endtime2) < 0)
            return true;

        if(starttime2.compareTo(starttime1) > 0 && starttime2.compareTo(endtime1) < 0)
            return true;

        if(endtime2.compareTo(starttime1) > 0 && endtime2.compareTo(endtime1) < 0)
            return true;

        if(starttime1.compareTo(starttime2) == 0 && endtime1.compareTo(endtime2) == 0)
            return true;

        return false;
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
        for (Appointment apmt : apmts) {
            appointments.deleteById(apmt.getId());
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

    public String createAppointment(UUID ruid, String title, LocalDate day, LocalTime starttime, LocalTime endtime)
    {
        if(starttime.compareTo(endtime)>0)
            return"invalid time: start time later than end time.";

        //get appointments of ruid. check if conflicts exist with new appointment.//
        boolean conflict=false;
        String conflictMessage="";
        List<Appointment> apmts=getAppointmentsByUserID(ruid);
        for(Appointment apmti:apmts)
        {
            if(ConflictExists(day, starttime, endtime, apmti.getDay(), apmti.getStarttime(), apmti.getEndtime()))
            {
                conflict=true;
                conflictMessage=conflictMessage+apmti.getDay().toString()+", from "+apmti.getStarttime().toString()+" to "+apmti.getEndtime().toString()+"\n";
            }
        }
        if(conflict)
        {
            conflictMessage="Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;
            return conflictMessage;
        }

        appointments.save(new Appointment(UUID.randomUUID(), ruid, title, day, starttime, endtime));

        return "appointment created";

    }

    public String updateAppointment(UUID ruid, UUID appid, String title, LocalDate day, LocalTime starttime, LocalTime endtime)
    {
        if(starttime.compareTo(endtime)>0)
            return"invalid time: start time later than end time.";

        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optapmt=appointments.findById(appid);
        Appointment apmt=optapmt.get();
        if(ruid.compareTo(apmt.getUser())!=0)
            return "user is not authorized to update the appointment";


        //get appointments of ruid. check if conflicts exist with new appointment.//
        boolean conflict=false;
        String conflictMessage="";
        List<Appointment> apmts=getAppointmentsByUserID(ruid);
        for(Appointment apmti:apmts)
        {
            if(ConflictExists(day, starttime, endtime, apmti.getDay(), apmti.getStarttime(), apmti.getEndtime()))
            {
                conflict=true;
                conflictMessage=conflictMessage+apmti.getDay().toString()+", from "+apmti.getStarttime().toString()+" to "+apmti.getEndtime().toString()+"\n";
            }
        }
        if(conflict)
        {
            conflictMessage="Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;
            return conflictMessage;
        }



        //update//
        apmt.setDay(day);
        apmt.setTitle(title);
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

package management.personnel.services;

import management.personnel.DataStructures.AppointmentOutput;
import management.personnel.models.Appointment;
import management.personnel.models.AppointmentUser;
import management.personnel.models.User;
import management.personnel.repositories.AppointmentRepository;
import management.personnel.repositories.AppointmentUserRepository;
import management.personnel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private UserRepository users;

    @Autowired
    private AppointmentRepository appointments;

    @Autowired
    private AppointmentUserRepository appointmentsUsers;

    /////////////////
    //auxiliary functions
    /////////////////

    public Long getUsersCount()
    {
        return users.count();
    }

    public Long getAppointmentsCount()
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

    private List<Appointment> getAppointmentsByUserID(UUID ruid)
    {
        return appointments.findAll().stream().filter( appointment -> ruid.compareTo(appointment.getUser())==0).collect(Collectors.toList());
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
        List<Appointment> appointmentList = getAppointmentsByUserID(id);
        for (Appointment appointment : appointmentList) {
            appointments.deleteById(appointment.getId());
        }
        return "user deleted";
    }

    /////////////////
    //Appointments functions//
    /////////////////



    /*public List<Appointment> getAllAppointments()
    {
        return appointments.findAll();
    }*/

    //still needs modification
    public List<AppointmentOutput> getAllAppointments()
    {
        List<Appointment> appointmentList =appointments.findAll();
        List<AppointmentOutput> outList = new ArrayList<AppointmentOutput>();

        for(Appointment appointment:appointmentList)
        {
            AppointmentOutput ao = new AppointmentOutput();
            ao.setAppointment(appointment);

            List<AppointmentUser> appUserList = appointmentsUsers.findAll().stream().filter(appu -> appu.getAppointmentID().compareTo(appointment.getId())==0).collect(Collectors.toList());

            for(AppointmentUser appUser: appUserList)
            {
                ao.addParticipant(appUser.getUserID().toString()); //change. need to get user name from users !
            }

            outList.add(ao);
        }

        return outList;
    }


    /*public List<Appointment> getUserAppointments(UUID ruid)
    {
        return getAppointmentsByUserID(ruid);
    }*/

    public List<Appointment> getUserAppointments(UUID ruid)
    {
        List<AppointmentUser> appUserList = appointmentsUsers.findAll().stream().filter(appu -> appu.getUserID().compareTo(ruid)==0).collect(Collectors.toList());

        List<Appointment> outList = new ArrayList<Appointment>();

        for(AppointmentUser appUser: appUserList)
        {
            Optional<Appointment> optAppointment = appointments.findAll().stream().filter(apmt -> apmt.getId().compareTo(appUser.getAppointmentID())==0).collect(Collectors.toList()).stream().findFirst();
            outList.add(optAppointment.get());
        }
        return outList;
    }


    public String createAppointment(UUID ruid, String title, LocalDate day, LocalTime starttime, LocalTime endtime)
    {
        if(starttime.compareTo(endtime)>0)
            return"invalid time: start time later than end time.";

        //get appointments of ruid. check if conflicts exist with new appointment.//
        boolean conflict=false;
        String conflictMessage="";
        List<Appointment> appointmentList=getAppointmentsByUserID(ruid);
        for(Appointment appointmenti:appointmentList)
        {
            if(ConflictExists(day, starttime, endtime, appointmenti.getDay(), appointmenti.getStarttime(), appointmenti.getEndtime()))
            {
                conflict=true;
                conflictMessage=conflictMessage+appointmenti.getDay().toString()+", from "+appointmenti.getStarttime().toString()+" to "+appointmenti.getEndtime().toString()+"\n";
            }
        }
        if(conflict)
        {
            conflictMessage="Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;
            return conflictMessage;
        }

        UUID appid=UUID.randomUUID();
        appointments.save(new Appointment(appid, ruid, title, day, starttime, endtime));

        appointmentsUsers.save(new AppointmentUser(UUID.randomUUID(),appid, ruid));

        return "appointment created";

    }

    public String updateAppointment(UUID ruid, UUID appid, String title, LocalDate day, LocalTime starttime, LocalTime endtime)
    {
        if(starttime.compareTo(endtime)>0)
            return"invalid time: start time later than end time.";

        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optAppointment=appointments.findById(appid);
        Appointment appointment=optAppointment.get();
        if(ruid.compareTo(appointment.getUser())!=0)
            return "user is not authorized to update the appointment";


        //get appointments of ruid. check if conflicts exist with new appointment.//
        boolean conflict=false;
        String conflictMessage="";
        List<Appointment> appointmentList=getAppointmentsByUserID(ruid);
        for(Appointment appointmenti:appointmentList)
        {
            if(appointmenti.getId().compareTo(appid)==0)    //do not compare with original appointment//
                continue;

            if(ConflictExists(day, starttime, endtime, appointmenti.getDay(), appointmenti.getStarttime(), appointmenti.getEndtime()))
            {
                conflict=true;
                conflictMessage=conflictMessage+appointmenti.getDay().toString()+", from "+appointmenti.getStarttime().toString()+" to "+appointmenti.getEndtime().toString()+"\n";
            }
        }
        if(conflict)
        {
            conflictMessage="Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;
            return conflictMessage;
        }


        //update//
        appointment.setDay(day);
        appointment.setTitle(title);
        appointment.setStarttime(starttime);
        appointment.setEndtime(endtime);
        appointments.save(appointment);
        return "appointment updated";

    }

    public String deleteAppointment(UUID ruid, UUID appid)
    {
        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optAppointment=appointments.findById(appid);
        Appointment appointment=optAppointment.get();
        if(ruid.compareTo(appointment.getUser())!=0)
            return "user is not authorized to delete the appointment";

        //delete//
        appointments.deleteById(appid);

        return "appointment deleted";

    }

    public String addUserToAppointment(UUID ruid, UUID appid, UUID uid)
    {
        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optAppointment=appointments.findById(appid);
        Appointment appointment=optAppointment.get();
        if(ruid.compareTo(appointment.getUser())!=0)
            return "user is not authorized to delete the appointment";





        Optional<AppointmentUser> optAppointmentUser = appointmentsUsers.findAll().stream().filter(appu -> appu.getUserID().compareTo(uid) == 0 && appu.getAppointmentID().compareTo(appid)==0).collect(Collectors.toList()).stream().findFirst();
        if(!optAppointmentUser.isEmpty())
            return "user is already participating in the appointment";

        appointmentsUsers.save(new AppointmentUser(UUID.randomUUID(),appid, uid));

        return "user added to the appointment as a participant";

    }



}

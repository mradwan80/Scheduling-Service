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

    private List<AppointmentUser> getAppointmentUserPairsOfUser(Long uid)
    {
        return appointmentsUsers.findAll().stream().filter(appu -> appu.getUserID() ==uid).collect(Collectors.toList());
    }

    private List<AppointmentUser> getAppointmentUserPairsOfAppointment(Long appid)
    {
        return appointmentsUsers.findAll().stream().filter(appu -> appu.getAppointmentID() ==appid).collect(Collectors.toList());
    }


    private List<Appointment> getAppointmentsOfResponsibleUser(Long uid)
    {
        return appointments.findAll().stream().filter( appointment -> uid == appointment.getUser()).collect(Collectors.toList());
    }

    private List<Appointment> getAppointmentsOfParticipatingUser(Long uid)
    {
        List<Appointment> outL=new ArrayList<>();

        List<AppointmentUser> appointmenUserList = getAppointmentUserPairsOfUser(uid);

        for(AppointmentUser appointmentUser: appointmenUserList)
        {
            Appointment appointment = appointments.findById(appointmentUser.getAppointmentID()).get();
            outL.add(appointment);
        }

        return outL;
    }

    private void deleteAppointmentUserPairsOfAppointment(Long appid)
    {
        List<AppointmentUser> appointmenUserList = getAppointmentUserPairsOfAppointment(appid);

        for(AppointmentUser appointmentUser: appointmenUserList)
        {
            appointmentsUsers.deleteById(appointmentUser.getId());
        }

    }

    private String ConflictAppointmentVsParticipatingUserAppointments(Long uid, Appointment appointment, Long exceptionAppointmentID)
    {
        boolean conflict=false;
        String conflictMessage="";
        List<Appointment> appointmentList=getAppointmentsOfParticipatingUser(uid);
        for(Appointment appointmenti:appointmentList)
        {
            if(appointmenti.getId()==exceptionAppointmentID)
                continue;

            if(ConflictExists(appointment.getDay(), appointment.getStarttime(), appointment.getEndtime(), appointmenti.getDay(), appointmenti.getStarttime(), appointmenti.getEndtime()))
            {
                conflict=true;
                conflictMessage=conflictMessage+appointmenti.getDay().toString()+", from "+appointmenti.getStarttime().toString()+" to "+appointmenti.getEndtime().toString()+"\n";
            }
        }
        if(conflict)
        {
            //conflictMessage="Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;
            return conflictMessage;
        }
        else
            return "";
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

    public Optional<User> getUser(Long id)  //returns null if user does not exist//
    {
        return users.findById(id);
    }

    public String createUser(User user)
    {
        users.save(user);

        return "user created";
    }


    public String updateUser(Long id, String fname, String lname, String address)
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

    public String deleteUser(Long id)
    {
        if(!users.existsById(id))
            return "user does not exist";

        //delete user//
        users.deleteById(id);

        //delete appointments that the user is responsible for//
        List<Appointment> appointmentList = getAppointmentsOfResponsibleUser(id);
        for (Appointment appointment : appointmentList) {
            appointments.deleteById(appointment.getId());
            deleteAppointmentUserPairsOfAppointment(appointment.getId());
        }

        //delete user from all appointments he is participating//
        List<AppointmentUser> appointmentUserList = appointmentsUsers.findAll().stream().filter(appu -> appu.getUserID()==id).collect(Collectors.toList());

        for(AppointmentUser appointmentUser: appointmentUserList)
        {
            appointmentsUsers.deleteById(appointmentUser.getId());
        }
        return "user deleted";
    }


    /////////////////
    //Appointments functions//
    /////////////////

    public List<AppointmentOutput> getAllAppointments()
    {
        List<AppointmentOutput> outList = new ArrayList<AppointmentOutput>();

        List<Appointment> appointmentList =appointments.findAll();

        for(Appointment appointment:appointmentList)
        {
            AppointmentOutput ao = new AppointmentOutput();

            ao.setAppointment(appointment);

            List<AppointmentUser> appUserList =getAppointmentUserPairsOfAppointment(appointment.getId());
            for(AppointmentUser appUser: appUserList)
            {
                ao.addParticipant(appUser.getUserID().toString()); //later: search for name and add it//
            }

            outList.add(ao);
        }

        return outList;
    }


    public List<AppointmentOutput> getUserAppointments(Long ruid)
    {
        List<AppointmentOutput> outList = new ArrayList<AppointmentOutput>();

        //get all appointments of the user//
        List<Appointment> appointmentsList = getAppointmentsOfParticipatingUser(ruid);


        //for each appointment//
        for(Appointment appointment: appointmentsList)
        {
            AppointmentOutput ao = new AppointmentOutput();

            ao.setAppointment(appointment);

            List<AppointmentUser> appUserList =getAppointmentUserPairsOfAppointment(appointment.getId());
            for(AppointmentUser appUser: appUserList)
            {
                ao.addParticipant(appUser.getUserID().toString()); //later: search for name and add it//
            }

            outList.add(ao);
        }
        return outList;
    }


    public String createAppointment(Long ruid, Appointment inputAppointment)
    {
        if(inputAppointment.getStarttime().compareTo(inputAppointment.getEndtime())>0)
            return"invalid time: start time later than end time.";

        Appointment newAppointment=new Appointment(null, ruid, inputAppointment.getTitle(), inputAppointment.getDay(), inputAppointment.getStarttime(),inputAppointment.getEndtime());

        String conflictMessage = ConflictAppointmentVsParticipatingUserAppointments(ruid,newAppointment,null);
        if(conflictMessage!="")
            return "Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;

        //get appointments of ruid. check if conflicts exist with new appointment.//
        /*boolean conflict=false;
        String conflictMessage="";
        List<Appointment> appointmentList=getAppointmentsOfParticipatingUser(ruid);
        for(Appointment appointmenti:appointmentList)
        {
            if(ConflictExists(inputAppointment.getDay(), inputAppointment.getStarttime(), inputAppointment.getEndtime(), appointmenti.getDay(), appointmenti.getStarttime(), appointmenti.getEndtime()))
            {
                conflict=true;
                conflictMessage=conflictMessage+appointmenti.getDay().toString()+", from "+appointmenti.getStarttime().toString()+" to "+appointmenti.getEndtime().toString()+"\n";
            }
        }
        if(conflict)
        {
            conflictMessage="Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;
            return conflictMessage;
        }*/


        appointments.save(newAppointment);

        appointmentsUsers.save(new AppointmentUser(null,newAppointment.getId(), ruid));

        return "appointment created";

    }


    public String updateAppointment(Long ruid, Long appid, Appointment updatedAppointment)
    {
        if(updatedAppointment.getStarttime().compareTo(updatedAppointment.getEndtime())>0)
            return"invalid time: start time later than end time.";

        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        /*Optional<Appointment> optAppointment=appointments.findById(appid);
        Appointment appointment=optAppointment.get();*/
        Appointment appointment=appointments.findById(appid).get();
        if(ruid!=appointment.getUser())
            return "user is not authorized to update the appointment";


        //get appointments of ruid. check if conflicts exist with new appointment.//
        /*boolean conflict=false;
        String conflictMessage="";

        List<Appointment> appointmentList=getAppointmentsByUserID(ruid);
        for(Appointment appointmenti:appointmentList)
        {
            if(appointmenti.getId()==appid)    //do not compare with original appointment//
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
        }*/

        String conflictMessage="";
        List<AppointmentUser> appointmentUserList = getAppointmentUserPairsOfAppointment(appid);
        for(AppointmentUser appointmentUser:appointmentUserList) {
            String Msg = ConflictAppointmentVsParticipatingUserAppointments(appointmentUser.getUserID(), updatedAppointment, appointment.getId());
            conflictMessage = conflictMessage+Msg;
        }
        if(conflictMessage.length()!=0)
            return "Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;



        //update//
        appointment.setDay(updatedAppointment.getDay());
        appointment.setTitle(updatedAppointment.getTitle());
        appointment.setStarttime(updatedAppointment.getStarttime());
        appointment.setEndtime(updatedAppointment.getEndtime());
        appointments.save(appointment);
        return "appointment updated";

    }


    public String deleteAppointment(Long ruid, Long appid)
    {
        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optAppointment=appointments.findById(appid);
        Appointment appointment=optAppointment.get();
        if(ruid != appointment.getUser())
            return "user is not authorized to delete the appointment";

        //delete//
        appointments.deleteById(appid);

        deleteAppointmentUserPairsOfAppointment(appid);

        return "appointment deleted";

    }


    public String AddUserToAppointment(Long ruid, Long appid, Long uid)
    {
        //make sure appointment exists//
        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //make sure user exists//
        if(!users.existsById(uid))
            return "user does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optAppointment=appointments.findById(appid);
        Appointment appointment=optAppointment.get();
        if(ruid != appointment.getUser())
            return "user is not authorized to add another user to  the appointment";

        //make sure user is not already participating//
        List<AppointmentUser> appointmentUserList = appointmentsUsers.findAll().stream().filter(appu -> appu.getAppointmentID()==appid && appu.getUserID()==uid).collect(Collectors.toList());
        if(appointmentUserList.size()>0)
            return "user is already participating in the appointment";

        String conflictMessage = ConflictAppointmentVsParticipatingUserAppointments(uid,appointment,null);
        if(conflictMessage!="")
            return "Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;

        //get appointments of uid. check if conflicts exist with new appointment.//
        /*boolean conflict=false;
        String conflictMessage="";
        List<Appointment> appointmentList=getAppointmentsOfParticipatingUser(uid);
        for(Appointment appointmenti:appointmentList)
        {

            if(ConflictExists(appointment.getDay(), appointment.getStarttime(), appointment.getEndtime(), appointmenti.getDay(), appointmenti.getStarttime(), appointmenti.getEndtime()))
            {
                conflict=true;
                conflictMessage=conflictMessage+appointmenti.getDay().toString()+", from "+appointmenti.getStarttime().toString()+" to "+appointmenti.getEndtime().toString()+"\n";
            }
        }
        if(conflict)
        {
            conflictMessage="Can not update the appointment with these data and time values. They conflict with the following appointment(s):\n"+conflictMessage;
            return conflictMessage;
        }*/


        appointmentsUsers.save(new AppointmentUser(null,appid,uid));
        return "user added as a participant to the appointment";
    }

    //The user responsible fro an appointment can not delete himself from it. later, will provide a function to allow the responsible user to assign another one, and can then delete himself//
    public String DeleteUserFromAppointment(Long ruid, Long appid, Long uid)
    {
        //make sure appointment exists//
        if(!appointments.existsById(appid))
            return "appointment does not exist";

        //make sure user exists//
        if(ruid==uid)
            return "can not delete the responsible user from the appointment";

        //make sure user exists//
        if(!users.existsById(uid))
            return "user does not exist";

        //get appointment with id appid. check ruid is the responsible user//
        Optional<Appointment> optAppointment=appointments.findById(appid);
        Appointment appointment=optAppointment.get();
        if(ruid != appointment.getUser())
            return "user is not authorized to remove another user from  the appointment";

        //make sure user is participating already//
        List<AppointmentUser> appointmentUserList = appointmentsUsers.findAll().stream().filter(appu -> appu.getAppointmentID()==appid && appu.getUserID()==uid).collect(Collectors.toList());
        if(appointmentUserList.size()==0)
            return "user is already not participating in the appointment";


        Optional<AppointmentUser> appointmentUser= appointmentUserList.stream().findFirst();
        appointmentsUsers.deleteById(appointmentUser.get().getId());
        return "user deleted as a participant from the appointment";
    }



}

package management.personnel.DataStructures;

import lombok.Data;
import management.personnel.models.Appointment;

import java.util.ArrayList;
import java.util.List;

//An object holds details of an appointment, plus the user participating in it//
//output as a response body//
@Data
public class AppointmentOutput
{
    private Appointment appointment;
    private List<String> participatingUsers = new ArrayList<String>();;

    public void addParticipant(String username)
    {
        participatingUsers.add(username);

    }
}

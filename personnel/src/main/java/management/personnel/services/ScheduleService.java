package management.personnel.services;

import management.personnel.models.User;
import management.personnel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleService {

    @Autowired
    private UserRepository users;


        /*UUID u0=UUID.randomUUID();
        //UUID u1=UUID.randomUUID();
        UUID u2=UUID.randomUUID();
        UUID u3=UUID.randomUUID();
        UUID u4=UUID.randomUUID();
        UUID u5=UUID.randomUUID();
        UUID u6=UUID.randomUUID();*/

        //users.save(new User(u0,"Matthias", "Schultz","Huetteldorfer Strasse 1, 1140 Wien"));
        //users.save(new User(u1,"Adam", "Kruger","Huetteldorfer Strasse 2, 1140 Wien"));
        //users.save(new User(u2,"Kati", "Mayerhofer","Huetteldorfer Strasse 3, 1140 Wien"));
        //users.save(new User(u3,"Stefan", "Fischer","Josef Baumann Gasse 1, 1220 Wien"));
        //users.save(new User(UUID.randomUUID(),"Anita", "Weber","Josef Baumann Gasse 2, 1220 Wien"));
        //users.save(new User(UUID.randomUUID(),"Peter", "Schmidt","Josef Baumann Gasse 3, 1220 Wien"));
        //users.save(new User(UUID.randomUUID(),"Markus", "Wagner","Josef Baumann Gasse 4, 1220 Wien"));

       // appointments.save(new Appointment(UUID.randomUUID(),u0,"","",""));
        //appointments.save(new Appointment(UUID.randomUUID(),u1,"","",""));
        //appointments.save(new Appointment(UUID.randomUUID(),u4,"","",""));
        //appointments.save(new Appointment(UUID.randomUUID(),u6,"","",""));

    public List<User> getAllUsers()
    {
        //sorted output. probably not efficient. for a real database, can have an index.//
        /*List<User> L = users.findAll();
        L.sort(Comparator.comparing(User::getLastname));
        return L;*/

        return users.findAll();

    }

    public void createUser(String fname, String lname, String address)
    {
        users.save(new User(UUID.randomUUID(),fname, lname, address));
    }

    public Optional<User> getUser(UUID id)
    {
        return users.findById(id);
    }

    public void updateUser(UUID id, String fname, String lname, String address)
    {
        Optional<User> user = users.findById(id);
        if(!user.isEmpty()) {
            User actualuser = user.get();
            actualuser.setFirstname(fname);
            actualuser.setLastname(lname);
            actualuser.setAddress(address);
            users.save(actualuser);
        }

    }

    public void deleteUser(UUID id)
    {
        users.deleteById(id);
    }
}

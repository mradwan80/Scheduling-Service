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

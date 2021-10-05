package management.personnel.repositories;

import management.personnel.DataStructures.AppointmentOutput;
import management.personnel.models.AppointmentUser;
import management.personnel.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppointmentUserRepository extends JpaRepository<AppointmentUser, UUID> {
}

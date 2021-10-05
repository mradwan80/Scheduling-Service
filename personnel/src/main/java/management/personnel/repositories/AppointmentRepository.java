package management.personnel.repositories;

import management.personnel.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {
}

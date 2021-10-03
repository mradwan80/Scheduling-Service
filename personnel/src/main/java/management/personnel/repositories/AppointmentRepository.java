package management.personnel.repositories;

import management.personnel.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository  extends JpaRepository<Appointment, UUID> {
}

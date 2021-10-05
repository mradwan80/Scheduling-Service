package management.personnel.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUser {

    @Id
    //@GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;

    @NonNull
    private UUID appointmentID;

    @NonNull
    private UUID userID;

}

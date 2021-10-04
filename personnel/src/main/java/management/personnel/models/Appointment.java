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
public class Appointment {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;

    @NonNull
    private UUID user;  //responsible user//

    @NonNull
    private String day;

    @NonNull
    private String starttime;

    @NonNull
    private String endtime;




}

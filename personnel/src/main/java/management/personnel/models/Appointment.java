package management.personnel.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    //@NonNull
    private Long user;  //responsible user//

    private String title;

    @NonNull
    private LocalDate day;

    @NonNull
    private LocalTime starttime;

    @NonNull
    private LocalTime endtime;


}

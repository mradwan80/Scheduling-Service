package management.personnel.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    //@GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;

    @NonNull
    private String firstname;

    @NonNull
    private String lastname;

    private String address;


}

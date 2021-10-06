package management.personnel;

import management.personnel.models.Appointment;
import management.personnel.models.User;
import management.personnel.services.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static java.lang.Math.toIntExact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PersonnelApplicationTests {

	@Autowired
	private ScheduleService service;


	@Test
	void createUser_test()
	{
		int origSize= toIntExact(service.getUsersCount());

		service.createUser(new User(null, "Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien"));

		List<User> L= service.getAllUsers(); //not tested yet//

		assertEquals(1, L.size()-origSize);

		int index=0;
		User user = L.get(index+origSize);

		assertEquals("Matthias", user.getFirstname());
		assertEquals("Schultz", user.getLastname());
		assertEquals("Huetteldorfer Strasse 1, 1140 Wien", user.getAddress());


	}

	@Test
	void getAllUsers_test()
	{
		int origSize= toIntExact(service.getUsersCount());

		//creatUser is already tested//
		service.createUser(new User(null, "Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien"));
		service.createUser(new User(null, "Adam","Kruger","Huetteldorfer Strasse 2, 1140 Wien"));
		service.createUser(new User(null, "Kati","Mayerhofer","Huetteldorfer Strasse 3, 1140 Wien"));
		service.createUser(new User(null, "Stefan","Fischer","Josef Baumann Gasse 1, 1220 Wien"));
		service.createUser(new User(null, "Anita","Weber","Josef Baumann Gasse 2, 1220 Wien"));
		service.createUser(new User(null, "Peter","Schmidt","Josef Baumann Gasse 3, 1220 Wien"));
		service.createUser(new User(null, "Markus","Wagner","Josef Baumann Gasse 4, 1220 Wien"));

		List<User> L = service.getAllUsers();

		assertEquals(7, L.size()-origSize);

	}


	@Test
	void getUser_test()
	{
		//creatUser is already tested//
		service.createUser(new User(null, "Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien"));
		service.createUser(new User(null, "Adam","Kruger","Huetteldorfer Strasse 2, 1140 Wien"));
		service.createUser(new User(null, "Kati","Mayerhofer","Huetteldorfer Strasse 3, 1140 Wien"));

		List<User> L= service.getAllUsers(); //already tested//

		Optional<User> user1 = L.stream().findFirst();
		User gotuser1=user1.get();
		Long id1 = gotuser1.getId();


		Optional<User> user2 = service.getUser(id1);
		User gotuser2=user2.get();

		assertEquals(gotuser1.getId(),gotuser2.getId());
		assertEquals(gotuser1.getFirstname(),gotuser2.getFirstname());
		assertEquals(gotuser1.getLastname(),gotuser2.getLastname());
		assertEquals(gotuser1.getAddress(),gotuser2.getAddress());

	}

	@Test
	void deleteUser_test()
	{
		int origSize= toIntExact(service.getUsersCount());

		//creatUser is already tested//
		service.createUser(new User(null, "Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien"));
		service.createUser(new User(null, "Adam","Kruger","Huetteldorfer Strasse 2, 1140 Wien"));
		service.createUser(new User(null, "Kati","Mayerhofer","Huetteldorfer Strasse 3, 1140 Wien"));

		List<User> L= service.getAllUsers(); //already tested//

		int index=1;
		User user = L.get(index+ origSize); //Adam Kruger//

		service.deleteUser(user.getId());

		Optional<User> user2 = service.getUser(user.getId());	//already tested//

		assertTrue(user2.isEmpty());


	}

	@Test
	void deleteUserAndItsAppointments_test()
	{
		int origSize= toIntExact(service.getUsersCount());

		//creatUser is already tested//
		service.createUser(new User(null, "Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien"));
		service.createUser(new User(null, "Adam","Kruger","Huetteldorfer Strasse 2, 1140 Wien"));

		List<User> L= service.getAllUsers(); //already tested//

		int index=0;
		User user1 = L.get(index+ origSize); //Matthias Schultz//

		index=1;
		User user2 = L.get(index+ origSize); //Adam Kruger//


		service.createAppointment(user1.getId(), new Appointment(null, null, "title", LocalDate.of(2021, 10, 6),LocalTime.of(13,0),LocalTime.of(14,0)));
		service.createAppointment(user2.getId(), new Appointment(null, null,"title",LocalDate.of(2021, 10, 7),LocalTime.of(13,0),LocalTime.of(14,0)));
		service.createAppointment(user1.getId(), new Appointment(null, null,"title",LocalDate.of(2021, 10, 8),LocalTime.of(13,0),LocalTime.of(14,0)));

		/*List<Appointment> Lbefore=service.getUserAppointments(user1.getId());


		service.deleteUser(user1.getId());

		List<Appointment> Lafter=service.getUserAppointments(user1.getId());

		assertEquals(2, Lbefore.size()-Lafter.size());*/

	}

	@Test
	void updateUser_test()
	{
		int origSize= toIntExact(service.getUsersCount());

		//creatUser is already tested//
		service.createUser(new User(null,"Stefan","Fischer","Josef Baumann Gasse 1, 1220 Wien"));
		service.createUser(new User(null,"Anita","Weber","Josef Baumann Gasse 2, 1220 Wien"));
		service.createUser(new User(null,"Peter","Schmidt","Josef Baumann Gasse 3, 1220 Wien"));
		service.createUser(new User(null,"Markus","Wagner","Josef Baumann Gasse 4, 1220 Wien"));

		List<User> L= service.getAllUsers(); //already tested//

		int index=2;
		User user = L.get(index+origSize); //Peter Schmidt//

		service.updateUser(user.getId(),"Simon","Huber","Josef Baumann Gasse 3, 1220 Wien");

		Optional<User> user2 = service.getUser(user.getId()); //already tested//

		assertFalse(user2.isEmpty());

		User updatedUser=user2.get();

		assertEquals("Simon", updatedUser.getFirstname());
		assertEquals("Huber", updatedUser.getLastname());
		assertEquals("Josef Baumann Gasse 3, 1220 Wien", updatedUser.getAddress());

	}

	@Disabled
	@Test
	void CheckDateAndTime()
	{
		//LocalDate.of(2021, 10, 6),LocalTime.of(13,0),LocalTime.of(14,0)
		LocalDate d1=LocalDate.of(2021, 10, 6);
		LocalDate d2=LocalDate.of(2021, 10, 6);
		LocalDate d3=LocalDate.of(2021, 10, 5);
		LocalDate d4=LocalDate.of(2021, 10, 7);

		int cmp;
		cmp= d1.compareTo(d2);
		cmp=d1.compareTo(d3);
		cmp=d1.compareTo(d4);

		int u=cmp;
	}


}

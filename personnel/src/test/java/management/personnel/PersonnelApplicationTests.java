package management.personnel;

import management.personnel.models.User;
import management.personnel.services.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
		service.createUser("Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien");

		List<User> L= service.getAllUsers(); //not tested yet//

		assertEquals(1, L.size());

		Optional<User> user = L.stream().findFirst();

		assertFalse(user.isEmpty());

		User actualUser= user.get();

		assertEquals("Matthias", actualUser.getFirstname());
		assertEquals("Schultz", actualUser.getLastname());
		assertEquals("Huetteldorfer Strasse 1, 1140 Wien", actualUser.getAddress());


	}

	@Test
	void getAllUsers_test()
	{
		//creatUser is already tested//
		service.createUser("Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien");
		service.createUser("Adam","Kruger","Huetteldorfer Strasse 2, 1140 Wien");
		service.createUser("Kati","Mayerhofer","Huetteldorfer Strasse 3, 1140 Wien");
		service.createUser("Stefan","Fischer","Josef Baumann Gasse 1, 1220 Wien");
		service.createUser("Anita","Weber","Josef Baumann Gasse 2, 1220 Wien");
		service.createUser("Peter","Schmidt","Josef Baumann Gasse 3, 1220 Wien");
		service.createUser("Markus","Wagner","Josef Baumann Gasse 4, 1220 Wien");

		List<User> L = service.getAllUsers();

		assertEquals(7, L.size());

	}

	@Test
	void getUser_test()
	{
		//creatUser is already tested//
		service.createUser("Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien");
		service.createUser("Adam","Kruger","Huetteldorfer Strasse 2, 1140 Wien");
		service.createUser("Kati","Mayerhofer","Huetteldorfer Strasse 3, 1140 Wien");

		List<User> L= service.getAllUsers(); //already tested//

		Optional<User> user1 = L.stream().findFirst();
		User gotuser1=user1.get();
		UUID id1 = gotuser1.getId();


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
		//creatUser is already tested//
		service.createUser("Matthias","Schultz","Huetteldorfer Strasse 1, 1140 Wien");
		service.createUser("Adam","Kruger","Huetteldorfer Strasse 2, 1140 Wien");
		service.createUser("Kati","Mayerhofer","Huetteldorfer Strasse 3, 1140 Wien");

		List<User> L= service.getAllUsers(); //already tested//

		User user = L.get(1); //Adam Kruger//

		service.deleteUser(user.getId());

		Optional<User> user2 = service.getUser(user.getId());	//already tested//

		assertTrue(user2.isEmpty());


	}

	@Test
	void updateUser_test()
	{
		//creatUser is already tested//
		service.createUser("Stefan","Fischer","Josef Baumann Gasse 1, 1220 Wien");
		service.createUser("Anita","Weber","Josef Baumann Gasse 2, 1220 Wien");
		service.createUser("Peter","Schmidt","Josef Baumann Gasse 3, 1220 Wien");
		service.createUser("Markus","Wagner","Josef Baumann Gasse 4, 1220 Wien");

		List<User> L= service.getAllUsers(); //already tested//

		User user = L.get(2); //Peter Schmidt//

		service.updateUser(user.getId(),"Simon","Huber","Josef Baumann Gasse 3, 1220 Wien");

		Optional<User> user2 = service.getUser(user.getId()); //already tested//

		assertFalse(user2.isEmpty());

		User updatedUser=user2.get();

		assertEquals("Simon", updatedUser.getFirstname());
		assertEquals("Huber", updatedUser.getLastname());
		assertEquals("Josef Baumann Gasse 3, 1220 Wien", updatedUser.getAddress());



	}
}
package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.*;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {

	@Override
protected FakeApplication provideFakeApplication() {
    return Helpers.fakeApplication(Helpers.inMemoryDatabase());
}
	
	FakeApplication fakeAppWithMemoryDb = provideFakeApplication();

	@Test
	public void createAndRetrieveUser() {
		running(fakeAppWithMemoryDb, new Runnable() {
			public void run() {
				new User("rshah@bugbare.co.uk", "Rishi", "password").save();
				User rishi = User.find.where().eq("email","rshah@bugbare.co.uk").findUnique();
				assertNotNull(rishi);
				assertEquals("Rishi", rishi.name);
			}
		});
	}

	@Test
    public void tryAuthenticateUser() {
        new User("rshah@bugbare.co.uk", "Rishi", "password").save();
        
        assertNotNull(User.authenticate("rshah@bugbare.co.uk", "password"));
        assertNull(User.authenticate("rshah@bugbare.co.uk", "badpassword"));
        assertNull(User.authenticate("tom@bugbare.com", "password"));
    }

    @Test
    public void findProjectsInvolving() {
        new User("rshah@bugbare.co.uk", "Rishi", "password").save();
        new User("amehay@bugbare.co.uk", "Anita", "secret").save();

        Project.create("Play 2", "play", "rshah@bugbare.co.uk");
        Project.create("Play 1", "play", "amehay@bugbare.co.uk");

        List<Project> results = Project.findInvolving("rshah@bugbare.co.uk");
        assertEquals(1, results.size());
        assertEquals("Play 2", results.get(0).name);
    }

    @Test
    public void findTodoTasksInvolving() {
        User rishi = new User("rshah@bugbare.co.uk", "Rishi", "password");
        rishi.save();

        Project project = Project.create("Play 2", "play", "rshah@bugbare.co.uk");
        Task t1 = new Task();
        t1.title = "Write tutorial";
        t1.assignedTo = rishi;
        t1.done = true;
        t1.save();

        Task t2 = new Task();
        t2.title = "Release next version";
        t2.project = project;
        t2.save();

        List<Task> results = Task.findTodoInvolving("rshah@bugbare.co.uk");
        assertEquals(1, results.size());
        assertEquals("Release next version", results.get(0).title);
    }

    @Test
    public void fullTest() {
        Ebean.save((List) Yaml.load("test-data.yml"));

        // Count things
        assertEquals(3, User.find.findRowCount());
        assertEquals(7, Project.find.findRowCount());
        assertEquals(5, Task.find.findRowCount());

        // Try to authenticate as users
        assertNotNull(User.authenticate("rshah@bugbare.co.uk", "password"));
        assertNotNull(User.authenticate("amehay@bugbare.co.uk", "secret"));
        assertNull(User.authenticate("jeff@example.com", "badpassword"));
        assertNull(User.authenticate("tom@example.com", "secret"));

        // Find all Rishi's projects
        List<Project> rishisProjects = Project.findInvolving("rshah@bugbare.co.uk");
        assertEquals(5, rishisProjects.size());

        // Find all Rishi's todo tasks
        List<Task> rishisTasks = Task.findTodoInvolving("rshah@bugbare.co.uk");
        assertEquals(4, rishisTasks.size());
    }
}
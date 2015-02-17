package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.repository.EntityDataRepository
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*

class EntityDataServiceImplTest {

    private EntityDataServiceImpl service
    private EntityDataRepository repository
    private SecurityService securityService
    private Project project

    @Before
    void 'Setup'() {
        repository = mock(EntityDataRepository)
        securityService = mock(SecurityService)
        service = new EntityDataServiceImpl(repository, securityService)
        project = Project.of(NameDescription.nd("P", "Project")).withId(ID.of(1))
    }

    @Test
    void 'Getting a false boolean'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.of("false"));
        assert !service.retrieveBoolean(project, "Test").get()
    }

    @Test
    void 'Getting a true boolean'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.of("true"));
        assert service.retrieveBoolean(project, "Test").get()
    }

    @Test
    void 'Not getting a boolean'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.empty());
        assert !service.retrieveBoolean(project, "Test").present
    }

    @Test
    void 'Getting an integer'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.of("10"));
        assert service.retrieveInteger(project, "Test").get() == 10
    }

    @Test
    void 'Not getting an integer'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.empty());
        assert !service.retrieveInteger(project, "Test").present
    }

    @Test
    void 'Getting a string'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.of("Value"));
        assert service.retrieve(project, "Test").get() == "Value"
    }

    @Test
    void 'Not getting a string'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.empty());
        assert !service.retrieve(project, "Test").present
    }

    @Test
    void 'Getting a JSON'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.of("""{"name":"Name","value":"Value"}"""));
        def json = JsonUtils.toMap(service.retrieveJson(project, "Test").get())
        assert json.name == "Name"
        assert json.value == "Value"
    }

    @Test
    void 'Not getting a JSON'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.empty());
        assert !service.retrieveJson(project, "Test").present
    }

    @Test
    void 'Getting an Object'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.of("""{"name":"Name","value":"Value"}"""));
        NameValue o = service.retrieve(project, "Test", NameValue).get()
        assert o.name == "Name"
        assert o.value == "Value"
    }

    @Test
    void 'Not getting an Object'() {
        when(repository.retrieve(project, "Test")).thenReturn(Optional.empty());
        assert !service.retrieve(project, "Test", NameValue).present
    }

    @Test
    void 'Store a boolean'() {
        service.store(project, "Test", true)
        verify(repository).store(project, "Test", "true")
    }

    @Test
    void 'Store an integer'() {
        service.store(project, "Test", 10)
        verify(repository).store(project, "Test", "10")
    }

    @Test
    void 'Store a string'() {
        service.store(project, "Test", "Value")
        verify(repository).store(project, "Test", "Value")
    }

    @Test
    void 'Store an object'() {
        service.store(project, "Test", new NameValue("Name", "Value"))
        verify(repository).store(project, "Test", """{"name":"Name","value":"Value"}""")
    }

    @Test
    void 'Deleting an object'() {
        service.delete(project, "Test")
        verify(repository).delete(project, "Test")
    }

}

package ru.mobiledimension.test.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mobiledimension.test.domain.Person;

import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository repository;

    @Test
    public void isDocumentRegistered() {
        // given
        Person alex = new Person();
        alex.setFirstName("Alex");
        alex.setLastName("Bloduin");
        alex.setDocumentNumber("Dock");
        entityManager.persist(alex);
        entityManager.flush();

        // when
        boolean isDocumentRegisteredTrue = repository.isDocumentRegistered(alex.getDocumentNumber());
        boolean isDocumentRegisteredFalse = repository.isDocumentRegistered("123");

        // then
        assertTrue(isDocumentRegisteredTrue);
        assertFalse(isDocumentRegisteredFalse);
    }

    @Test
    public void findWithFriends() {
        // given
        Person alex = new Person();
        alex.setFirstName("Alex");
        alex.setLastName("Bloduin");
        alex.setDocumentNumber("Dock");

        Person alexFriend = new Person();
        alexFriend.setFirstName("Maria");
        alexFriend.setLastName("Antuannetta");
        alexFriend.setDocumentNumber("Dock-2");

        alex.setFriends(Collections.singletonList(alexFriend));
        alexFriend = entityManager.persist(alexFriend);
        alex = entityManager.persist(alex);
        entityManager.flush();

        // when
        Person hasFriends = repository.findWithFriends(alex.getId());
        Person hasNoFriends = repository.findWithFriends(alexFriend.getId());

        // then
        assertNotNull(hasFriends.getFriends());
        assertNull(hasNoFriends.getFriends());
    }


}
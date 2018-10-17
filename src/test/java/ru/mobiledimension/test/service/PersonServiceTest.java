package ru.mobiledimension.test.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mobiledimension.test.domain.Person;
import ru.mobiledimension.test.dto.PersonDTO;
import ru.mobiledimension.test.exception.AlreadyHaveThisFriend;
import ru.mobiledimension.test.exception.DocumentAlreadyRegisteredException;
import ru.mobiledimension.test.exception.PersonNotFoundException;
import ru.mobiledimension.test.mapper.PersonMapper;
import ru.mobiledimension.test.mapper.PersonMapperImpl;
import ru.mobiledimension.test.repository.PersonRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {

    @Autowired
    private PersonService service;

    @MockBean
    private PersonRepository repository;

    private PersonMapper personMapper;

    @Before
    public void setUp() {
        personMapper = new PersonMapperImpl();
    }

    @Test
    public void createPerson_withUniqDocument() {
        //given
        PersonDTO dto = new PersonDTO();
        dto.setFirstName("Alex");
        dto.setLastName("Bloduin");
        dto.setDocumentNumber("Dock");

        Person person = personMapper.toEntity(dto);
        person.setId(1);

        //when
        when(repository.isDocumentRegistered(dto.getDocumentNumber())).thenReturn(false);
        when(repository.save(personMapper.toEntity(dto))).thenReturn(person);

        //than
        assertEquals(new Integer(1), service.createPerson(dto));
    }

    @Test(expected = DocumentAlreadyRegisteredException.class)
    public void createPerson_withNonUniqDocument() {
        //given
        PersonDTO dto = new PersonDTO();
        dto.setFirstName("Alex");
        dto.setLastName("Bloduin");
        dto.setDocumentNumber("Dock");

        Person person = personMapper.toEntity(dto);
        person.setId(1);

        //when
        when(repository.isDocumentRegistered(dto.getDocumentNumber())).thenReturn(true);
        when(repository.save(personMapper.toEntity(dto))).thenReturn(person);

        //than
        assertEquals(new Integer(1), service.createPerson(dto));
    }

    @Test(expected = AlreadyHaveThisFriend.class)
    public void addFriend_alreadyHaveFriend() {
        //given
        PersonDTO dto = new PersonDTO();
        dto.setFirstName("Alex");
        dto.setLastName("Bloduin");
        dto.setDocumentNumber("Dock");

        Person person = personMapper.toEntity(dto);
        person.setId(1);
        Person friend = personMapper.toEntity(dto);
        friend.setId(2);
        person.setFriends(Collections.singletonList(friend));

        //when
        when(repository.findById(1)).thenReturn(Optional.of(person));
        //than
        service.addFriend(1, 2);
    }

    @Test(expected = PersonNotFoundException.class)
    public void addFriend_noSuchPerson() {
        //given
        //when
        when(repository.findById(1)).thenReturn(Optional.empty());
        //than
        service.addFriend(1, 2);
    }
}
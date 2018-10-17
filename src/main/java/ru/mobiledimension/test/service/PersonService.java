package ru.mobiledimension.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mobiledimension.test.domain.Person;
import ru.mobiledimension.test.dto.PersonDTO;
import ru.mobiledimension.test.dto.PersonSmallDto;
import ru.mobiledimension.test.exception.AlreadyHaveThisFriend;
import ru.mobiledimension.test.exception.DocumentAlreadyRegisteredException;
import ru.mobiledimension.test.exception.PersonNotFoundException;
import ru.mobiledimension.test.mapper.PersonMapper;
import ru.mobiledimension.test.repository.PersonRepository;

import java.util.List;
import java.util.Objects;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Autowired
    public PersonService(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    public Integer createPerson(PersonDTO personDTO) {
        if (!personRepository.isDocumentRegistered(personDTO.getDocumentNumber()))
            return personRepository.save(personMapper.toEntity(personDTO)).getId();
        else
            throw new DocumentAlreadyRegisteredException(personDTO.getDocumentNumber());
    }

    @Transactional
    public void updatePerson(Integer id, PersonDTO dto) {
        Person person = personRepository.getOne(id);
        if (person == null) {
            throw new PersonNotFoundException(id);
        }

        personMapper.updateEntity(person, dto);
        personRepository.save(person);
    }

    public List<PersonSmallDto> getPersons() {
        List<Person> all = personRepository.findAll();
        return personMapper.toSmallDto(all);
    }

    public void delete(Integer id) {
        personRepository.delete(personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id)));
    }

    public PersonDTO getPerson(Integer id) {
        return personMapper.toFullDto(personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id)));
    }

    public List<PersonSmallDto> getFriends(Integer id) {
        Person personWithFriends = personRepository.findWithFriends(id);
        return personMapper.toSmallDto(personWithFriends.getFriends());
    }

    @Transactional
    public void deleteFriend(Integer id, Integer friendId) {
        Person person = personRepository.findWithFriends(id);
        person.getFriends().removeIf(p -> p.getId().equals(friendId));
        personRepository.save(person);
    }

    @Transactional
    public void addFriend(Integer id, Integer friendId) {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        if (person.getFriends().stream().map(Person::getId).filter(f -> Objects.equals(f, friendId)).toArray().length > 0)
            throw new AlreadyHaveThisFriend(friendId);
        else
            person.getFriends().add(personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(friendId)));
    }
}

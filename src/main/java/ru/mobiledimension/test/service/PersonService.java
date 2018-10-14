package ru.mobiledimension.test.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mobiledimension.test.domain.Person;
import ru.mobiledimension.test.dto.PersonDTO;
import ru.mobiledimension.test.dto.PersonSmallDto;
import ru.mobiledimension.test.exception.DocumentAlreadyRegisteredException;
import ru.mobiledimension.test.exception.PersonNotFoundException;
import ru.mobiledimension.test.mapper.PersonMapper;
import ru.mobiledimension.test.repository.PersonRepository;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    @Transactional
    public Integer createPerson(PersonDTO personDTO) {
        if (personRepository.isDocumentRegistered(personDTO.getDocumentNumber())) {
            throw new DocumentAlreadyRegisteredException(personDTO.getDocumentNumber());
        }

        Person person = personMapper.toEntity(personDTO);
        Person saved = personRepository.save(person);
        return saved.getId();
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
        personRepository.deleteById(id);
    }

    public PersonDTO getPerson(Integer id) {
        Person person = personRepository.getOne(id);
        if (person == null) {
            throw new PersonNotFoundException(id);
        }

        return personMapper.toFullDto(person);
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
        Person person = personRepository.findWithFriends(id);
        Person newFriend = personRepository.getOne(friendId);
        if (!person.getFriends().contains(newFriend)) {
            person.getFriends().add(newFriend);
        }
        personRepository.save(person);
    }
}

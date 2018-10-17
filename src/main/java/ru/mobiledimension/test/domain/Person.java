package ru.mobiledimension.test.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

//todo сделать этот класс сущностью, которая маппится на таблицу person
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birthday;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "person_friend",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Person> friends;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(documentNumber, person.documentNumber) &&
                Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(birthday, person.birthday) &&
                Objects.equals(friends, person.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documentNumber, firstName, lastName, birthday, friends);
    }
}

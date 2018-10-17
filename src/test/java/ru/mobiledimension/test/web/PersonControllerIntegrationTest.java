package ru.mobiledimension.test.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.mobiledimension.test.domain.Person;
import ru.mobiledimension.test.dto.PersonDTO;
import ru.mobiledimension.test.dto.PersonSmallDto;
import ru.mobiledimension.test.service.PersonService;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService service;

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void createPersonIntegrationTest() throws Exception {
        //given
        PersonDTO dto = new PersonDTO();
        dto.setFirstName("Alex");
        dto.setLastName("Bloduin");
        dto.setDocumentNumber("Dock");

        //when
        when(service.createPerson(dto)).thenReturn(1);

        //than
        this.mockMvc.perform(post("/person")
                .contentType("application/json; charset=UTF-8")
                .content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is(201))
                .andExpect(redirectedUrl("person/1"));
    }

    @Test
    public void addFriendIntegrationTest() throws Exception {
        //given
        List<PersonSmallDto> dtoList = new ArrayList<>();

        PersonSmallDto dto1 = new PersonSmallDto();
        dto1.setFirstName("Галя");
        dto1.setLastName("Петрова");
        dto1.setId(2);
        PersonSmallDto dto2 = new PersonSmallDto();
        dto2.setFirstName("Петя");
        dto2.setLastName("Иванов");
        dto2.setId(3);
        PersonSmallDto dto3 = new PersonSmallDto();
        dto3.setFirstName("Лариса");
        dto3.setLastName("Константин");
        dto3.setId(4);

        dtoList.add(dto1);
        dtoList.add(dto2);
        dtoList.add(dto3);

        //when
        when(service.getFriends(1)).thenReturn(dtoList);

        //than
        this.mockMvc.perform(get("/person/1/friend")
                .contentType("application/json; charset=UTF-8"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(dtoList)));
    }

    @Test
    public void getPersonsIntegrationTest() throws Exception {
//given
        List<PersonSmallDto> dtoList = new ArrayList<>();

        PersonSmallDto dto1 = new PersonSmallDto();
        dto1.setFirstName("Галя");
        dto1.setLastName("Петрова");
        dto1.setId(2);
        PersonSmallDto dto2 = new PersonSmallDto();
        dto2.setFirstName("Петя");
        dto2.setLastName("Иванов");
        dto2.setId(3);
        PersonSmallDto dto3 = new PersonSmallDto();
        dto3.setFirstName("Лариса");
        dto3.setLastName("Константин");
        dto3.setId(4);

        dtoList.add(dto1);
        dtoList.add(dto2);
        dtoList.add(dto3);

        //when
        when(service.getPersons()).thenReturn(dtoList);

        //than
        this.mockMvc.perform(get("/person")
                .contentType("application/json; charset=UTF-8"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(dtoList)));
    }
}
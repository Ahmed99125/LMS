package AdvSe.LMS;

import AdvSe.LMS.courses.controllers.LessonsController;
import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.services.LessonsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5 tests
class LessonsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LessonsService lessonsService;

    @InjectMocks
    private LessonsController lessonsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lessonsController).build();
    }

    @Test
    public void getAllLessonsByCourseIdTest() throws Exception {
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);
        lesson1.setName("Lesson 1");

        Lesson lesson2 = new Lesson();
        lesson2.setId(2);
        lesson2.setName("Lesson 2");

        List<Lesson> lessons = Arrays.asList(lesson1, lesson2);
        Mockito.when(lessonsService.getLessonsByCourseId(anyInt())).thenReturn(lessons);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/lessons")
                        .param("courseId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Lesson 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Lesson 2"));
    }

    @Test
    void getLessonByIdTest() throws Exception {
        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setName("Test Lesson");

        // Mock the behavior for getLessonById
        when(lessonsService.getLessonById(anyInt())).thenReturn(lesson);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Lesson"));
    }
}

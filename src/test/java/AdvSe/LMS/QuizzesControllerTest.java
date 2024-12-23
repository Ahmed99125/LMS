package AdvSe.LMS;

import AdvSe.LMS.courses.controllers.QuizzesController;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.courses.services.QuizzesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuizzesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizzesService quizzesService;

    @InjectMocks
    private QuizzesController quizzesController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizzesController).build();
    }

    @Test
    void getAllQuizzesTest() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1);
        quiz1.setName("Quiz 1");

        Quiz quiz2 = new Quiz();
        quiz2.setId(2);
        quiz2.setName("Quiz 2");

        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);

        when(quizzesService.getQuestionsByCourseId(anyInt())).thenReturn(quizzes);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/quizzes")
                        .param("courseId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Quiz 1"))
                .andExpect(jsonPath("$[1].name").value("Quiz 2"));
    }

    @Test
    void getQuizByIdTest() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setId(1);
        quiz.setName("Test Quiz");

        when(quizzesService.getQuizById(anyInt())).thenReturn(quiz);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Quiz"));
    }
}

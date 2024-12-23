package AdvSe.LMS;

import AdvSe.LMS.courses.controllers.AssignmentsController;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.repositories.AssignmentsRepository;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.courses.services.AssignmentsService;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class AssignmentsControllerTest {

    private MockMvc mockMvc;
    @Mock
    private AssignmentsService assignmentsService;

    @Mock
    private AssignmentsRepository assignmentsRepository;

    @Mock
    private CoursesRepository coursesRepository;

    @InjectMocks
    private AssignmentsController assignmentsController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(assignmentsController).build();
    }

    @Test
    public void getAllAssignmentsTest() throws Exception {
        Course course = new Course();
        Assignment assignment1 = new Assignment(1, "Assignment 1", course, null, null);
        Assignment assignment2 = new Assignment(2, "Assignment 2", course, null, null);
        List<Assignment> assignments = Arrays.asList(assignment1, assignment2);

        Mockito.when(assignmentsService.getAssignmentsByCourseId(1)).thenReturn(assignments);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/assignments")
                        .param("courseId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Assignment 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Assignment 2"));


    }

    @Test
    public void getAssignmentByIdTest() throws Exception {
        Assignment assignment = new Assignment(123123, "AssignmentOneTest", new Course(), new ArrayList<>(), new ArrayList<>());

        Mockito.when(assignmentsService.getAssignmentById(123123)).thenReturn(assignment);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/assignments/{assignmentId}", 123123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(123123))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("AssignmentOneTest"));
    }
}

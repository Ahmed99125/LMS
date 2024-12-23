package AdvSe.LMS;

import AdvSe.LMS.courses.controllers.CoursesController;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.services.CoursesService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class CoursesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CoursesService coursesService;

    @InjectMocks
    private CoursesController coursesController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(coursesController).build();
    }

    @Test
    public void getCourseByIdTest() throws Exception {
        Course course = new Course();
        course.setId(1);
        course.setName("Course 1");
        course.setDescription("Description of Course 1");

        Mockito.when(coursesService.getCourseById(1)).thenReturn(course);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Course 1"))
                .andExpect(jsonPath("$.description").value("Description of Course 1"));
    }

    @Test
    public void getAllCoursesTest() throws Exception {
        Course course1 = new Course();
        course1.setId(1);
        course1.setName("Course 1");
        Course course2 = new Course();
        course2.setId(2);
        course2.setName("Course 2");

        Mockito.when(coursesService.getAllCourses()).thenReturn(java.util.Arrays.asList(course1, course2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Course 1"))
                .andExpect(jsonPath("$[1].name").value("Course 2"));
    }
}

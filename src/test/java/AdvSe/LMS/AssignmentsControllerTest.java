package AdvSe.LMS;

import AdvSe.LMS.courses.controllers.AssignmentsController;
import AdvSe.LMS.courses.dtos.CreateAssignmentDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.repositories.AssignmentsRepository;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.courses.services.AssignmentsService;
import AdvSe.LMS.users.entities.Instructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class AssignmentsControllerTest {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

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

    @Test
    @WithMockUser(username = "instructor", roles = "INSTRUCTOR")  // Simulate the user as an instructor
    public void createAssignmentTest() throws Exception {
        // Create the mock user with the instructor role
        UserDetails user = User.builder()
                .username("instructor")
                .password("password")
                .roles("INSTRUCTOR")
                .build();

        // Create a mock course
        Course course = new Course();
        Mockito.when(coursesRepository.save(course)).thenReturn(course);

        // Create the CreateAssignmentDto
        CreateAssignmentDto createAssignmentDto = new CreateAssignmentDto();
        createAssignmentDto.setCourseId(course.getId());
        createAssignmentDto.setName("AssignmentOneTest");
        createAssignmentDto.setFiles(new ArrayList<MultipartFile>());  // Simulating no files for simplicity

        // Convert CreateAssignmentDto to JSON string using ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(createAssignmentDto);

        // Build the POST request
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/assignments")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        // Perform the request with the mock user and validate the response
        mockMvc.perform(mockRequest.with(user(user)))  // Simulate authentication
                .andExpect(status().isCreated())  // Expect HTTP status 201 (Created)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("AssignmentOneTest"))  // Validate name
                .andExpect(MockMvcResultMatchers.jsonPath("$.courseId").value(course.getId()));  // Validate courseId
    }
}

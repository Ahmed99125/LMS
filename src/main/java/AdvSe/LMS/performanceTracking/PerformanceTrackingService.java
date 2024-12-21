package AdvSe.LMS.performanceTracking;

import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.StudentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PerformanceTrackingService {
    private final StudentsRepository studentsRepository;
    private final CoursesRepository coursesRepository;

    public PerformanceTrackingService(StudentsRepository studentsRepository, CoursesRepository coursesRepository) {
        this.studentsRepository = studentsRepository;
        this.coursesRepository = coursesRepository;
    }

    public PerformanceTrackingDto getStudentPerformanceTracking(Integer courseId, String studentId) {
        PerformanceTrackingDto performanceTrackingDto = new PerformanceTrackingDto();
        // Initialize performanceTrackingDto fields
        performanceTrackingDto.setLessonsAttended(new ArrayList<>());
        performanceTrackingDto.setLessonsMissed(new ArrayList<>());
        performanceTrackingDto.setAssignmentsSubmitted(new ArrayList<>());
        performanceTrackingDto.setAssignmentsMissed(new ArrayList<>());
        performanceTrackingDto.setQuizzesTaken(new ArrayList<>());
        performanceTrackingDto.setQuizzesMissed(new ArrayList<>());

        Optional<Course> course = coursesRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Optional<Student> student = studentsRepository.findById(studentId);
        if (student.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Student not found");
        }


        // Set performanceTrackingDto fields
        performanceTrackingDto.setName(student.get().getName());

        // Set lessons attended and missed
        course.get().getLessons().forEach(lesson -> {
            if (lesson.getAttendance().contains(student.get())) {
                performanceTrackingDto.getLessonsAttended().add(lesson);
            } else {
                performanceTrackingDto.getLessonsMissed().add(lesson);
            }
        });
        // Set lessons attendance percentage
        performanceTrackingDto.setLessonsAttendancePercentage(
                course.get().getLessons().isEmpty() ? 0.0 :
                        100.0 * (double) performanceTrackingDto.getLessonsAttended().size() / course.get().getLessons().size()
        );

        // Set assignments submitted and missed
        course.get().getAssignments().forEach(assignment -> {
            if (assignment.getSubmissions().stream().anyMatch(submission -> submission.getStudent().equals(student.get()))) {
                performanceTrackingDto.getAssignmentsSubmitted().add(assignment);
            } else {
                performanceTrackingDto.getAssignmentsMissed().add(assignment);
            }
        });
        // Set assignments submission percentage
        performanceTrackingDto.setAssignmentsSubmissionPercentage(
                course.get().getAssignments().isEmpty() ? 0.0 :
                        100.0 * (double) performanceTrackingDto.getAssignmentsSubmitted().size() / course.get().getAssignments().size()
        );
        // Set assignments scored percentage
        AtomicReference<Double> totalScore = new AtomicReference<>(0.0);
        AtomicReference<Double> totalMaxScore = new AtomicReference<>(0.0);
        course.get().getAssignments().forEach(assignment -> {
            assignment.getSubmissions().stream()
                    .filter(submission -> submission.getStudent().equals(student.get()))
                    .findFirst()
                    .ifPresent(submission -> {
                        totalScore.updateAndGet(v -> v + submission.getScore());
                        totalMaxScore.updateAndGet(v -> v + 100); // Assuming max score is 100
                    });
        });
        performanceTrackingDto.setAssignmentsScoredPercentage(
                totalMaxScore.get() == 0 ? 0.0 : totalScore.get() / totalMaxScore.get() * 100
        );

        // Set quizzes taken and missed
        course.get().getQuizzes().forEach(quiz -> {
            if (quiz.getSubmissions().stream().anyMatch(submission -> submission.getStudent().equals(student.get()))) {
                performanceTrackingDto.getQuizzesTaken().add(quiz);
            } else {
                performanceTrackingDto.getQuizzesMissed().add(quiz);
            }
        });
        // Set quizzes taken percentage
        performanceTrackingDto.setQuizzesTakenPercentage(
                course.get().getQuizzes().isEmpty() ? 0.0 :
                        100.0 * (double) performanceTrackingDto.getQuizzesTaken().size() / course.get().getQuizzes().size()
        );
        // Set quizzes scored percentage
        totalScore.set(0.0);
        totalMaxScore.set(0.0);
        course.get().getQuizzes().forEach(quiz -> {
            quiz.getSubmissions().stream()
                    .filter(submission -> submission.getStudent().equals(student.get()))
                    .findFirst()
                    .ifPresent(submission -> {
                        totalScore.updateAndGet(v -> v + submission.getScore());
                        totalMaxScore.updateAndGet(v -> v + 100); // Assuming max score is 100
                    });
        });
        performanceTrackingDto.setQuizzesScoredPercentage(
                totalMaxScore.get() == 0 ? 0.0 : totalScore.get() / totalMaxScore.get() * 100
        );

        return performanceTrackingDto;
    }

    public List<PerformanceTrackingDto> getPerformanceTracking(Integer courseId) {
        List<PerformanceTrackingDto> performanceTrackingDtos = new ArrayList<>();
        Optional<Course> course = coursesRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }
        for (Student student : course.get().getStudents()) {
            performanceTrackingDtos.add(getStudentPerformanceTracking(courseId, student.getId()));
        }

        return performanceTrackingDtos;
    }

}

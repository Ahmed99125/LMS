package AdvSe.LMS.courses.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.courses.dtos.CreateLessonDto;
import AdvSe.LMS.courses.dtos.UpdateLessonDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.courses.repositories.LessonsRepository;
import AdvSe.LMS.notifications.NotificationsService;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.utils.HashingOTP;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

import static org.springframework.http.HttpStatus.*;

@Service
public class LessonsService {
    private final LessonsRepository lessonsRepository;
    private final CoursesRepository courseRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationsService notificationsService;
    private final Random random = new Random();

    LessonsService(LessonsRepository lessonsRepository, CoursesRepository courseRepository, CloudinaryService cloudinaryService, NotificationsService notificationsService) {
        this.lessonsRepository = lessonsRepository;
        this.courseRepository = courseRepository;
        this.cloudinaryService = cloudinaryService;
        this.notificationsService = notificationsService;
    }

    public Lesson getLessonById(Integer lessonId) {
        return lessonsRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lesson not found"));
    }

    public List<Lesson> getLessonsByCourseId(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        return course.getLessons();
    }

    public Lesson createLesson(CreateLessonDto createLessonDto) {
        Course course = courseRepository.findById(createLessonDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(createLessonDto.getInstructorId()))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setName(createLessonDto.getName());
        for (MultipartFile file : createLessonDto.getFiles()) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "lessons");
            lesson.addLessonFile(cloudinaryFile);
        }

        // Send notification to all students in the course

        String title = lesson.getCourse().getName() + ": " + "New lesson added";
        String message = "A new lesson (" + lesson.getName() + ") was added to " + course.getName() + " course.";
        notificationsService.sendNotifications(lesson.getCourse().getStudents(), title, message);

        return lessonsRepository.save(lesson);
    }

    public Lesson updateLesson(UpdateLessonDto updateLessonDto) {
        Lesson lesson = getLessonById(updateLessonDto.getLessonId());

        if (!lesson.getCourse().getInstructor().getId().equals(updateLessonDto.getInstructorId()))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        if (updateLessonDto.getName() != null)
            lesson.setName(updateLessonDto.getName());

        if (updateLessonDto.getFiles() != null) {
            lesson.getLessonFiles().clear();
            for (MultipartFile file : updateLessonDto.getFiles()) {
                CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "lessons");
                lesson.addLessonFile(cloudinaryFile);
            }
        }

        // Send notification to all students in the course

        String title = lesson.getCourse().getName() + ": " + "Lesson update";
        String message = "Lesson (" + lesson.getName() + ") was updated in " + lesson.getCourse().getName() + " course.";
        notificationsService.sendNotifications(lesson.getCourse().getStudents(), title, message);

        return lessonsRepository.save(lesson);
    }

    public void deleteLesson(Integer lessonId, String instructorId) {
        Lesson lesson = getLessonById(lessonId);

        if (!lesson.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        lessonsRepository.delete(lesson);
    }

    public String generateOTP(Integer lessonId, String instructorId) {
        Lesson lesson = getLessonById(lessonId);

        if (!lesson.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        Integer otp = random.nextInt(100000, 500000);
        lesson.setOTP(otp);
        lessonsRepository.save(lesson);
        String message = String.format("The OTP for the lesson '%s' is", lesson.getName());
        String url = String.format("use URL: http://localhost:8080/api/lessons/%d/attend", lessonId);
        for (Student student : lesson.getCourse().getStudents()) {
            Integer studentOTP = HashingOTP.hashOTP(otp, student.getId());
            String otpMessage = String.format("%s %d - %s?otp=%s", message, studentOTP, url, studentOTP);
            notificationsService.sendNotification(student, "OTP", otpMessage);
        }
        return "OTP generated successfully";
    }

    public String attendLesson(Integer lessonId, Integer otp, String studentId) {
        Lesson lesson = getLessonById(lessonId);

        Student student = lesson.getCourse().getStudents().stream().filter(s -> s.getId().equals(studentId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "You are not enrolled in this course"));

        if (lesson.getAttendance().contains(student))
            throw new ResponseStatusException(BAD_REQUEST, "You have already attended this lesson");

        Integer studentOTP = HashingOTP.hashOTP(lesson.getOTP(), studentId);
        if (!studentOTP.equals(otp))
            throw new ResponseStatusException(FORBIDDEN, "Invalid OTP");

        lesson.addStudent(student);
        lessonsRepository.save(lesson);
        return "You have successfully attended the lesson";
    }

    public List<Student> getAttendance(Integer lessonId, String instructorId) {
        Lesson lesson = getLessonById(lessonId);

        if (!lesson.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        return lesson.getAttendance();
    }
}

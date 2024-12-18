package AdvSe.LMS.courses.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.courses.repositories.LessonsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class LessonsService {
    private final LessonsRepository lessonsRepository;
    private final CoursesRepository courseRepository;
    private final CloudinaryService cloudinaryService;

    LessonsService(LessonsRepository lessonsRepository, CoursesRepository courseRepository, CloudinaryService cloudinaryService) {
        this.lessonsRepository = lessonsRepository;
        this.courseRepository = courseRepository;
        this.cloudinaryService = cloudinaryService;
    }

    private Lesson getCheckedLessonById(Integer course_id, Integer lesson_id, String instructorId) {
        Lesson lesson = getLessonById(course_id, lesson_id);
        if (!lesson.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");
        return lesson;
    }

    public Lesson getLessonById(Integer course_id, Integer lesson_id) {
        if (!courseRepository.existsById(course_id)) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Lesson lesson = lessonsRepository.findById(lesson_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lesson not found"));

        if (!lesson.getCourse().getId().equals(course_id))
            throw new ResponseStatusException(NOT_FOUND, "Lesson not found");

        return lesson;
    }

    public List<Lesson> getLessonsByCourseId(Integer course_id) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        return course.getLessons();
    }

    public Lesson createLesson(Integer course_id, String instructorId, String name, List<MultipartFile> files) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setName(name);
        for (MultipartFile file : files) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "lessons");
            lesson.addLessonFile(cloudinaryFile);
        }
        return lessonsRepository.save(lesson);
    }

    public Lesson updateLesson(Integer course_id, String instructorId, Integer lesson_id, String name, List<MultipartFile> files) {
        Lesson lesson = getCheckedLessonById(course_id, lesson_id, instructorId);

        lesson.setName(name);
        lesson.removeAllLessonFiles();
        for (MultipartFile file : files) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "lessons");
            lesson.addLessonFile(cloudinaryFile);
        }

        return lessonsRepository.save(lesson);
    }

    public void deleteLesson(Integer course_id, String instructorId, Integer lesson_id) {
        Lesson lesson = getCheckedLessonById(course_id, lesson_id, instructorId);
        lessonsRepository.delete(lesson);
    }
}

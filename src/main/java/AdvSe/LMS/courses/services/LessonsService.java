package AdvSe.LMS.courses.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.courses.dtos.CreateLessonDto;
import AdvSe.LMS.courses.dtos.UpdateLessonDto;
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

        return lessonsRepository.save(lesson);
    }

    public void deleteLesson(Integer lessonId, String instructorId) {
        Lesson lesson = getLessonById(lessonId);

        if (!lesson.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        lessonsRepository.delete(lesson);
    }
}

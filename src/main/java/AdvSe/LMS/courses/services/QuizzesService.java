package AdvSe.LMS.courses.services;

import AdvSe.LMS.courses.dtos.GenerateQuizDto;
import AdvSe.LMS.courses.dtos.QuizDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.courses.repositories.QuizzesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Service
public class QuizzesService {

    private final QuizzesRepository quizzesRepository;
    private final CoursesRepository courseRepository;

    public QuizzesService(QuizzesRepository quizzesRepository, CoursesRepository courseRepository) {
        this.quizzesRepository = quizzesRepository;
        this.courseRepository = courseRepository;
    }

    public List<Quiz> getQuestionsByCourseId(Integer courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        return course.getQuizzes();
    }

    public Quiz getQuizById(Integer quizId) {
        return quizzesRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz not found"));
    }

    public Quiz createQuiz(QuizDto quizDto) {
        Course course = courseRepository.findById(quizDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(quizDto.getInstructorId())) {
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");
        }

        Set<Integer> questionIds = quizDto.getQuestionIds();
        List<Question> questions = course.getQuestions().stream().filter(question -> questionIds.contains(question.getId())).toList();
        if (questions.size() != questionIds.size()) {
            throw new ResponseStatusException(BAD_REQUEST, "Some questions not found");
        }
        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setName(quizDto.getName());
        for (Question question : questions) {
            quiz.addQuestion(question);
        }
        return quizzesRepository.save(quiz);
    }

    public Quiz generateQuiz(GenerateQuizDto generateQuizDto) {
        Course course = courseRepository.findById(generateQuizDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(generateQuizDto.getInstructorId())) {
            throw new ResponseStatusException(BAD_REQUEST, "This course does not belong to you");
        }

        List<Question> questions = course.getQuestions();
        if (generateQuizDto.getNumberOfQuestions() > questions.size()) {
            throw new ResponseStatusException(BAD_REQUEST, "Not enough questions");
        }
        Collections.shuffle(questions);
        questions = questions.subList(0, generateQuizDto.getNumberOfQuestions());
        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setName(generateQuizDto.getName());
        for (Question question : questions) {
            quiz.addQuestion(question);
        }
        return quizzesRepository.save(quiz);
    }

    public void deleteQuiz(Integer quizId, String instructorId) {
        Quiz quiz = getQuizById(quizId);

        if (!quiz.getCourse().getInstructor().getId().equals(instructorId)) {
            throw new ResponseStatusException(BAD_REQUEST, "This quiz does not belong to you");
        }

        quizzesRepository.delete(quiz);
    }
}

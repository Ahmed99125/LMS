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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class QuizzesService {

    private final QuizzesRepository quizzesRepository;
    private final CoursesRepository courseRepository;
    private final QuestionsService questionsService;

    public QuizzesService(QuizzesRepository quizzesRepository, CoursesRepository courseRepository, QuestionsService questionsService) {
        this.quizzesRepository = quizzesRepository;
        this.courseRepository = courseRepository;
        this.questionsService = questionsService;
    }

    public List<Quiz> getQuestionsByCourseId(Integer course_id) {
        Course course = courseRepository.findById(course_id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        return course.getQuizzes();
    }

    public Quiz getQuizById(Integer course_id, Integer quiz_id) {
        if (!courseRepository.existsById(course_id)) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Quiz quiz = quizzesRepository.findById(quiz_id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz not found"));

        if (quiz.getCourse().getId().equals(course_id)) throw new ResponseStatusException(NOT_FOUND, "Quiz not found");

        return quiz;
    }

    public Quiz createQuiz(Integer courseId, QuizDto quizDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        Set<Integer> questionIds = quizDto.getQuestionIds();
        List<Question> questions = course.getQuestions().stream().filter(question -> questionIds.contains(question.getId())).toList();
        if (questions.size() != questionIds.size()) {
            throw new ResponseStatusException(NOT_FOUND, "Question not found");
        }
        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setName(quizDto.getName());
        for (Question question : questions) {
            quiz.addQuestion(question);
        }
        return quizzesRepository.save(quiz);
    }

    public Quiz generateQuiz(Integer courseId, GenerateQuizDto generateQuizDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

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
}

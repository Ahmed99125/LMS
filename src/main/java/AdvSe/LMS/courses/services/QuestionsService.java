package AdvSe.LMS.courses.services;

import AdvSe.LMS.courses.dtos.CreateQuestionDto;
import AdvSe.LMS.courses.dtos.UpdateQuestionDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.courses.repositories.QuestionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class QuestionsService {
    private final QuestionsRepository questionsRepository;
    private final CoursesRepository courseRepository;

    QuestionsService(QuestionsRepository questionsRepository, CoursesRepository courseRepository) {
        this.questionsRepository = questionsRepository;
        this.courseRepository = courseRepository;
    }

    public Question getQuestionById(Integer course_id, Integer question_id, String instructorId) {
        if (!courseRepository.existsById(course_id)) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Question question = questionsRepository.findById(question_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Question not found"));

        if (!question.getCourse().getId().equals(course_id))
            throw new ResponseStatusException(NOT_FOUND, "Question not found");

        if (!question.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        return question;
    }

    public List<Question> getQuestionsByCourseId(Integer course_id, String instructorId) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        return course.getQuestions();
    }

    public Question createQuestion(Integer course_id, CreateQuestionDto createQuestionDto, String instructorId) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        Question question = new Question();
        question.setCourse(course);
        question.setType(createQuestionDto.getType());
        question.setQuestion(createQuestionDto.getQuestion());
        question.setAnswer(createQuestionDto.getAnswer());

        return questionsRepository.save(question);
    }

    public Question updateQuestion(Integer course_id, Integer Question_id, UpdateQuestionDto updateQuestionDto, String instructorId) {
        Question question = getQuestionById(course_id, Question_id, instructorId);

        if (updateQuestionDto.getType() != null)
            question.setType(updateQuestionDto.getType());
        if (updateQuestionDto.getQuestion() != null)
            question.setQuestion(updateQuestionDto.getQuestion());
        if (updateQuestionDto.getAnswer() != null)
            question.setAnswer(updateQuestionDto.getAnswer());
        return questionsRepository.save(question);
    }

    public void deleteQuestion(Integer course_id, Integer question_id, String instructorId) {
        Question question = getQuestionById(course_id, question_id, instructorId);
        questionsRepository.delete(question);
    }
}

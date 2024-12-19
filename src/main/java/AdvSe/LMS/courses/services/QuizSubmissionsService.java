package AdvSe.LMS.courses.services;

import AdvSe.LMS.courses.dtos.submissions.QuestionSubmissionDto;
import AdvSe.LMS.courses.dtos.submissions.QuizSubmissionDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.courses.entities.Questions.Submissions.QuestionAnswer;
import AdvSe.LMS.courses.entities.Questions.Submissions.QuizSubmission;
import AdvSe.LMS.courses.repositories.*;
import AdvSe.LMS.users.entities.Student;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class QuizSubmissionsService {
    private final QuizSubmissionsRepository quizSubmissionsRepository;
    private final CoursesRepository courseRepository;
    private final QuizzesRepository quizzesRepository;
    private final QuestionAnswersRepository questionAnswersRepository;

    public QuizSubmissionsService(QuizSubmissionsRepository quizSubmissionsRepository, CoursesRepository courseRepository, QuizzesRepository quizzesRepository, QuestionsRepository questionsRepository, QuestionAnswersRepository questionAnswersRepository) {
        this.quizSubmissionsRepository = quizSubmissionsRepository;
        this.courseRepository = courseRepository;
        this.quizzesRepository = quizzesRepository;
        this.questionAnswersRepository = questionAnswersRepository;
    }


    public List<QuizSubmission> getQuizSubmissions(Integer courseId, Integer quizId, String instructorId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Quiz quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz not found"));

        if (!quiz.getCourse().getId().equals(courseId)) {
            throw new ResponseStatusException(NOT_FOUND, "Quiz not found");
        }

        if (!quiz.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        return quiz.getSubmissions();
    }

    public QuizSubmission getQuizSubmission(Integer courseId, Integer quizId, String studentId, String instructorId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Quiz quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz not found"));

        if (!quiz.getCourse().getId().equals(courseId)) {
            throw new ResponseStatusException(NOT_FOUND, "Quiz not found");
        }

        if (!quiz.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        return quizSubmissionsRepository.findByQuizIdAndStudentId(quizId, studentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz submission not found"));

    }

    public QuizSubmission getMyQuizSubmission(Integer courseId, Integer quizId, String studentId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Quiz quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz not found"));

        if (!quiz.getCourse().getId().equals(courseId)) {
            throw new ResponseStatusException(NOT_FOUND, "Quiz not found");
        }

        return quizSubmissionsRepository.findByQuizIdAndStudentId(quizId, studentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz submission not found"));
    }

    public QuizSubmission submitQuiz(QuizSubmissionDto quizSubmissionDto) {
        Course course = courseRepository.findById(quizSubmissionDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        Student student = course.getStudents().stream().filter(s -> s.getId().equals(quizSubmissionDto.getStudentId())).findFirst()
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "You are not enrolled in this course"));

        Quiz quiz = quizzesRepository.findById(quizSubmissionDto.getQuizId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz not found"));

        List<Integer> questionIds = quizSubmissionDto.getAnswers().stream().map(QuestionSubmissionDto::getQuestionId).toList();
        List<Question> questions = quiz.getQuestions().stream().filter(question -> questionIds.contains(question.getId())).toList();
        if (questions.size() != questionIds.size()) {
            throw new ResponseStatusException(NOT_FOUND, "Question not found");
        }

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student);
        quizSubmission = quizSubmissionsRepository.save(quizSubmission);
        int correct = 0;
        int numberOfQuestions = quiz.getQuestions().size();
        List<QuestionSubmissionDto> answers = quizSubmissionDto.getAnswers();
        for (Question question : quiz.getQuestions()) {
            QuestionSubmissionDto questionSubmissionDto = answers.stream().filter(q -> q.getQuestionId().equals(question.getId())).findFirst().orElse(null);
            QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setQuestion(question);
            if (questionSubmissionDto != null) {
                questionAnswer.setUserAnswer(questionSubmissionDto.getAnswer());
                if (question.getAnswer().equals(questionSubmissionDto.getAnswer())) {
                    correct++;
                }
            }
            questionAnswer.setQuizSubmission(quizSubmission);
            questionAnswer.setCorrectAnswer(question.getAnswer());
            quizSubmission.addAnswer(questionAnswersRepository.save(questionAnswer));
        }
        Double score = (correct * 100) / (double) numberOfQuestions;
        quizSubmission.setScore(score);
        return quizSubmissionsRepository.save(quizSubmission);
    }
}
package AdvSe.LMS.courses.services;

import AdvSe.LMS.courses.dtos.submissions.QuestionSubmissionDto;
import AdvSe.LMS.courses.dtos.submissions.QuizSubmissionDto;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.courses.entities.Questions.Submissions.QuestionAnswer;
import AdvSe.LMS.courses.entities.Questions.Submissions.QuizSubmission;
import AdvSe.LMS.courses.repositories.QuestionAnswersRepository;
import AdvSe.LMS.courses.repositories.QuestionsRepository;
import AdvSe.LMS.courses.repositories.QuizSubmissionsRepository;
import AdvSe.LMS.courses.repositories.QuizzesRepository;
import AdvSe.LMS.users.entities.Student;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class QuizSubmissionsService {
    private final QuizSubmissionsRepository quizSubmissionsRepository;
    private final QuizzesRepository quizzesRepository;
    private final QuestionAnswersRepository questionAnswersRepository;

    public QuizSubmissionsService(QuizSubmissionsRepository quizSubmissionsRepository, QuizzesRepository quizzesRepository, QuestionsRepository questionsRepository, QuestionAnswersRepository questionAnswersRepository) {
        this.quizSubmissionsRepository = quizSubmissionsRepository;
        this.quizzesRepository = quizzesRepository;
        this.questionAnswersRepository = questionAnswersRepository;
    }

    private Quiz getQuizOrThrow(Integer quizId) {
        return quizzesRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz not found"));
    }

    public List<QuizSubmission> getQuizSubmissions(Integer quizId, String instructorId) {
        Quiz quiz = getQuizOrThrow(quizId);

        if (!quiz.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        return quiz.getSubmissions();
    }

    public QuizSubmission getQuizSubmissionByStudentId(Integer quizId, String studentId, String instructorId) {
        Quiz quiz = getQuizOrThrow(quizId);

        if (!quiz.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        return getMyQuizSubmission(quizId, studentId);
    }

    public QuizSubmission getMyQuizSubmission(Integer quizId, String studentId) {
        return quizSubmissionsRepository.findByQuizIdAndStudentId(quizId, studentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Quiz submission not found"));
    }

    public QuizSubmission submitQuiz(QuizSubmissionDto quizSubmissionDto) {
        Quiz quiz = getQuizOrThrow(quizSubmissionDto.getQuizId());

        Student student = quiz.getCourse().getStudents().stream().filter(s -> s.getId().equals(quizSubmissionDto.getStudentId())).findFirst()
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "You are not enrolled in this course"));

        if (quizSubmissionsRepository.existsByQuizIdAndStudentId(quiz.getId(), student.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "You have already submitted this quiz");
        }

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
package AdvSe.LMS.performanceTracking;

import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceTrackingDto {
    private String name;
    private List<Lesson> lessonsAttended;
    private List<Lesson> lessonsMissed;
    private Double lessonsAttendancePercentage;
    private List<Assignment> assignmentsSubmitted;
    private List<Assignment> assignmentsMissed;
    private Double assignmentsSubmissionPercentage;
    private Double assignmentsScoredPercentage;
    private List<Quiz> quizzesTaken;
    private List<Quiz> quizzesMissed;
    private Double quizzesTakenPercentage;
    private Double quizzesScoredPercentage;
}

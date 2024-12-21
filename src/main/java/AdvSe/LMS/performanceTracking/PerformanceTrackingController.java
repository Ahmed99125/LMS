package AdvSe.LMS.performanceTracking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import AdvSe.LMS.performanceTracking.PerformanceTrackingService;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/performance_tracking")
public class PerformanceTrackingController {
    private final PerformanceTrackingService performanceTrackingService;

    PerformanceTrackingController(PerformanceTrackingService performanceTrackingService) {
        this.performanceTrackingService = performanceTrackingService;
    }

    @GetMapping("")
    List<PerformanceTrackingDto> getPerformanceTracking(
            @PathVariable("courseId") Integer courseId
    ) {
        return performanceTrackingService.getPerformanceTracking(courseId);
    }

    @GetMapping("/{studentId}")
    PerformanceTrackingDto getStudentPerformanceTracking(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("studentId") String studentId
    ) {
        return performanceTrackingService.getStudentPerformanceTracking(courseId, studentId);
    }
}

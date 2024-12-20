package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.CreateAssignmentDto;
import AdvSe.LMS.courses.dtos.UpdateAssignmentDto;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.services.AssignmentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentsController {
    private final AssignmentsService assignmentsService;

    AssignmentsController(AssignmentsService assignmentsService) {
        this.assignmentsService = assignmentsService;
    }

    @GetMapping("")
    List<Assignment> getAssignments(@RequestParam Integer courseId) {
        return assignmentsService.getAssignmentsByCourseId(courseId);
    }

    @GetMapping("/{assignmentId}")
    Assignment getAssignmentById(@PathVariable("assignmentId") Integer assignmentId) {
        return assignmentsService.getAssignmentById(assignmentId);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping(path = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    Assignment postAssignment(
            @ModelAttribute CreateAssignmentDto createAssignmentDto,
            @AuthenticationPrincipal User user
    ) {
        createAssignmentDto.setInstructorId(user.getUsername());
        return assignmentsService.createAssignment(createAssignmentDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping(path = "/{assignmentId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    Assignment updateAssignment(
            @ModelAttribute UpdateAssignmentDto updateAssignmentDto,
            @AuthenticationPrincipal User user
    ) {
        updateAssignmentDto.setInstructorId(user.getUsername());
        return assignmentsService.updateAssignment(updateAssignmentDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAssignment(
            @PathVariable("assignmentId") Integer assignmentId,
            @AuthenticationPrincipal User user
    ) {
        assignmentsService.deleteAssignment(assignmentId, user.getUsername());
    }
}

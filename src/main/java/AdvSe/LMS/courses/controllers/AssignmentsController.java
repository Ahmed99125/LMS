package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.services.AssignmentsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{course_id}/assignments")
public class AssignmentsController {
    private final AssignmentsService assignmentsService;

    AssignmentsController(AssignmentsService assignmentsService) {
        this.assignmentsService = assignmentsService;
    }

    @GetMapping("")
    List<Assignment> getAssignments(@PathVariable("course_id") Integer course_id) {
        return assignmentsService.getAssignmentsByCourseId(course_id);
    }

    @GetMapping("/{assignment_id}")
    Assignment getAssignmentById(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("assignment_id") Integer assignment_id) {
        return assignmentsService.getAssignmentById(course_id, assignment_id);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Assignment postAssignment(
            @PathVariable("course_id") Integer course_id,
            @RequestPart("name") String name,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return assignmentsService.createAssignment(course_id, user.getUsername(), name, files);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{assignment_id}")
    Assignment updateAssignment(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("assignment_id") Integer assignment_id,
            @RequestPart("name") String name,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return assignmentsService.updateAssignment(course_id, user.getUsername(), assignment_id, name, files);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{assignment_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAssignment(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("assignment_id") Integer assignment_id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        assignmentsService.deleteAssignment(course_id, user.getUsername(), assignment_id);
    }
}

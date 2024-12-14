package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.AssignmentsService;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.repositories.AssignmentsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/courses/{course_id}/assignments")
public class AssignmentsController {
    private final AssignmentsService assignmentsService;
    private final AssignmentsRepository assignmentsRepository;

    AssignmentsController(AssignmentsService assignmentsService, AssignmentsRepository assignmentsRepository) {
        this.assignmentsService = assignmentsService;
        this.assignmentsRepository = assignmentsRepository;
    }

    @GetMapping("")
    List<Assignment> getAssignments(@PathVariable("course_id") Integer course_id) {
        List<Assignment> result = assignmentsService.getAssignmentsByCourseId(course_id);
        if (result == null)
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        return result;
    }

    @GetMapping("/{assignment_id}")
    Assignment getAssignmentById(@PathVariable("assignment_id") Integer assignment_id) {
        Optional<Assignment> result = assignmentsRepository.findById(assignment_id);
        if (result.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Assignment not found");
        return result.get();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Assignment postAssignment(
            @PathVariable("course_id") Integer course_id,
            @RequestPart("data") String name,
            @RequestPart("files")List<MultipartFile> files) {
        return assignmentsService.createAssignment(course_id, name, files);
    }

    @PutMapping("/{assignment_id}")
    Assignment updateAssignment(
            @PathVariable("assignment_id") Integer assignment_id,
            @RequestPart("data") String name,
            @RequestPart("files")List<MultipartFile> files) {
        return assignmentsService.updateAssignment(assignment_id, name, files);
    }

    @DeleteMapping("/{assignment_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAssignment(@PathVariable("assignment_id") Integer assignment_id) {
        if (assignmentsRepository.findById(assignment_id).isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Assignment not found");
        assignmentsRepository.deleteById(assignment_id);
    }
}

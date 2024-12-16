package AdvSe.LMS.users.services;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class InstructorsService {

    private final UsersService usersService;
    private final InstructorsRepository instructorsRepository;

    public InstructorsService(UsersService usersService, InstructorsRepository instructorsRepository) {
        this.usersService = usersService;
        this.instructorsRepository = instructorsRepository;
    }

    public List<Instructor> getInstructors() {
        return instructorsRepository.findAll();
    }

    public Instructor createInstructor(CreateUserDto userDto) {
        Instructor instructor = new Instructor();
        usersService.setUserData(instructor, userDto);
        return instructorsRepository.save(instructor);
    }

    public Instructor updateInstructor(String instructorId, CreateUserDto userDto) {
        Instructor instructor = instructorsRepository.findById(instructorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Instructor not found"));
        usersService.updateUser(instructor, userDto);
        return instructorsRepository.save(instructor);
    }

    public Instructor getInstructorById(String instructorId) {
        return instructorsRepository.findById(instructorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Instructor not found"));
    }

    public void deleteInstructor(String instructorId) {
        if (!instructorsRepository.existsById(instructorId))
            throw new ResponseStatusException(NOT_FOUND, "Instructor not found");
        instructorsRepository.deleteById(instructorId);
    }
}

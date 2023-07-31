package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final List<InternshipFilter> filterList;

    public InternshipDto saveNewInternship(InternshipDto internshipDto) { //1 создать стажировку
        InternshipValidator.validateServiceSaveInternship(internshipDto);
        Internship internship = internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toDto(internship);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto, long id) { //2 обновить стажировку
        Internship oldInternship = internshipRepository.getById(id);
        InternshipValidator.validateServiceUpdateInternship(oldInternship, internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(getListOfInterns(internshipDto.getInternsId())); //50
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internsDoneTasks(internshipDto.getInternsId()); //60
            TeamRole role = TeamRole.DEVELOPER;
            for (TeamMember intern : interns) {
                intern.setRoles(List.of(role));
            }
            internshipRepository.deleteById(id);
        } else {
            return internshipMapper.toDto(internshipRepository.save(internship));
        }
        return null;
    }

    public List<TeamMember> getListOfInterns(List<Long> interns) { //2.1
        int sizeOfListInterns = interns.size();
        List<TeamMember> secondListOfInterns = new ArrayList<>(sizeOfListInterns);
        for (Long aLong : interns) {
            TeamMember intern = teamMemberRepository.findById(aLong);
            secondListOfInterns.add(intern);
        }
        return secondListOfInterns;
    }

    public List<TeamMember> internsDoneTasks(List<Long> interns) { //2.2
        int sizeOfListInterns = interns.size();
        List<TeamMember> secondListOfInterns = new ArrayList<>(sizeOfListInterns);
        for (Long aLong : interns) {
            TeamMember intern = teamMemberRepository.findById(aLong);
            if (checkTaskDone(intern)) { //72
                secondListOfInterns.add(intern);
            }
        }
        return secondListOfInterns;
    }

    public boolean checkTaskDone(TeamMember member) { //2.3
        List<Stage> stages = member.getStages();
        for (Stage stage : stages) {
            List<Task> tasks = stage.getTasks();
            for (Task task : tasks) {
                if (!task.getStatus().equals(TaskStatus.DONE)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<InternshipDto> findInternshipsByStatusWithFilter(long projectId, InternshipFilterDto filterDto) { //3 получить все стажировки по статусу
        List<InternshipDto> listOfInternship = getAllInternships();
        listOfInternship.removeIf(dto -> !dto.getProjectId().equals(projectId));
        filter(filterDto, listOfInternship);
        return listOfInternship;
    }

    public void filter(InternshipFilterDto filter, List<InternshipDto> dtoList) { //3.1
        filterList.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(dtoList, filter));
    }

    public List<InternshipDto> getAllInternships() { //4 получить все стажировки
        List<Internship> listOfInternship = internshipRepository.findAll();
        return listOfInternship.stream().map(internshipMapper::toDto).toList();
    }

    public InternshipDto findAllInternshipById(long id) { //5 получить стажировку по id
        return internshipMapper.toDto(internshipRepository.getById(id));
    }
}
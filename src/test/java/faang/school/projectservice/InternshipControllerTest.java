package faang.school.projectservice;

import faang.school.projectservice.controller.InternshipController;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {
    @Mock
    private InternshipService internshipService;

    @InjectMocks
    private InternshipController internshipController;

    @Test
    public void saveNewInternshipThrowsExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> internshipController.saveNewInternship(new Internship()));
    }
}

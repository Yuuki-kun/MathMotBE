package com.mot.mot.classService;

import com.mot.mot.model.dto.ClassDto;
import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.repository.ClassRepository;
import com.mot.mot.service.ClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassServiceTest {

    @InjectMocks
    //tim cac mock object va inject vao classService
    private ClassService classService;

    @Mock
    private ClassRepository classRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate() {
        // Given
        CreateClassRequest createClassRequest = new CreateClassRequest(
                new ClassDto(
                        0L,
                        "className",
                        1L,
                        true,
                        "classDesc",
                        new Date()
                ),
                1L
        );
        Class expected = new Class(
                0L,
                "className",
                1L,
                true,
                "classDesc",
                null,
                new Date()
        );

        Class toSaveClass = Class.builder()
                .className(createClassRequest.getClassDto().getClassName())
                .classDesc(createClassRequest.getClassDto().getClassDesc())
                .classGrade(createClassRequest.getClassDto().getClassGrade())
                .classStatus(createClassRequest.getClassDto().getClassStatus())
                .createdAt(createClassRequest.getClassDto().getCreatedAt())
                .teacher(Teacher.builder().teacherId(createClassRequest.getTeacherId()).build())
                .build();

        //mock repository.save
        Mockito.when(classRepository.save(Mockito.any(Class.class))).thenReturn(toSaveClass);

        // When

        Class result = classService.create(createClassRequest);

        // Then
        assertEquals(result.getClassName(), expected.getClassName());
        assertEquals(result.getClassGrade(), expected.getClassGrade());
        assertEquals(result.getClassStatus(), expected.getClassStatus());
        assertEquals(result.getClassDesc(), expected.getClassDesc());
// Xác minh rằng save chỉ được gọi đúng một lần
        Mockito.verify(classRepository, Mockito.times(1)).save(Mockito.any(Class.class));
    }

}

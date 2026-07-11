package com.example.minitasks.services;

import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.entities.Task;
import com.example.minitasks.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository repo;
    @InjectMocks TaskService service;

    @Test void create_savesWithDoneFalse() {
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        Task saved = new Task("X"); saved.setId(1L);
        when(repo.save(any(Task.class))).thenReturn(saved);

        Task result = service.create(new CreateTaskDTO("X"));

        verify(repo).save(captor.capture());
        Task toSave = captor.getValue();
        assertThat(toSave.getTitle()).isEqualTo("X");
        assertThat(toSave.isDone()).isFalse();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test void list_withNullDone_returnsAll() {
        given(repo.findAll()).willReturn(List.of(new Task("A")));
        assertThat(service.list(null)).hasSize(1);
    }

    @Test void toggle_flipsDoneOrThrows() {
        Task t = new Task("t"); t.setId(9L); t.setDone(false);
        given(repo.findById(9L)).willReturn(Optional.of(t));

        Task res = service.toggle(9L);
        assertThat(res.isDone()).isTrue();

        given(repo.findById(99L)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.toggle(99L));
    }

    @Test void delete_checksExistence() {
        given(repo.existsById(5L)).willReturn(true);
        service.delete(5L);
        verify(repo).deleteById(5L);

        given(repo.existsById(6L)).willReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.delete(6L));
    }
}

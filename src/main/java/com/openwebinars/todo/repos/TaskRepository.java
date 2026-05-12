package com.openwebinars.todo.repos;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.users.User;
import jakarta.persistence.EnumType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /* Buscar por autor - todas las tareas del usuario*/
    List<Task> findByAuthor(User author);

    /* Buscar por categoria */
    List<Task> findByCategoryAndAuthor(Category category, User author);
    List<Task> findByCategoryIdAndAuthor(Long id, User author);
    List<Task> findByCategoryId(Long id);

    /* Buscar por descripcion */
    List<Task> findByDescriptionContainingIgnoreCaseAndAuthor(String description, User author);
    /* Buscar por título */
    List<Task> findByTitleContainingIgnoreCaseAndAuthor(String title, User author);

    /* Buscar por completadas */
    List<Task> findByCompletedAndAuthor(boolean completed, User author);

    /* Buscar por fecha de creacion */
    List<Task> findByCreatedAtAndAuthor(LocalDateTime createdAt, User author);

    /* Buscar por fecha limite */
    List<Task> findByDeadlineLessThanEqualAndAuthor(LocalDate deadline, User author);

    /* Buscar por nombre de etiqueta y autor */
    List<Task> findByTags_NameIgnoreCaseAndAuthor(String tagName, User author);

    /* Buscar por prioridad */
    List<Task> findByPriorityAndAuthor(Task.Priority priority, User author);
}

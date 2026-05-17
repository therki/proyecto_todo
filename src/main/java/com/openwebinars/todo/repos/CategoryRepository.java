package com.openwebinars.todo.repos;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByTitle(String title);

}

package com.openwebinars.todo.service;

import com.openwebinars.todo.dto.EditCategoryCommand;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.repos.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TaskService taskService;
/* LISTAR CATEGORIAS */
    public List<Category> listCategories(){
        return categoryRepository.findAll(Sort.by("title").ascending());
    }
/* OBTENER CATEGORIA POR ID */
    public Category findByID(Long id){
        return  categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }
/* CREAR NUEVA CATGORIA */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

/* EDITAR CATEGORIA */
    public Category edit(Long id, EditCategoryCommand category) {

        return categoryRepository.findById(id)
                .map(cat -> {
                    if (category.title() != null && !category.title().isBlank()) {
                        cat.setTitle(category.title());
                    }
                    return categoryRepository.save(cat);
                })
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }
/* ELIMINAR CATEGORIA */
    public void deleteById(Long id) {
        // No borrar categoria por defecto
        if (id != 1L) {
            taskService.updateCategoryBeforeDelete(id, 1L);
            categoryRepository.deleteById(id);

        } else {
            throw new RuntimeException("No está permitido eliminar la categoría principal.");
        }
    }
}

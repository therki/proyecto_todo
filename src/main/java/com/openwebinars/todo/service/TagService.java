package com.openwebinars.todo.service;

import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.repos.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /* Listar etiquetas */
    public List<Tag> findAll(){
        return tagRepository.findAll();
    }
    /* Obtener etiqueta por id */
    public Tag findById(Long id){
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada con ID: " + id));
    } /* Obtner etiqueta por nombre */
    public Tag findByName(String name){
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada con nombre: " + name));
    }
    /* Crear tag */
    public  Tag save (Tag tag){
        if(tagRepository.findByName(tag.getName()).isPresent()){
            throw new RuntimeException("La etiqueta '" + tag.getName() + "' ya existe");
        }
        tag.setId(null);
        return tagRepository.save(tag);
    }

    /* Editar etiqueta */
    public Tag edit(Long id, Tag tag){
        if(tagRepository.findByName(tag.getName()).isPresent()){
            throw new RuntimeException("La etiqueta '" + tag.getName() + "' ya existe");
        }
        return tagRepository.findById(id)
            .map(t -> {
                t.setName(tag.getName());
                return tagRepository.save(t);
            })
            .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada"));
    }

    /* Eliminar etiqueta */
    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }
}

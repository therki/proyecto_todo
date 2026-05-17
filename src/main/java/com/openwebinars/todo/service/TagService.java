package com.openwebinars.todo.service;

import com.openwebinars.todo.dto.EditTagDto;
import com.openwebinars.todo.dto.GetTagDto;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.repos.TagRepository;
import com.openwebinars.todo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /* Listar todas  etiquetas */
    public List<GetTagDto> findAll(){
        return tagRepository.findAll()
                .stream()
                .map(GetTagDto::of)
                .toList();
    }
    /* Listar tags de usuario */
    public List<GetTagDto> findAllByUser(User user){
        return tagRepository.findByUser(user)
                .stream()
                .map(GetTagDto::of)
                .toList();
    }

    /* Obtener etiqueta por id */
    public Tag findById(Long id){
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada con ID: " + id));
    }
    /* Obtner etiqueta por nombre */
    public Tag findByName(String name){
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada con nombre: " + name));
    }
    /* Obtner etiqueta del usuario por nombre */
    public Tag findByNameAndUser(String name, User user){
        return tagRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada con nombre: " + name));
    }
    /* Crear tag */
    public  Tag save (EditTagDto tag, User user){
        if (tagRepository.findByNameAndUser(tag.name(), user).isPresent()) {
            throw new RuntimeException("La etiqueta '" + tag.name() + "' ya existe");
        }
        Tag tagRes = Tag.builder()
                .name(tag.name())
                .user(user)
                .build();
        return tagRepository.save(tagRes);
    }

    /* Editar etiqueta */
    public Tag edit(Long id, EditTagDto tag, User user){
        if(tagRepository.findByNameAndUser(tag.name(), user).isPresent()){
            throw new RuntimeException("La etiqueta '" + tag.name() + "' ya existe");
        }
        return tagRepository.findById(id)
                .map(t -> {
                    t.setName(tag.name());
                    return tagRepository.save(t);
                })
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada"));
    }

    /* Eliminar etiqueta */
    public void deleteById(Long id, User user) {
        Tag tag = tagRepository.findById(id)
                .filter(t->t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada"));
        ;
        tagRepository.deleteById(id);
    }
    /* Buscar por usuario */
}

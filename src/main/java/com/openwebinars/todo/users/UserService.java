package com.openwebinars.todo.users;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /* Registrar usuario */
    public User register(NewUserCommand cmd) {
        User user = User.builder()
                .username(cmd.username())
                .email(cmd.email())
                .fullname(cmd.fullname())
                .password(passwordEncoder.encode(cmd.password()))
                .role(cmd.role() != null ? cmd.role() : User.RoleType.USUARIO)
                .build();
        return userRepository.save(user);
    }
    /* Cambiar role del usuario */
    public User changeRole(Long userId, User.RoleType role) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setRole(role);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
    }
    /**** CRUD USUARIO ****/

    /* Crear usuario */
    public User createUser(User user){
        return userRepository.save(user);
    }

    /* Editar usuario */
    public User updateUser(Long userId, NewUserCommand user){

        return userRepository.findById(userId)
                .map(u->{
                    u.setEmail(user.email());
                    u.setUsername(user.username());
                    if (user.password() != null && !user.password().isBlank()) {
                        u.setPassword(passwordEncoder.encode(user.password()));
                    }
                    u.setRole(user.role());
                    return  userRepository.save(u);
                })
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado con ID: " + userId));
    }

    /* Editar usuario - desde usuario (solo unos campos) */
    public User updatePartialUser(Long userId, EditUserCommand user){

        return userRepository.findById(userId)
                .map(u->{
                    u.setEmail(user.email());
                    u.setUsername(user.username());
                    if (user.password() != null && !user.password().isBlank()) {
                        u.setPassword(passwordEncoder.encode(user.password()));
                    }
                    return  userRepository.save(u);
                })
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado con ID: " + userId));
    }

    /* Listar todos los usuarios */
    public List<User> listUsers(){
        return userRepository.findAll();
    }
    /* Obtener usuario por ID */
    public User getUser(Long userId){
        return userRepository
                .findById(userId).orElseThrow(()-> new RuntimeException("Usuario no encontrado con ID: " + userId));
    }
    /* Obtener usuario por Email */
    public User getUser(String email){
        return userRepository
                .findByEmail(email).orElseThrow(()-> new RuntimeException("Usuario no encontrado con email: " + email));
    }
    /* Eliminar usuario */
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

}
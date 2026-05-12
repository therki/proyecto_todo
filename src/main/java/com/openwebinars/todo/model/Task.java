package com.openwebinars.todo.model;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.users.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Priority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Task {
    @Id @GeneratedValue
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String title;

    private String description;

    private boolean completed;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDate deadline;

    public enum Priority { BAJA, MEDIA, ALTA }
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    @JoinColumn(foreignKey =@ForeignKey(name= "fk_task_category"))
    private Category category;

    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(name="task_tag",
            joinColumns = @JoinColumn(name="task_id"),
            foreignKey = @ForeignKey(name="fk_task_tag_task"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            inverseForeignKey = @ForeignKey(name="fk_task_tag_tag")
    )
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name="fk_task_user"))
    private User author;

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Task task = (Task) o;
        return getId() != null && Objects.equals(getId(), task.getId());
    }
}

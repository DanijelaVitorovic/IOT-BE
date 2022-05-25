package com.dex.coreserver.model;

import com.dex.coreserver.model.enums.Actions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "role",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"roleName"}, name = "unq_role_name")
)
public class Role extends AbstractDataModel {

    @NotBlank(message = "{NotBlank.role.roleName}")
    private String roleName;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> users = new ArrayList<>();

    @ElementCollection(targetClass = Actions.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "role_actions")
    @Column(name = "actions")
    private List<Actions> actions;

    private String regex;
    private String regexDescription;
    private Integer priority;

    public void addUser(User user) {
        this.users.add(user);
        user.getRoles().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getRoles().remove(this);
    }

}

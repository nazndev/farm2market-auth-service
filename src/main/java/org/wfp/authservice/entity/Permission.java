package org.wfp.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Permission extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
}

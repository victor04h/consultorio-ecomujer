package com.consultorio_obstetra.obstetra.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permiso", uniqueConstraints = @UniqueConstraint(columnNames = {"modulo", "accion"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String modulo;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(length = 150)
    private String descripcion;
}

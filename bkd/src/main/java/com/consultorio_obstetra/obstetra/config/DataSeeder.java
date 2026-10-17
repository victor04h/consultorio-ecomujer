package com.consultorio_obstetra.obstetra.config;

import com.consultorio_obstetra.obstetra.model.Perfil;
import com.consultorio_obstetra.obstetra.model.Permiso;
import com.consultorio_obstetra.obstetra.model.Persona;
import com.consultorio_obstetra.obstetra.model.Usuario;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.repository.PerfilRepository;
import com.consultorio_obstetra.obstetra.repository.PermisoRepository;
import com.consultorio_obstetra.obstetra.repository.PersonaRepository;
import com.consultorio_obstetra.obstetra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Crea, si no existen, el catalogo de permisos, los perfiles base (roles) y
 * un usuario Administrador de arranque para poder entrar al dashboard la
 * primera vez.
 *
 * Usuario: admin@consultorio.com
 * Clave:   Admin123!
 * (cambiala apenas entres la primera vez).
 *
 * Modulos del catalogo de permisos (uno por seccion del panel; la accion es
 * siempre "ACCESO" porque hoy el control es a nivel de seccion completa, no
 * por operacion individual dentro de ella):
 * PERFILES, USUARIOS, CATEGORIAS, PRODUCTOS, PROFESIONALES, PACIENTES,
 * CITAS, HISTORIAL_CLINICO, CALIFICACIONES.
 *
 * "Inicio" (el resumen del dashboard) no requiere permiso: cualquier cuenta
 * que inicie sesion en el panel lo puede ver.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final String ACCION_ACCESO = "ACCESO";

    private static final List<String> MODULOS = List.of(
            "PERFILES", "USUARIOS", "CATEGORIAS", "PRODUCTOS", "PROFESIONALES",
            "PACIENTES", "CITAS", "HISTORIAL_CLINICO", "CALIFICACIONES"
    );

    private final PerfilRepository perfilRepository;
    private final PermisoRepository permisoRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Set<Permiso> todosLosPermisos = sembrarPermisos();

        boolean administradorEsNuevo = perfilRepository.findByNombre("Administrador").isEmpty();
        Perfil administrador = perfilRepository.findByNombre("Administrador")
                .orElseGet(() -> perfilRepository.save(Perfil.builder()
                        .nombre("Administrador")
                        .descripcion("Acceso completo al sistema de gestion")
                        .estado(EstadoActivo.ACTIVO)
                        .build()));
        // El Administrador siempre debe partir con el catalogo completo de
        // permisos marcado; si esta vacio (recien creado, o una instalacion
        // anterior a que existiera este catalogo) se lo completamos.
        if (administradorEsNuevo || administrador.getPermisos().isEmpty()) {
            administrador.setPermisos(new HashSet<>(todosLosPermisos));
            administrador = perfilRepository.save(administrador);
        }

        perfilRepository.findByNombre("Paciente")
                .orElseGet(() -> perfilRepository.save(Perfil.builder()
                        .nombre("Paciente")
                        .descripcion("Paciente que usa el sitio web de citas")
                        .estado(EstadoActivo.ACTIVO)
                        .build()));

        sembrarPerfilConPermisosPorDefecto(
                "Secretaria",
                "Personal de recepcion: agenda citas, atiende pacientes y mantiene el catalogo",
                Set.of("PACIENTES", "CITAS", "CATEGORIAS", "PRODUCTOS", "PROFESIONALES", "CALIFICACIONES")
        );

        // Nota: la pantalla de Citas carga en una sola tanda Pacientes, Productos
        // y Profesionales junto con las citas (para los combos del formulario), y
        // la de Historial Clinico hace lo mismo con Pacientes y Profesionales. Por
        // eso el Obstetra necesita esos modulos marcados aunque solo termine
        // viendo sus propias citas/historiales (el filtrado es por datos, no por
        // pantalla). El administrador puede ajustar esto luego desde Perfiles.
        sembrarPerfilConPermisosPorDefecto(
                "Obstetra",
                "Profesional médico: ve y atiende sus propias citas e historiales clínicos",
                Set.of("CITAS", "HISTORIAL_CLINICO", "CALIFICACIONES", "PROFESIONALES", "PACIENTES", "PRODUCTOS")
        );

        if (!usuarioRepository.existsByCorreo("admin@consultorio.com")) {
            Persona persona = personaRepository.save(Persona.builder()
                    .numeroDocumento("ADMIN-0001")
                    .nombres("Administrador")
                    .apellidos("del Sistema")
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .persona(persona)
                    .perfil(administrador)
                    .correo("admin@consultorio.com")
                    .contrasenaHash(passwordEncoder.encode("Admin123!"))
                    .estado(EstadoActivo.ACTIVO)
                    .correoValidado(true)
                    .build());
        }
    }

    /** Crea cualquier permiso del catalogo que todavia no exista y devuelve el catalogo completo. */
    private Set<Permiso> sembrarPermisos() {
        Set<Permiso> permisos = new HashSet<>();
        for (String modulo : MODULOS) {
            Permiso permiso = permisoRepository.findByModuloAndAccion(modulo, ACCION_ACCESO)
                    .orElseGet(() -> permisoRepository.save(Permiso.builder()
                            .modulo(modulo)
                            .accion(ACCION_ACCESO)
                            .descripcion("Acceso a la seccion " + modulo.replace("_", " "))
                            .build()));
            permisos.add(permiso);
        }
        return permisos;
    }

    /** Crea el perfil si no existe, con el set de modulos por defecto ya marcado. */
    private void sembrarPerfilConPermisosPorDefecto(String nombre, String descripcion, Set<String> modulosPorDefecto) {
        if (perfilRepository.findByNombre(nombre).isPresent()) {
            return;
        }
        Set<Permiso> permisos = new HashSet<>();
        for (String modulo : modulosPorDefecto) {
            permisoRepository.findByModuloAndAccion(modulo, ACCION_ACCESO).ifPresent(permisos::add);
        }
        perfilRepository.save(Perfil.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .estado(EstadoActivo.ACTIVO)
                .permisos(permisos)
                .build());
    }
}

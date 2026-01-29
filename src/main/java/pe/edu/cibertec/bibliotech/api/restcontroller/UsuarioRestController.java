package pe.edu.cibertec.bibliotech.api.restcontroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.bibliotech.api.request.UsuarioRequestDto;
import pe.edu.cibertec.bibliotech.api.response.UsuarioResponseDto;
import pe.edu.cibertec.bibliotech.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody UsuarioRequestDto request) {
        UsuarioResponseDto response = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto request) {
        UsuarioResponseDto response = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obtener(@PathVariable Long id) {
        UsuarioResponseDto response = usuarioService.obtener(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listar() {
        List<UsuarioResponseDto> response = usuarioService.listar();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDto>> listarActivos() {
        List<UsuarioResponseDto> response = usuarioService.listarActivos();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar/dni/{dni}")
    public ResponseEntity<UsuarioResponseDto> buscarPorDni(@PathVariable String dni) {
        UsuarioResponseDto response = usuarioService.buscarPorDni(dni);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar/email")
    public ResponseEntity<UsuarioResponseDto> buscarPorEmail(@RequestParam String email) {
        UsuarioResponseDto response = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        usuarioService.activar(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok().build();
    }
}
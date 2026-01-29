package pe.edu.cibertec.bibliotech.api.restcontroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.bibliotech.api.request.PrestamoRequestDto;
import pe.edu.cibertec.bibliotech.api.response.PrestamoResponseDto;
import pe.edu.cibertec.bibliotech.service.PrestamoService;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoRestController {
    
    private final PrestamoService prestamoService;
    
    @PostMapping
    public ResponseEntity<PrestamoResponseDto> registrarPrestamo(@Valid @RequestBody PrestamoRequestDto request) {
        PrestamoResponseDto response = prestamoService.registrarPrestamo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PatchMapping("/{id}/devolver")
    public ResponseEntity<PrestamoResponseDto> registrarDevolucion(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        PrestamoResponseDto response = prestamoService.registrarDevolucion(id, observaciones);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        prestamoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDto> obtener(@PathVariable Long id) {
        PrestamoResponseDto response = prestamoService.obtener(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PrestamoResponseDto>> listar() {
        List<PrestamoResponseDto> response = prestamoService.listar();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<PrestamoResponseDto>> listarActivos() {
        List<PrestamoResponseDto> response = prestamoService.listarActivos();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/vencidos")
    public ResponseEntity<List<PrestamoResponseDto>> listarVencidos() {
        List<PrestamoResponseDto> response = prestamoService.listarVencidos();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PrestamoResponseDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<PrestamoResponseDto> response = prestamoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/libro/{libroId}")
    public ResponseEntity<List<PrestamoResponseDto>> listarPorLibro(@PathVariable Long libroId) {
        List<PrestamoResponseDto> response = prestamoService.listarPorLibro(libroId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/actualizar-vencidos")
    public ResponseEntity<Void> actualizarEstadosVencidos() {
        prestamoService.actualizarEstadosVencidos();
        return ResponseEntity.ok().build();
    }
}
package pe.edu.cibertec.bibliotech.api.restcontroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.bibliotech.api.request.AutorRequestDto;
import pe.edu.cibertec.bibliotech.api.response.AutorResponseDto;
import pe.edu.cibertec.bibliotech.service.AutorService;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
@RequiredArgsConstructor
public class AutorRestController {
    
    private final AutorService autorService;
    
    @PostMapping
    public ResponseEntity<AutorResponseDto> crear(@Valid @RequestBody AutorRequestDto request) {
        AutorResponseDto response = autorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AutorResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AutorRequestDto request) {
        AutorResponseDto response = autorService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        autorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AutorResponseDto> obtener(@PathVariable Long id) {
        AutorResponseDto response = autorService.obtener(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<AutorResponseDto>> listar() {
        List<AutorResponseDto> response = autorService.listar();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<AutorResponseDto>> buscarPorNombre(@RequestParam String nombre) {
        List<AutorResponseDto> response = autorService.buscarPorNombre(nombre);
        return ResponseEntity.ok(response);
    }
}
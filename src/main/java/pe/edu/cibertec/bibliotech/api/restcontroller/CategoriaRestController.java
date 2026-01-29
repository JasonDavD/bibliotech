package pe.edu.cibertec.bibliotech.api.restcontroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.bibliotech.api.request.CategoriaRequestDto;
import pe.edu.cibertec.bibliotech.api.response.CategoriaResponseDto;
import pe.edu.cibertec.bibliotech.service.CategoriaService;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaRestController {
    
    private final CategoriaService categoriaService;
    
    @PostMapping
    public ResponseEntity<CategoriaResponseDto> crear(@Valid @RequestBody CategoriaRequestDto request) {
        CategoriaResponseDto response = categoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDto request) {
        CategoriaResponseDto response = categoriaService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> obtener(@PathVariable Long id) {
        CategoriaResponseDto response = categoriaService.obtener(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDto>> listar() {
        List<CategoriaResponseDto> response = categoriaService.listar();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<CategoriaResponseDto> buscarPorNombre(@RequestParam String nombre) {
        CategoriaResponseDto response = categoriaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(response);
    }
}
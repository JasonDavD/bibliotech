package pe.edu.cibertec.bibliotech.api.restcontroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.bibliotech.api.request.LibroRequestDto;
import pe.edu.cibertec.bibliotech.api.response.LibroResponseDto;
import pe.edu.cibertec.bibliotech.service.LibroService;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroRestController {
    
    private final LibroService libroService;
    
    @PostMapping
    public ResponseEntity<LibroResponseDto> crear(@Valid @RequestBody LibroRequestDto request) {
        LibroResponseDto response = libroService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LibroResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LibroRequestDto request) {
        LibroResponseDto response = libroService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LibroResponseDto> obtener(@PathVariable Long id) {
        LibroResponseDto response = libroService.obtener(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<LibroResponseDto>> listar() {
        List<LibroResponseDto> response = libroService.listar();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/disponibles")
    public ResponseEntity<List<LibroResponseDto>> listarDisponibles() {
        List<LibroResponseDto> response = libroService.listarDisponibles();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar/titulo")
    public ResponseEntity<List<LibroResponseDto>> buscarPorTitulo(@RequestParam String titulo) {
        List<LibroResponseDto> response = libroService.buscarPorTitulo(titulo);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar/autor/{autorId}")
    public ResponseEntity<List<LibroResponseDto>> buscarPorAutor(@PathVariable Long autorId) {
        List<LibroResponseDto> response = libroService.buscarPorAutor(autorId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar/categoria/{categoriaId}")
    public ResponseEntity<List<LibroResponseDto>> buscarPorCategoria(@PathVariable Long categoriaId) {
        List<LibroResponseDto> response = libroService.buscarPorCategoria(categoriaId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<LibroResponseDto>> buscar(@RequestParam String keyword) {
        List<LibroResponseDto> response = libroService.buscarPorPalabra(keyword);
        return ResponseEntity.ok(response);
    }
}
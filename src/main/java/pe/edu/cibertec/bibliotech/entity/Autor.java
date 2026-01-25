package pe.edu.cibertec.bibliotech.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(length = 100)
	private String nacionalidad;

	@Column(name = "fecha_registro", updatable = false)
	private LocalDateTime fechaRegistro = LocalDateTime.now();

	@OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
	private List<Libro> libros = new ArrayList<>();
}
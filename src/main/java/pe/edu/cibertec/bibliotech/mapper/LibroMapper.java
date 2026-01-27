package pe.edu.cibertec.bibliotech.mapper;

import java.time.LocalDateTime;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import pe.edu.cibertec.bibliotech.api.request.LibroRequestDto;
import pe.edu.cibertec.bibliotech.api.response.LibroResponseDto;
import pe.edu.cibertec.bibliotech.entity.Autor;
import pe.edu.cibertec.bibliotech.entity.Categoria;
import pe.edu.cibertec.bibliotech.entity.Libro;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LibroMapper {
    
    // Request DTO a Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "autor", source = "autorId", qualifiedByName = "idToAutor")
    @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "idToCategoria")
    @Mapping(target = "cantidadDisponible", source = "cantidadTotal")
    @Mapping(target = "fechaRegistro", expression = "java(getCurrentDateTime())")
    @Mapping(target = "ultimaActualizacion", expression = "java(getCurrentDateTime())")
    @Mapping(target = "prestamos", ignore = true)
    Libro toEntity(LibroRequestDto dto);
    
    // Entity a Response DTO
    @Mapping(target = "autorNombre", source = "autor.nombre")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "disponible", expression = "java(entity.isDisponible())")
    LibroResponseDto toResponseDto(Libro entity);
    
    // Update Entity desde DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "autor", source = "autorId", qualifiedByName = "idToAutor")
    @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "idToCategoria")
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "ultimaActualizacion", expression = "java(getCurrentDateTime())")
    @Mapping(target = "prestamos", ignore = true)
    void updateEntityFromDto(LibroRequestDto dto, @MappingTarget Libro entity);
    
    // Métodos auxiliares
    @Named("idToAutor")
    default Autor idToAutor(Long autorId) {
        if (autorId == null) return null;
        Autor autor = new Autor();
        autor.setId(autorId);
        return autor;
    }
    
    @Named("idToCategoria")
    default Categoria idToCategoria(Long categoriaId) {
        if (categoriaId == null) return null;
        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        return categoria;
    }
    
    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
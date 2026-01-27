package pe.edu.cibertec.bibliotech.mapper;

import java.time.LocalDateTime;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import pe.edu.cibertec.bibliotech.api.request.CategoriaRequestDto;
import pe.edu.cibertec.bibliotech.api.response.CategoriaResponseDto;
import pe.edu.cibertec.bibliotech.entity.Categoria;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaMapper {
    
    // Request DTO a Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", expression = "java(getCurrentDateTime())")
    @Mapping(target = "libros", ignore = true)
    Categoria toEntity(CategoriaRequestDto dto);
    
    // Entity a Response DTO
    @Mapping(target = "cantidadLibros", expression = "java(entity.getLibros() != null ? entity.getLibros().size() : 0)")
    CategoriaResponseDto toResponseDto(Categoria entity);
    
    // Update Entity desde DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "libros", ignore = true)
    void updateEntityFromDto(CategoriaRequestDto dto, @MappingTarget Categoria entity);
    
    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
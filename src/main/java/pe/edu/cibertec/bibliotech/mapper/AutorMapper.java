package pe.edu.cibertec.bibliotech.mapper;

import java.time.LocalDateTime;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import pe.edu.cibertec.bibliotech.api.request.AutorRequestDto;
import pe.edu.cibertec.bibliotech.api.response.AutorResponseDto;
import pe.edu.cibertec.bibliotech.entity.Autor;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AutorMapper {
    
    // Request DTO a Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", expression = "java(getCurrentDateTime())")
    @Mapping(target = "libros", ignore = true)
    Autor toEntity(AutorRequestDto dto);
    
    // Entity a Response DTO
    @Mapping(target = "cantidadLibros", expression = "java(entity.getLibros() != null ? entity.getLibros().size() : 0)")
    AutorResponseDto toResponseDto(Autor entity);
    
    // Update Entity desde DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "libros", ignore = true)
    void updateEntityFromDto(AutorRequestDto dto, @MappingTarget Autor entity);
    
    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
package mappers;

public final class TipoPrendaMapper {
    private TipoPrendaMapper() { }
    public static dtos.TipoPrendaDTO toDTO(dominio.TipoPrenda tipo) {
        return tipo == null ? null : dtos.TipoPrendaDTO.valueOf(tipo.name());
    }
    public static dominio.TipoPrenda toEntity(dtos.TipoPrendaDTO tipo) {
        return tipo == null ? null : dominio.TipoPrenda.valueOf(tipo.name());
    }
}

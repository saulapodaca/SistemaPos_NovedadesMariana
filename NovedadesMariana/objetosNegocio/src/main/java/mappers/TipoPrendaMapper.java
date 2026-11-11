package mappers;

public final class TipoPrendaMapper {
    private TipoPrendaMapper() { }
    public static dtos.TipoPrendaDTO toDTO(enums.TipoPrenda tipo) {
        return tipo == null ? null : dtos.TipoPrendaDTO.valueOf(tipo.name());
    }
    public static enums.TipoPrenda toEntity(dtos.TipoPrendaDTO tipo) {
        return tipo == null ? null : enums.TipoPrenda.valueOf(tipo.name());
    }
}

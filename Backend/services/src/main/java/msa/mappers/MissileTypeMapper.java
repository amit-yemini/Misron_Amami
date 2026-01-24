package msa.mappers;

import msa.DBEntities.MissileType;
import msa.MissileTypeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MissileTypeMapper {
    MissileType convert(MissileTypeDTO dto);
}

package msa.mappers;

import msa.Alert;
import msa.AlertDistribution;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    AlertDistribution toDistribution(Alert alert);
}

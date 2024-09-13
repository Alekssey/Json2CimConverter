package ru.nti.dtps.cimconverter.fromcim.extractor.equipment

import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.proto.lib.field.FieldLibId

internal fun RawEquipmentNodeDto.copyWithFields(
    vararg fields: Pair<FieldLibId, Any?>
): RawEquipmentNodeDto {
    return this.copy(
        fields = this.fields + fields
            .filter { (_, value) -> value != null }
            .associate { (fieldLibId, value) -> fieldLibId to value!!.toString() }
    )
}

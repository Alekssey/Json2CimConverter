package ru.nti.dtps.cimconverter.tocim.equipment

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource

data class EquipmentRelatedResources(
    val equipmentResource: RdfResource,
    val portIdToTerminalResourceMap: Map<String, RdfResource>,
    val equipmentAdditionalResources: List<RdfResource> = emptyList()
)

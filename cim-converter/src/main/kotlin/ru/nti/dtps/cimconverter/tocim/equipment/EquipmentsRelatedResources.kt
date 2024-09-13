package ru.nti.dtps.cimconverter.tocim.equipment

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource

data class EquipmentsRelatedResources(
    val equipmentIdToResourceMap: Map<String, RdfResource>,
    val portIdToTerminalResourceMap: Map<String, RdfResource>,
    val equipmentAdditionalResources: List<RdfResource>,
    val shortCircuitIdToFaultResources: Map<String, RdfResource>
)

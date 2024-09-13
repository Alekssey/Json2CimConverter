package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.extractor.EquipmentsExtractionResult
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.PortsCreator
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.Size
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.XyDto
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object ShortCircuitExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.EquipmentFault
    private val threePhaseEquipmentLibId = EquipmentLibId.SHORT_CIRCUIT
    private val singlePhaseEquipmentLibId = EquipmentLibId.SHORT_CIRCUIT_1PH

    override fun extract(
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        links: List<RawEquipmentLinkDto>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ) = EquipmentsExtractionResult(
        equipments = create(
            repository.selectAllVarsFromTriples(
                cimClass,
                CimClasses.IdentifiedObject.name,
                CimClasses.FaultImpedance.rGround,
                DtpsClasses.EquipmentFault.enableByUaZeroCrossing,
                CimClasses.Fault.kind,
                CimClasses.Fault.FaultyEquipment,
                CimClasses.EquipmentFault.Terminal
            ),
            equipmentIdToPortsMap,
            objectIdToDiagramObjectMap
        )
    )

    private fun create(
        queryResult: TupleQueryResult,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ): List<RawEquipmentNodeDto> = queryResult.mapIndexed { index, bindingSet ->
        val equipmentId = bindingSet.extractIdentifiedObjectId()

        val equipmentLibId = defineEquipmentLibId(
            threePhaseEquipmentLibId,
            singlePhaseEquipmentLibId,
            equipmentIdToPortsMap[equipmentId]
        )
        val equipmentLib = EquipmentMetaInfoManager.getEquipmentLibById(equipmentLibId)

        val equipmentName = bindingSet.extractIdentifiedObjectNameOrNull()
        val size = Size(
            EquipmentMetaInfoManager.getEquipmentLibById(equipmentLibId).height[Language.RU]!!,
            EquipmentMetaInfoManager.getEquipmentLibById(equipmentLibId).width[Language.RU]!!
        )
        val diagramObject = objectIdToDiagramObjectMap[equipmentId]
        val coords: XyDto = diagramObject?.points?.firstOrNull()?.coords ?: XyDto(0.0, 0.0)
        RawEquipmentNodeDto(
            id = equipmentId,
            libEquipmentId = equipmentLibId,
            dimensions = RawEquipmentNodeDto.SizeDto(
                height = size.height,
                width = size.width
            ),
            hour = diagramObject?.hour ?: 0,
            coords = XyDto(
                coords.x,
                coords.y
            ),
            voltageLevelId = null,
            ports = PortsCreator.createPorts(
                equipmentId,
                equipmentLib,
                equipmentIdToPortsMap[equipmentId] ?: listOf(),
                coords
            ),
            fields = mapOf(
                FieldLibId.NAME to (equipmentName ?: "${equipmentLib.name[Language.RU]} ${index + 1}"),
                FieldLibId.SUBSTATION to "noId",
                FieldLibId.RESISTANCE to bindingSet.extractStringValueOrNull(
                    CimClasses.FaultImpedance.rGround
                ),
                if (equipmentLibId == threePhaseEquipmentLibId) {
                    FieldLibId.ENABLE_BY_UA_ZERO_CROSSING
                } else {
                    FieldLibId.ENABLE_BY_U_ZERO_CROSSING
                } to if (
                    bindingSet.extractBooleanValueOrFalse(DtpsClasses.EquipmentFault.enableByUaZeroCrossing)
                ) {
                    "enabled"
                } else {
                    "disabled"
                }
            )
        )
    }
}

package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getName
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TPowerTransformer
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TPowerTransformerEnum
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TTransformerWinding
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TTransformerWindingEnum
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId.*
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId

class PowerTransformerBuilder {
    private val counter = ObjectsCounter()

    fun createPowerTransformers(
        powerTransformerEquipments: List<ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain>,
        linkIdToConnectivityNodeContainer: Map<String, ConnectivityNodeContainer>
    ): List<TPowerTransformer> {
        return powerTransformerEquipments.map { powerTransformerEquipment ->
            when (powerTransformerEquipment.libEquipmentId) {
                TWO_WINDING_POWER_TRANSFORMER, TWO_WINDING_AUTO_TRANSFORMER -> createTwoWindingPowerTransformer(
                    powerTransformerEquipment,
                    linkIdToConnectivityNodeContainer
                )

                THREE_WINDING_POWER_TRANSFORMER, THREE_WINDING_AUTO_TRANSFORMER -> createThreeWindingPowerTransformer(
                    powerTransformerEquipment,
                    linkIdToConnectivityNodeContainer
                )

                else -> throw RuntimeException(
                    "Unexpected power transformer type: " + powerTransformerEquipment.libEquipmentId
                )
            }
        }
    }

    private fun createTwoWindingPowerTransformer(
        powerTransformerEquipment: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
        linkIdToConnectivityNodeContainer: Map<String, ConnectivityNodeContainer>
    ) = TPowerTransformer().apply {
        val highVoltageKilovolts = powerTransformerEquipment.fields[FieldLibId.FIRST_WINDING_RATED_VOLTAGE]!!
        val lowVoltageKilovolts = powerTransformerEquipment.fields[FieldLibId.SECOND_WINDING_RATED_VOLTAGE]!!
        name = "T${counter.increaseAndGet()}"
        type = TPowerTransformerEnum.PTR
        desc = "${powerTransformerEquipment.getName()} $highVoltageKilovolts/$lowVoltageKilovolts кВ"
        transformerWinding.add(
            createTransformerWinding(
                linkIdToConnectivityNodeContainer[
                    powerTransformerEquipment.ports
                        .first { it.libId == PortLibId.FIRST }.links[0]
                ]!!,
                "HighVoltageWinding",
                highVoltageKilovolts
            )
        )
        transformerWinding.add(
            createTransformerWinding(
                linkIdToConnectivityNodeContainer[
                    powerTransformerEquipment.ports
                        .first { it.libId == PortLibId.SECOND }.links[0]
                ]!!,
                "LowVoltageWinding",
                lowVoltageKilovolts
            )
        )
    }

    private fun createThreeWindingPowerTransformer(
        powerTransformerEquipment: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
        linkIdToConnectivityNodeContainer: Map<String, ConnectivityNodeContainer>
    ) = TPowerTransformer().apply {
        val highVoltageKilovolts = powerTransformerEquipment.fields[FieldLibId.FIRST_WINDING_RATED_VOLTAGE]!!
        val mediumVoltageKilovolts = powerTransformerEquipment.fields[FieldLibId.SECOND_WINDING_RATED_VOLTAGE]!!
        val lowVoltageKilovolts = powerTransformerEquipment.fields[FieldLibId.THIRD_WINDING_RATED_VOLTAGE]!!
        name = "T${counter.increaseAndGet()}"
        type = TPowerTransformerEnum.PTR
        desc = "${powerTransformerEquipment.getName()} $highVoltageKilovolts/$mediumVoltageKilovolts/$lowVoltageKilovolts кВ"
        transformerWinding.add(
            createTransformerWinding(
                linkIdToConnectivityNodeContainer[
                    powerTransformerEquipment.ports
                        .first { it.libId == PortLibId.FIRST }.links[0]
                ]!!,
                "HighVoltageWinding",
                highVoltageKilovolts
            )
        )
        transformerWinding.add(
            createTransformerWinding(
                linkIdToConnectivityNodeContainer[
                    powerTransformerEquipment.ports
                        .first { it.libId == PortLibId.SECOND }.links[0]
                ]!!,
                "MediumVoltageWinding",
                lowVoltageKilovolts
            )
        )
        transformerWinding.add(
            createTransformerWinding(
                linkIdToConnectivityNodeContainer[
                    powerTransformerEquipment.ports
                        .first { it.libId == PortLibId.THIRD }.links[0]
                ]!!,
                "LowVoltageWinding",
                lowVoltageKilovolts
            )
        )
    }

    private fun createTransformerWinding(
        cnContainer: ConnectivityNodeContainer,
        windingName: String,
        kilovolts: String
    ) = TTransformerWinding().apply {
        name = windingName
        type = TTransformerWindingEnum.PTW
        desc = "Обмотка $kilovolts кВ"
        terminal.add(buildTerminal(cnContainer))
    }
}

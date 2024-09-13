package ru.nti.dpts.schememanagerback.transmissionLine.usecase

import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineSingleCircuitTemplate

interface GetAllTransmissionLineSegmentTemplates {
    fun execute(): List<TransmissionLineSingleCircuitTemplate>
}

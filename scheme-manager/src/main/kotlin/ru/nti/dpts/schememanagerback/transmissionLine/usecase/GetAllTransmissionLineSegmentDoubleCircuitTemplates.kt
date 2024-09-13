package ru.nti.dpts.schememanagerback.transmissionLine.usecase

import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineDoubleCircuitTemplate

interface GetAllTransmissionLineSegmentDoubleCircuitTemplates {
    fun execute(): List<TransmissionLineDoubleCircuitTemplate>
}

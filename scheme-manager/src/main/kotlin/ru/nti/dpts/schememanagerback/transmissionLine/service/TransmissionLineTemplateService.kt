package ru.nti.dpts.schememanagerback.transmissionLine.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nti.dpts.schememanagerback.transmissionLine.domain.*
import ru.nti.dpts.schememanagerback.transmissionLine.domain.adapter.TransmissionLineTemplatePersister
import ru.nti.dtps.proto.TransmissionLineEventMsg
import ru.nti.dtps.proto.scheme.matrices.TransmissionLineMatrices
import java.util.*

//@Service
class TransmissionLineTemplateService(
    private val transmissionLineTemplatePersister: TransmissionLineTemplatePersister
) {

    private val log = LoggerFactory.getLogger(javaClass)

//    @Transactional
    fun onTransmissionLineEvent(event: TransmissionLineEventMsg) {
        when (event.eventType) {
            TransmissionLineEventMsg.EventType.CREATE, TransmissionLineEventMsg.EventType.UPDATE -> {
                transmissionLineTemplatePersister.save(event.mapToDomain())
            }

            TransmissionLineEventMsg.EventType.DELETE ->
                transmissionLineTemplatePersister.deleteById(UUID.fromString(event.id))

            TransmissionLineEventMsg.EventType.UNRECOGNIZED ->
                log.warn("Unrecognized line event type has been received")

            null -> throw NullPointerException("Line event type cannot be null")
        }
    }
}

private fun TransmissionLineEventMsg.mapToDomain() = TransmissionLineTemplate(
    id = UUID.fromString(id),
    name = model.name,
    frequency = model.frequency,
    lastModifiedAt = model.lastModifiedAt,
    matrices3x3 = if (model.hasMatrices3X3()) {
        model.matrices3X3.mapToDomain()
    } else {
        null
    },
    matrices6x6 = if (model.hasMatrices6X6()) {
        model.matrices6X6.mapToDomain()
    } else {
        null
    },
    companyId = this.companyId
)

private fun TransmissionLineMatrices.Matrices3x3.mapToDomain() = Matrices3x3(
    seriesImpedanceOmhPerMeterPhaseValues = seriesImpedanceOmhPerMeterPhaseValues.mapToDomain(),
    shuntAdmittanceSiemensPerMeterPhaseValues = shuntAdmittanceSiemensPerMeterPhaseValues.mapToDomain(),
    seriesImpedanceOmhPerMeterSequenceValues = seriesImpedanceOmhPerMeterSequenceValues.mapToDomain(),
    shuntAdmittanceSiemensPerMeterSequenceValues = shuntAdmittanceSiemensPerMeterSequenceValues.mapToDomain()
)

private fun TransmissionLineMatrices.Matrices6x6.mapToDomain() = Matrices6x6(
    seriesImpedanceOmhPerMeterPhaseValues = seriesImpedanceOmhPerMeterPhaseValues.mapToDomain(),
    shuntAdmittanceSiemensPerMeterPhaseValues = shuntAdmittanceSiemensPerMeterPhaseValues.mapToDomain(),
    seriesImpedanceOmhPerMeterSequenceValues = seriesImpedanceOmhPerMeterSequenceValues.mapToDomain(),
    shuntAdmittanceSiemensPerMeterSequenceValues = shuntAdmittanceSiemensPerMeterSequenceValues.mapToDomain()
)

private fun TransmissionLineMatrices.ComplexMatrix3x3.mapToDomain() = ComplexMatrix3x3(
    r0c0 = r0C0.mapToDomain(),
    r0c1 = r0C1.mapToDomain(),
    r0c2 = r0C2.mapToDomain(),
    r1c0 = r1C0.mapToDomain(),
    r1c1 = r1C1.mapToDomain(),
    r1c2 = r1C2.mapToDomain(),
    r2c0 = r2C0.mapToDomain(),
    r2c1 = r2C1.mapToDomain(),
    r2c2 = r2C2.mapToDomain()
)

private fun TransmissionLineMatrices.ComplexMatrix6x6.mapToDomain() = ComplexMatrix6x6(
    r0c0 = r0C0.mapToDomain(),
    r0c1 = r0C1.mapToDomain(),
    r0c2 = r0C2.mapToDomain(),
    r0c3 = r0C3.mapToDomain(),
    r0c4 = r0C4.mapToDomain(),
    r0c5 = r0C5.mapToDomain(),
    r1c0 = r1C0.mapToDomain(),
    r1c1 = r1C1.mapToDomain(),
    r1c2 = r1C2.mapToDomain(),
    r1c3 = r1C3.mapToDomain(),
    r1c4 = r1C4.mapToDomain(),
    r1c5 = r1C5.mapToDomain(),
    r2c0 = r2C0.mapToDomain(),
    r2c1 = r2C1.mapToDomain(),
    r2c2 = r2C2.mapToDomain(),
    r2c3 = r2C3.mapToDomain(),
    r2c4 = r2C4.mapToDomain(),
    r2c5 = r2C5.mapToDomain(),
    r3c0 = r3C0.mapToDomain(),
    r3c1 = r3C1.mapToDomain(),
    r3c2 = r3C2.mapToDomain(),
    r3c3 = r3C3.mapToDomain(),
    r3c4 = r3C4.mapToDomain(),
    r3c5 = r3C5.mapToDomain(),
    r4c0 = r4C0.mapToDomain(),
    r4c1 = r4C1.mapToDomain(),
    r4c2 = r4C2.mapToDomain(),
    r4c3 = r4C3.mapToDomain(),
    r4c4 = r4C4.mapToDomain(),
    r4c5 = r4C5.mapToDomain(),
    r5c0 = r5C0.mapToDomain(),
    r5c1 = r5C1.mapToDomain(),
    r5c2 = r5C2.mapToDomain(),
    r5c3 = r5C3.mapToDomain(),
    r5c4 = r5C4.mapToDomain(),
    r5c5 = r5C5.mapToDomain()
)

private fun TransmissionLineMatrices.Complex.mapToDomain() = Complex(
    re = re,
    im = im
)

package ru.nti.dpts.schememanagerback.transmissionLine.domain

import java.util.*

data class TransmissionLineTemplate(
    val id: UUID,
    val name: String,
    val frequency: Int,
    val lastModifiedAt: Long,
    val matrices3x3: Matrices3x3?,
    val matrices6x6: Matrices6x6?,
    val companyId: String
)

data class TransmissionLineSingleCircuitTemplate(
    val id: UUID,
    val name: String,
    val frequency: Int,
    val lastModifiedAt: Long,
    val matrices3x3: Matrices3x3,
    val companyId: String
)

data class TransmissionLineDoubleCircuitTemplate(
    val id: UUID,
    val name: String,
    val frequency: Int,
    val lastModifiedAt: Long,
    val matrices6x6: Matrices6x6,
    val companyId: String
)

data class Matrices3x3(
    val seriesImpedanceOmhPerMeterPhaseValues: ComplexMatrix3x3,
    val shuntAdmittanceSiemensPerMeterPhaseValues: ComplexMatrix3x3,
    val seriesImpedanceOmhPerMeterSequenceValues: ComplexMatrix3x3,
    val shuntAdmittanceSiemensPerMeterSequenceValues: ComplexMatrix3x3
)

data class Matrices6x6(
    val seriesImpedanceOmhPerMeterPhaseValues: ComplexMatrix6x6,
    val shuntAdmittanceSiemensPerMeterPhaseValues: ComplexMatrix6x6,
    val seriesImpedanceOmhPerMeterSequenceValues: ComplexMatrix6x6,
    val shuntAdmittanceSiemensPerMeterSequenceValues: ComplexMatrix6x6
)

data class Complex(
    val re: Float,
    val im: Float
)

data class ComplexMatrix3x3(
    val r0c0: Complex,
    val r0c1: Complex,
    val r0c2: Complex,

    val r1c0: Complex,
    val r1c1: Complex,
    val r1c2: Complex,

    val r2c0: Complex,
    val r2c1: Complex,
    val r2c2: Complex
)

data class ComplexMatrix6x6(
    val r0c0: Complex,
    val r0c1: Complex,
    val r0c2: Complex,
    val r0c3: Complex,
    val r0c4: Complex,
    val r0c5: Complex,

    val r1c0: Complex,
    val r1c1: Complex,
    val r1c2: Complex,
    val r1c3: Complex,
    val r1c4: Complex,
    val r1c5: Complex,

    val r2c0: Complex,
    val r2c1: Complex,
    val r2c2: Complex,
    val r2c3: Complex,
    val r2c4: Complex,
    val r2c5: Complex,

    val r3c0: Complex,
    val r3c1: Complex,
    val r3c2: Complex,
    val r3c3: Complex,
    val r3c4: Complex,
    val r3c5: Complex,

    val r4c0: Complex,
    val r4c1: Complex,
    val r4c2: Complex,
    val r4c3: Complex,
    val r4c4: Complex,
    val r4c5: Complex,

    val r5c0: Complex,
    val r5c1: Complex,
    val r5c2: Complex,
    val r5c3: Complex,
    val r5c4: Complex,
    val r5c5: Complex
)

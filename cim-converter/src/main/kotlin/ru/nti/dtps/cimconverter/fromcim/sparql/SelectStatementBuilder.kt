package ru.nti.dtps.cimconverter.fromcim.sparql

import ru.nti.dtps.cimconverter.rdf.schema.AbstractCimClass
import ru.nti.dtps.cimconverter.rdf.schema.CimField

interface SelectStatementBuilder : Builder {
    fun selectAllVarsFromTriples(
        subject: AbstractCimClass,
        vararg predicateAndObject: CimField
    ): SelectStatementBuilder
}

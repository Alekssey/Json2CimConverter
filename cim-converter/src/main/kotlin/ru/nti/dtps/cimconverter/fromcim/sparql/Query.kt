package ru.nti.dtps.cimconverter.fromcim.sparql

import ru.nti.dtps.cimconverter.rdf.schema.*

const val SUBJECT_NAME_OF_TRIPLE = "subject"

class Query(
    private val cimNamespaceUriFromFile: String
) {

    private var inputTriples: Triples? = null
    private val namespaces = mutableSetOf<RdfNamespace>()

    fun addTriples(
        subject: AbstractCimClass,
        predicatesAndObjectList: List<CimField>
    ) {
        inputTriples = Triples(subject, predicatesAndObjectList).also {
            namespaces.add(subject.namespace)
            predicatesAndObjectList.forEach { predicatesAndObject ->
                namespaces.add(predicatesAndObject.cimClass.namespace)
            }
        }
    }

    fun construct(): String = namespaces
        .filter { it.getAlias() != Namespace.cim.getAlias() }
        .joinToString(separator = "\n") { "PREFIX ${it.getAlias()}: <${it.getUri()}>\n" } +
        "PREFIX ${Namespace.cim.getAlias()}: <$cimNamespaceUriFromFile>\n" +
        "SELECT *\n" +
        "WHERE {\n" +
        "${inputTriples!!.stringify()}\n" +
        "}"

    class Triples(
        private val subject: AbstractCimClass,
        private val predicatesAndObjects: List<CimField>
    ) {
        fun stringify() =
            "\t?$SUBJECT_NAME_OF_TRIPLE a ${subject.fullName} .\n" +
                predicatesAndObjects.joinToString(separator = "\n") {
                    "\t\tOPTIONAL { ?$SUBJECT_NAME_OF_TRIPLE ${it.fullName} ?${it.nameWithNamespaceAlias} . }"
                }
    }
}

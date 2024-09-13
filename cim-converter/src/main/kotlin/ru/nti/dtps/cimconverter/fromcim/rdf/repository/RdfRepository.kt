package ru.nti.dtps.cimconverter.fromcim.rdf.repository

import org.eclipse.rdf4j.query.TupleQueryResult
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.RepositoryConnection
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.w3c.dom.Document
import org.w3c.dom.Element
import ru.nti.dtps.cimconverter.fromcim.exception.CimDataException
import ru.nti.dtps.cimconverter.fromcim.exception.RdfFileException
import ru.nti.dtps.cimconverter.fromcim.sparql.QueryBuilder
import ru.nti.dtps.cimconverter.rdf.schema.AbstractCimClass
import ru.nti.dtps.cimconverter.rdf.schema.CimField
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

private const val RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

class RdfRepository(
    private val connection: RepositoryConnection,
    private val cimNamespaceUriFromFile: String
) {
    fun selectAllVarsFromTriples(
        subject: AbstractCimClass,
        vararg predicateAndObject: CimField
    ): TupleQueryResult {
        return QueryBuilder.new(cimNamespaceUriFromFile).selectAllVarsFromTriples(
            subject = subject,
            predicateAndObject = predicateAndObject
        ).build(connection).evaluate()
    }
}

fun <T> runWithRdfRepository(
    xmlRdfFileInputStream: InputStream,
    run: (repository: RdfRepository) -> T
): T {
    val bytes = xmlRdfFileInputStream.readAllBytes()
    val cimNamespaceUriFromFile = findCimNamespace(buildXmlDocument(bytes))

    val rdfModel = try {
        Rio.parse(ByteArrayInputStream(bytes), RDF_URI, RDFFormat.RDFXML)
    } catch (e: Exception) {
        throw RdfFileException(e.message!!)
    }

    val repository: Repository = SailRepository(MemoryStore())
    val connection: RepositoryConnection = repository.connection
    connection.add(rdfModel)

    return run(RdfRepository(connection, cimNamespaceUriFromFile))
}

private fun findCimNamespace(xmlDoc: Document): String {
    val element = xmlDoc.getElementsByTagName("rdf:RDF").item(0) as Element?
    return if (element == null) {
        throw RdfFileException("File isn't an RDF file")
    } else {
        element.getAttributeNode("xmlns:cim")?.value ?: throw CimDataException("File isn't a CIM file")
    }
}

private fun buildXmlDocument(xml: ByteArray): Document {
    val documentBuilderFactory = DocumentBuilderFactory.newInstance()
    val documentBuilder = documentBuilderFactory.newDocumentBuilder()
    return documentBuilder.parse(ByteArrayInputStream(xml))
}

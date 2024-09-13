package ru.nti.dtps.cimconverter.fromcim.sparql

import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.query.TupleQuery
import org.eclipse.rdf4j.repository.RepositoryConnection
import ru.nti.dtps.cimconverter.rdf.schema.AbstractCimClass
import ru.nti.dtps.cimconverter.rdf.schema.CimField

class QueryBuilder private constructor(
    private val query: Query
) : SelectStatementBuilder {

    companion object {
        fun new(cimNamespaceUriFromFile: String): SelectStatementBuilder {
            return QueryBuilder(Query(cimNamespaceUriFromFile))
        }
    }

    override fun selectAllVarsFromTriples(
        subject: AbstractCimClass,
        vararg predicateAndObject: CimField
    ): SelectStatementBuilder {
        this.query.addTriples(subject, predicateAndObject.toList())
        return this
    }

    override fun build(repositoryConnection: RepositoryConnection): TupleQuery {
        val queryString = query.construct()
        return repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString)
    }
}

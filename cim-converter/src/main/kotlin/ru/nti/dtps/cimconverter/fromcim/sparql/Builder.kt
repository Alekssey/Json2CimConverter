package ru.nti.dtps.cimconverter.fromcim.sparql

import org.eclipse.rdf4j.query.TupleQuery
import org.eclipse.rdf4j.repository.RepositoryConnection

interface Builder {
    fun build(repositoryConnection: RepositoryConnection): TupleQuery
}

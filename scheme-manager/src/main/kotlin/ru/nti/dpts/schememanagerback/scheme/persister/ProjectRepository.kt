package ru.nti.dpts.schememanagerback.scheme.persister

import org.springframework.data.mongodb.repository.MongoRepository

interface ProjectRepository : MongoRepository<ProjectDoc, String>

package ru.nti.dpts.schememanagerback.application.common.base

abstract class AggregateRoot<T>(id: T) : DomainEntity<T>(id)

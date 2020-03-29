package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Request;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {

}
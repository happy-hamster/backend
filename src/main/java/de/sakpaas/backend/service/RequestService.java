package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Request;
import org.springframework.stereotype.Service;


@Service
public class RequestService {

  private final RequestRepository requestRepository;

  public RequestService(RequestRepository requestRepository) {
    this.requestRepository = requestRepository;
  }

  public void addRequest(Request request){
    requestRepository.save(request);
  }
}

package de.sakpaas.backend.model;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "REQUEST")
public class Request {

  @Id
  @GeneratedValue
  @Column(name = "ID", nullable = false)
  Long id;

  @Column(name = "TIMESTAMP")
  ZonedDateTime date;

  @Column(name = "ADDRESS")
  String address;

  @Column(name = "METHOD")
  String method;

  @Column(name = "URI")
  String requestUri;
}

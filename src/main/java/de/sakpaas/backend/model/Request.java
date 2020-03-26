package de.sakpaas.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Setter
@Getter
@Entity(name = "REQUEST")
public class Request {
  @Column(name = "ID", nullable = false)
  @Id
  @GeneratedValue
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

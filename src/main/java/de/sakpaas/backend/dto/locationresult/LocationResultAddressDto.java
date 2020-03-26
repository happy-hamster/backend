package de.sakpaas.backend.dto.locationresult;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.sakpaas.backend.model.Address;
import lombok.Data;

@Data
@JsonPropertyOrder({"country", "city", "postcode", "street", "housenumber"})
public class LocationResultAddressDto {

  private String country = null;
  private String city = null;
  private String postcode = null;
  private String street = null;
  private String housenumber = null;

  public LocationResultAddressDto(Address address) {
    this.country = address.getCountry();
    this.city = address.getCity();
    this.postcode = address.getPostcode();
    this.street = address.getStreet();
    this.housenumber = address.getHousenumber();
  }

}

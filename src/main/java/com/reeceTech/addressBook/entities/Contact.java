package com.reeceTech.addressBook.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Contact implements Serializable {
  @Id @Column @GeneratedValue private Long id;
  @Column private String firstName;
  @Column private String lastName;
  @Column private String mobileNumber;
  @Column private String landLine;
  @ManyToOne private AddressBook addressBook;

  public Contact(
      final String firstName,
      final String lastName,
      final String mobileNumber,
      final String landLine) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.mobileNumber = mobileNumber;
    this.landLine = landLine;
  }
}

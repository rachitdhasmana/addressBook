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
public class AddressBook implements Serializable {
  @Id
  @Column(unique = true, length = 20, nullable = false)
  private String id;

  @Column private String name;

  @Column private String description;
}

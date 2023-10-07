package com.reeceTech.addressBook.dao;

import com.reeceTech.addressBook.entities.Contact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContactRepository extends JpaRepository<Contact, Long> {
  public List<Contact> findByAddressBookId(String addressBookId);

  public List<Contact> findByFirstNameIgnoreCaseOrLastNameIgnoreCase(
      String fistName, String lastName);

  public List<Contact> findByAddressBookIdAndFirstNameIgnoreCase(
      String addressBookId, String firstName);

  public List<Contact> findByAddressBookIdAndLastNameIgnoreCase(
      String addressBookId, String lastName);

  public Contact findByAddressBookIdAndFirstNameAndLastNameAndMobileNumberAndLandLine(
      String addressBookId,
      String firstName,
      String lastName,
      String mobileNumber,
      String landLine);

  @Query("select distinct c.firstName, c.lastName, c.mobileNumber, c.landLine from Contact c")
  public List<Object> findAllDistinctContacts();
}

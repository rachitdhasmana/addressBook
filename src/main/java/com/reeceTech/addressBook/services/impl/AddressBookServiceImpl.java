package com.reeceTech.addressBook.services.impl;

import com.reeceTech.addressBook.controller.AddressBookRestController;
import com.reeceTech.addressBook.dao.AddressBookRepository;
import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.services.AddressBookService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class AddressBookServiceImpl implements AddressBookService {

  Logger logger = LoggerFactory.getLogger(AddressBookRestController.class);
  @Autowired private AddressBookRepository addressBookRepository;

  // For Test Only
  public AddressBookServiceImpl(AddressBookRepository repo) {
    this.addressBookRepository = repo;
  }

  /*
      Name: getAllAddressBooks
      Description: fetches all record from the addressbook table
      Args: []
      Returns: List of records fetched from address book table
  */
  @Override
  public List<AddressBook> getAllAddressBooks() {
    List<AddressBook> addressBooks = new ArrayList<>();
    try {
      addressBooks.addAll(addressBookRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
      logger.info("AddressBooks Found: " + addressBooks.size());
    } catch (Exception ex) {
      logger.info("Exception while adding addressBook: " + ex.getMessage());
    }
    return addressBooks;
  }

  /*
      Name: getAddressBookById
      Description: fetches a record from the addressBook table searched by addressBookId
      Args: [
          addressBookId: id of the addressBook to be fetched
      ]
      Returns: object of addressBook record fetched from address book table by Id
  */
  @Override
  public AddressBook getAddressBookById(String addressBookId) {
    try {
      Optional<AddressBook> fetchedAddressBook = addressBookRepository.findById(addressBookId);
      if (fetchedAddressBook.isPresent()) {
        logger.info(
            "AddressBooks with id {addressBookId} Found".replace("addressBookId", addressBookId));
        return fetchedAddressBook.get();
      }
      logger.warn(
          "AddressBook with id {addressBookId} does not exists"
              .replace("addressBookId", addressBookId));
    } catch (IllegalArgumentException | JpaSystemException ex) {
      logger.error("Exception while adding addressBook: " + ex.getMessage());
    }
    return null;
  }

  /*
      Name: deleteAddressBookById
      Description: delete a record from the addressbook table
      Args: [
             addressBookId: id of the addressbook record to be deleted
      ]
      Returns: true if record to be deleted exists and is deleted, else false.
  */
  @Override
  public boolean deleteAddressBookById(String addressBookId) {
    try {
      if (addressBookRepository.existsById(addressBookId)) {
        addressBookRepository.deleteById(addressBookId);
        logger.info("Address Book {addressBook} deleted!".replace("addressBook", addressBookId));
        return true;
      }
      logger.warn(
          "Address Book {addressBook} does not exists!".replace("addressBook", addressBookId));
    } catch (IllegalArgumentException | JpaSystemException | DataIntegrityViolationException ex) {
      logger.error("Exception while deleting addressBook: " + ex.getMessage());
    }
    return false;
  }

  /*
      Name: addNewAddressBook
      Description: add a new record to the addressbook table
      Args: [
             addressBook: Addressbook Object with addressbook record values
      ]
      Returns: AddressBook object of the updated record if updated else null
  */
  @Override
  public AddressBook addNewAddressBook(AddressBook addressBook) {
    try {
      if (addressBookRepository.existsById(addressBook.getId())) {
        logger.warn(
            "Address Book with id {addressBookId} already exists!"
                .replace("addressBookId", addressBook.getId()));
        return null;
      }
      AddressBook savedAddressBook = addressBookRepository.save(addressBook);
      logger.info("Address Book {addressBook} saved!".replace("addressBook", addressBook.getId()));
      return savedAddressBook;
    } catch (IllegalArgumentException
        | OptimisticLockingFailureException
        | JpaSystemException
        | InvalidDataAccessApiUsageException ex) {
      logger.error("Exception while deleting addressBook: " + ex.getMessage());
    }
    return null;
  }

  /*
     Name: updateAddressBook
     Description: updates the contents of addressbook record
     Args: [
            addressBookId: id of the addressbook record to be updated
            addressBook: Addressbook Object with updated record values
     ]
     Returns: AddressBook object of the updated record
  */
  @Override
  public AddressBook updateAddressBook(String addressBookId, AddressBook addressBook) {
    try {
      Optional<AddressBook> optionalAddressBook = addressBookRepository.findById(addressBookId);
      if (optionalAddressBook.isPresent()) {
        logger.info("AddressBook {addressBookId} updated!".replace("addressBookId", addressBookId));
        addressBook.setId(addressBookId);
        addressBookRepository.save(addressBook);
        return addressBook;
      }
      logger.warn(
          "AddressBook {addressBookId} does not exist!".replace("addressBookId", addressBookId));
    } catch (IllegalArgumentException | JpaSystemException ex) {
      logger.error("Exception while updating addressBook: " + ex.getMessage());
    }
    return null;
  }
}

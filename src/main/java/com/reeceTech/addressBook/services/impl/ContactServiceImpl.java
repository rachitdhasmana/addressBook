package com.reeceTech.addressBook.services.impl;

import com.reeceTech.addressBook.controller.AddressBookRestController;
import com.reeceTech.addressBook.dao.AddressBookRepository;
import com.reeceTech.addressBook.dao.ContactRepository;
import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.entities.Contact;
import com.reeceTech.addressBook.services.ContactService;
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
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {

  Logger logger = LoggerFactory.getLogger(AddressBookRestController.class);

  @Autowired private ContactRepository contactRepository;
  @Autowired private AddressBookRepository addressBookRepository;

  // For Test Only
  public ContactServiceImpl(
      ContactRepository contactRepository, AddressBookRepository addressBookRepository) {
    this.contactRepository = contactRepository;
    this.addressBookRepository = addressBookRepository;
  }

  /*
      Name: checkIfContactExistsInAddressBook
      Description: checks
            if there is existing contact with same details in contact table
            if AddressBook with addressBookId exists
      Args: [
             addressBookId: Id of the addressBook where contact is to be looked
             contact: Contact class object which has record values to be looked up.
      ]
      Returns: true if a record is found with same details, else false
  */
  public boolean checkIfContactExistsInAddressBook(String addressBookId, Contact contact) {
    Contact foundContact =
        contactRepository.findByAddressBookIdAndFirstNameAndLastNameAndMobileNumberAndLandLine(
            addressBookId,
            contact.getFirstName(),
            contact.getLastName(),
            contact.getMobileNumber(),
            contact.getLandLine());
    return foundContact != null;
  }

  /*
      Name: addContactInAddressBook
      Description: Adds a new contact record to contacts table
      Args: [
             addressBookId: Id of the addressBook where contact is to be added
             contact: Contact class object which has record values to be added
      ]
      Returns: Object of the newly added contact else null
  */
  @Override
  public Contact addContactInAddressBook(String addressBookId, Contact contact) {
    try {
      if (!addressBookRepository.existsById(addressBookId)) {
        logger.warn(
            "AddressBook {addressBookId} does not exists!".replace("addressBookId", addressBookId));
        return null;
      }
      boolean isDuplicate = checkIfContactExistsInAddressBook(addressBookId, contact);
      if (isDuplicate) {
        logger.warn(
            "Contact {contactName} either already exists in addressBook {addressBookId}!"
                .replace("contactName", contact.getFirstName() + " " + contact.getLastName())
                .replace("addressBookId", addressBookId));
        return null;
      }
      // setting the addressBook object to link (foreign Key) with AddressBook table
      contact.setAddressBook(new AddressBook(addressBookId, addressBookId, ""));
      Contact savedContact = contactRepository.save(contact);
      logger.info(
          "Contact {contactName} saved in addressBook {addressBookId}!"
              .replace("contactName", contact.getFirstName() + " " + contact.getLastName())
              .replace("addressBookId", addressBookId));
      return savedContact;
    } catch (IllegalArgumentException
        | OptimisticLockingFailureException
        | JpaSystemException
        | InvalidDataAccessApiUsageException ex) {
      logger.error("Exception while adding new contact: " + ex.getMessage());
    }
    return null;
  }

  /*
      Name: getContactByIdInAddressBook
      Description: fetches a contact record with contact id from contacts table
      Args: [
             addressBookId: Id of the address book to look into
             contactId: Id of the contact record to be fetched
      ]
      Returns: Object of the fetched contact if found else null
  */
  @Override
  public Contact getContactByIdInAddressBook(String addressBookId, Long contactId) {
    try {
      Optional<Contact> contact = contactRepository.findById(contactId);
      if (contact.isPresent()) {
        Contact fetchedContact = contact.get();
        logger.info(
            "Contact {contactName} with id {id} found in addressBook {addressBookId}!"
                .replace(
                    "contactName",
                    fetchedContact.getFirstName() + " " + fetchedContact.getLastName())
                .replace("id", String.valueOf(contactId))
                .replace("addressBookId", fetchedContact.getAddressBook().getId()));
        return fetchedContact;
      }
      logger.warn("Contact with {id} Not Found!".replace("id", String.valueOf(contactId)));
    } catch (IllegalArgumentException | JpaSystemException ex) {
      logger.error("Exception while fetching contact by ContactID: " + ex.getMessage());
    }
    return null;
  }

  /*
      Name: getAllContactsInAddressBook
      Description: fetches all contact records from contacts table with specific addressBookID
      Args: [
             addressBookId: Id of the AddressBook with which contacts are to be searched
      ]
      Returns: List of the Contact records matching to addressBookID. Empty List if none found.
  */
  @Override
  public List<Contact> getAllContactsInAddressBook(String addressBookId) {
    List<Contact> fetchedContacts = new ArrayList<>();
    try {
      fetchedContacts.addAll(contactRepository.findByAddressBookId(addressBookId));
    } catch (Exception ex) {
      logger.error("Exception while fetching all contacts by addressBookID: " + ex.getMessage());
    }
    return fetchedContacts;
  }

  /*
    Name: getUniqueContactsInAllAddressBooks
    Description: fetches unique contact records from contacts table
    Args: []
    Returns: List of the Contact records that are all unique. Empty List if none found.
  */
  @Override
  public List<Contact> getUniqueContactsInAllAddressBooks() {
    List<Contact> contacts = new ArrayList<>();
    try {
      List<Object> contactList = contactRepository.findAllDistinctContacts();
      for (Object obj : contactList) {
        Object[] contactObj = (Object[]) obj;
        Contact contact =
            new Contact(
                (String) contactObj[0],
                (String) contactObj[1],
                (String) contactObj[2],
                (String) contactObj[3]);
        contacts.add(contact);
      }
    } catch (Exception ex) {
      logger.error("Exception while fetching unique contacts: " + ex.getMessage());
    }
    return contacts;
  }

  /*
    Name: getContactsBySearchFieldInAllAddressBooks
    Description: fetches contact records whose firstName or lastName matches with input search field
    Args: [
           name: string field use to match against firstName or Last Name of contact record
    ]
    Returns: List of the Contact with either firstName or LastName matched with input search field. Empty List if none found.
  */
  @Override
  public List<Contact> getContactsBySearchFieldInAllAddressBooks(String name) {
    List<Contact> searchedContacts = new ArrayList<>();
    try {
      searchedContacts.addAll(
          contactRepository.findByFirstNameIgnoreCaseOrLastNameIgnoreCase(name, name));
    } catch (Exception ex) {
      logger.error("Exception while searching contacts in all addressBooks: " + ex.getMessage());
    }
    return searchedContacts;
  }

  /*
  Name: getContactsBySearchFieldInOneAddressBook
  Description: fetches contact records whose firstName or lastName matches with input search field in an addressBook
  Args: [
         addressBookId: id of the address book with which contacts are to be searched
         name: string field use to match against firstName or Last Name of contact record
  ]
  Returns: List of the Contact with either firstName or LastName matched with input search field. Empty List if none found.
  */
  @Override
  public List<Contact> getContactsBySearchFieldInOneAddressBook(String addressBookId, String name) {
    List<Contact> searchedContacts = new ArrayList<>();
    try {
      searchedContacts.addAll(
          contactRepository.findByAddressBookIdAndFirstNameIgnoreCase(addressBookId, name));
      searchedContacts.addAll(
          contactRepository.findByAddressBookIdAndLastNameIgnoreCase(addressBookId, name));
    } catch (Exception ex) {
      logger.error("Exception while searching contacts in an addressBook: " + ex.getMessage());
    }
    return searchedContacts;
  }

  /*
      Name: deleteContactByIdInAddressBook
      Description: Delete a contact record from contacts table
      Args: [
             addressBookId: Id of the address book from where contact is to be removed
             contactId: Id of the Contact that is to be deleted
      ]
      Returns: true if the contact is found and deleted else false
  */
  @Override
  public boolean deleteContactByIdInAddressBook(String addressBookId, Long contactId) {
    try {
      if (contactRepository.existsById(contactId)) {
        contactRepository.deleteById(contactId);
        logger.info("Contact {contactId} deleted!".replace("contactId", String.valueOf(contactId)));
        return true;
      }
      logger.warn(
          "Contact {contactId} does not exists!".replace("contactId", String.valueOf(contactId)));
    } catch (IllegalArgumentException | JpaSystemException | DataIntegrityViolationException ex) {
      logger.error("Exception while deleting a contact in an addressBook: " + ex.getMessage());
    }
    return false;
  }

  /*
    Name: updateContactInAddressBook
    Description: updates a contact record with new field values in contacts table
    Args: [
           addressBookId: Id of the AddressBook in which contact is to be updated
           contactId: Id of the contact to be updated
           contact: object to Contact record with values to be updated
    ]
    Returns: Object of the Updated Contact record else null
  */
  @Override
  public Contact updateContactInAddressBook(String addressBookId, Long contactId, Contact contact) {
    try {
      Optional<Contact> optionalContact = contactRepository.findById(contactId);
      if (optionalContact.isPresent()) {
        boolean isDuplicate = checkIfContactExistsInAddressBook(addressBookId, contact);
        if (isDuplicate) {
          logger.warn(
              "Contact {contactName} already exists in addressBook {addressBookId}!"
                  .replace("contactName", contact.getFirstName() + " " + contact.getLastName())
                  .replace("addressBookId", addressBookId));
          return null;
        }
        if (!addressBookRepository.existsById(addressBookId)) {
          logger.warn(
              "AddressBook {addressBookId} does not exists!"
                  .replace("addressBookId", addressBookId));
          return null;
        }
        contact.setId(contactId);
        contact.setAddressBook(new AddressBook(addressBookId, addressBookId, ""));
        Contact updatedContact = contactRepository.save(contact);
        logger.info(
            "Contact {contactName} updated in addressBook {addressBookId}!"
                .replace("contactName", contact.getFirstName() + " " + contact.getLastName())
                .replace("addressBookId", addressBookId));
        return updatedContact;
      }
      logger.warn(
          "Contact {contactId} does not exists!".replace("contactId", String.valueOf(contactId)));
    } catch (IllegalArgumentException | OptimisticLockingFailureException | JpaSystemException ex) {
      logger.error("Exception while updating a contact in an addressBook: " + ex.getMessage());
    }
    return null;
  }
}

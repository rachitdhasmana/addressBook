package com.reeceTech.addressBook.services;

import com.reeceTech.addressBook.entities.Contact;
import java.util.List;

public interface ContactService {

  public Contact addContactInAddressBook(String addressBookId, Contact contact);

  public boolean deleteContactByIdInAddressBook(String addressBookId, Long contactId);

  public List<Contact> getAllContactsInAddressBook(String addressBookId);

  public List<Contact> getUniqueContactsInAllAddressBooks();

  public Contact getContactByIdInAddressBook(String addressBookId, Long contactId);

  public List<Contact> getContactsBySearchFieldInOneAddressBook(
      String addressBookId, String searchField);

  public List<Contact> getContactsBySearchFieldInAllAddressBooks(String searchField);

  public Contact updateContactInAddressBook(String addressBookId, Long contactId, Contact contact);
}

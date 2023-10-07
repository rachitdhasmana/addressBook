package com.reeceTech.addressBook.services;

import com.reeceTech.addressBook.entities.AddressBook;
import java.util.List;

public interface AddressBookService {
  public List<AddressBook> getAllAddressBooks();

  public AddressBook getAddressBookById(String addressBookId);

  public boolean deleteAddressBookById(String addressBookId);

  public AddressBook addNewAddressBook(AddressBook addressBook);

  public AddressBook updateAddressBook(String addressBookId, AddressBook addressBook);
}

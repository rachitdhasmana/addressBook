package com.reeceTech.addressBook.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.reeceTech.addressBook.dao.AddressBookRepository;
import com.reeceTech.addressBook.dao.ContactRepository;
import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.entities.Contact;
import com.reeceTech.addressBook.services.impl.ContactServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

  @Mock private ContactRepository contactRepository;
  @Mock private AddressBookRepository addressBookRepository;

  private ContactService contactService;

  @BeforeEach
  void setUp() {
    contactService = new ContactServiceImpl(contactRepository, addressBookRepository);
  }

  void mockCheckIfContactExistsInAddressBookWhenPresent(String addressBookId, Contact contactMock) {
    Mockito.when(
            contactRepository.findByAddressBookIdAndFirstNameAndLastNameAndMobileNumberAndLandLine(
                addressBookId,
                contactMock.getFirstName(),
                contactMock.getLastName(),
                contactMock.getMobileNumber(),
                contactMock.getLandLine()))
        .thenReturn(contactMock);
  }

  void mockCheckIfContactExistsInAddressBookWhenAbsent(String addressBookId, Contact contactMock) {
    Mockito.when(
            contactRepository.findByAddressBookIdAndFirstNameAndLastNameAndMobileNumberAndLandLine(
                addressBookId,
                contactMock.getFirstName(),
                contactMock.getLastName(),
                contactMock.getMobileNumber(),
                contactMock.getLandLine()))
        .thenReturn(null);
  }

  @Test
  void testAddContactInAddressBookWhenSuccess() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
        new Contact(
            1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    mockCheckIfContactExistsInAddressBookWhenAbsent(addressBookId, contactMock);

    Mockito.when(contactRepository.save(contactMock)).thenReturn(contactMock);
    Mockito.when(addressBookRepository.existsById(addressBookId)).thenReturn(true);

    Contact contact = contactService.addContactInAddressBook(addressBookId, contactMock);
    assertThat(contact).isNotNull();
  }

  @Test
  void testAddContactInAddressBookWhenDuplicate() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
        new Contact(
            1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    mockCheckIfContactExistsInAddressBookWhenPresent(addressBookId, contactMock);
    Mockito.when(addressBookRepository.existsById(addressBookId)).thenReturn(true);

    Contact contact = contactService.addContactInAddressBook(addressBookId, contactMock);
    assertThat(contact).isNull();
  }

  @Test
  void testAddContactInAddressBookWhenNoAddressBookExists() {
    String addressBookId = "not-present";
    AddressBook addressBookMock =
            new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
            new Contact(
                    1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Mockito.when(addressBookRepository.existsById(addressBookId)).thenReturn(false);

    Contact contact = contactService.addContactInAddressBook(addressBookId, contactMock);
    assertThat(contact).isNull();
  }

  @Test
  void testAddContactInAddressBookWhenExceptionThrown() {
    String addressBookId = "official";
    AddressBook addressBookMock =
            new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
            new Contact(
                    1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    mockCheckIfContactExistsInAddressBookWhenAbsent(addressBookId, contactMock);

    Mockito.when(contactRepository.save(contactMock)).thenThrow(new IllegalArgumentException());
    Mockito.when(addressBookRepository.existsById(addressBookId)).thenReturn(true);

    Contact contact = contactService.addContactInAddressBook(addressBookId, contactMock);
    assertThat(contact).isNull();
  }

  @Test
  void testGetContactByIdInAddressBookWhenPresent() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
        new Contact(
            1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Mockito.when(contactRepository.findById(1l)).thenReturn(Optional.of(contactMock));

    Contact contact = contactService.getContactByIdInAddressBook(addressBookId, 1l);
    assertThat(contact).isNotNull();
  }

  @Test
  void testGetContactByIdInAddressBookWhenAbsent() {
    String addressBookId = "official";
    Mockito.when(contactRepository.findById(1l)).thenReturn(Optional.empty());

    Contact contact = contactService.getContactByIdInAddressBook(addressBookId, 1l);
    assertThat(contact).isNull();
  }

  @Test
  void testGetContactByIdInAddressBookWhenExceptionThrown() {
    String addressBookId = "official";
    Mockito.when(contactRepository.findById(1l)).thenThrow(new IllegalArgumentException());

    Contact contact = contactService.getContactByIdInAddressBook(addressBookId, 1l);
    assertThat(contact).isNull();
  }

  @Test
  void testGetAllContactsInAddressBookWhenPresent() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
        new Contact(
            1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);
    List<Contact> contactsListMock = new ArrayList<>();
    contactsListMock.add(contactMock);

    Mockito.when(contactRepository.findByAddressBookId(addressBookId)).thenReturn(contactsListMock);

    List<Contact> contactsList = contactService.getAllContactsInAddressBook(addressBookId);
    assertThat(contactsList).isNotEmpty();
  }

  @Test
  void testGetAllContactsInAddressBookWhenAbsent() {
    String addressBookId = "official";
    Mockito.when(contactRepository.findByAddressBookId(addressBookId))
        .thenReturn(new ArrayList<>());

    List<Contact> contactsList = contactService.getAllContactsInAddressBook(addressBookId);
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testGetAllContactsInAddressBookWhenExceptionThrown() {
    String addressBookId = "official";
    Mockito.when(contactRepository.findByAddressBookId(addressBookId))
            .thenThrow(new IllegalArgumentException());

    List<Contact> contactsList = contactService.getAllContactsInAddressBook(addressBookId);
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testGetUniqueContactsInAllAddressBooksWhenPresent() {
    Object[] dataMock = new Object[] {"Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX"};
    List<Object> objectsMock = new ArrayList<>();
    objectsMock.add(dataMock);

    Mockito.when(contactRepository.findAllDistinctContacts()).thenReturn(objectsMock);
    List<Contact> contactsList = contactService.getUniqueContactsInAllAddressBooks();
    assertThat(contactsList).isNotEmpty();
  }

  @Test
  void testGetUniqueContactsInAllAddressBooksWhenAbsent() {
    Mockito.when(contactRepository.findAllDistinctContacts()).thenReturn(new ArrayList<>());
    List<Contact> contactsList = contactService.getUniqueContactsInAllAddressBooks();
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testGetUniqueContactsInAllAddressBooksWhenExceptionThrown() {
    Mockito.when(contactRepository.findAllDistinctContacts()).thenThrow(new IllegalArgumentException());
    List<Contact> contactsList = contactService.getUniqueContactsInAllAddressBooks();
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInAllAddressBooksWhenPresent() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    String firstName = "Rachit";
    Contact contactMock =
        new Contact(
            1l, firstName, "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);
    List<Contact> contactsListMock = new ArrayList<>();
    contactsListMock.add(contactMock);

    Mockito.when(
            contactRepository.findByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, firstName))
        .thenReturn(contactsListMock);
    List<Contact> contactsList =
        contactService.getContactsBySearchFieldInAllAddressBooks(firstName);
    assertThat(contactsList).isNotEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInAllAddressBooksWhenAbsent() {

    String firstName = "Rachit";

    Mockito.when(
            contactRepository.findByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, firstName))
        .thenReturn(new ArrayList<>());
    List<Contact> contactsList =
        contactService.getContactsBySearchFieldInAllAddressBooks(firstName);
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInAllAddressBooksWhenExceptionThrown() {

    String firstName = "Rachit";

    Mockito.when(
                    contactRepository.findByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, firstName))
            .thenThrow(new IllegalArgumentException());
    List<Contact> contactsList =
            contactService.getContactsBySearchFieldInAllAddressBooks(firstName);
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookWhenPresentAsOnlyFirstName() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    String firstName = "Rachit";
    Contact contactMock =
        new Contact(
            1l, firstName, "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);
    List<Contact> contactsListMock = new ArrayList<>();
    contactsListMock.add(contactMock);

    Mockito.when(
            contactRepository.findByAddressBookIdAndFirstNameIgnoreCase(addressBookId, firstName))
        .thenReturn(contactsListMock);
    Mockito.when(
            contactRepository.findByAddressBookIdAndLastNameIgnoreCase(addressBookId, firstName))
        .thenReturn(new ArrayList<>());

    List<Contact> contactsList =
        contactService.getContactsBySearchFieldInOneAddressBook(addressBookId, firstName);
    assertThat(contactsList).isNotEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookWhenPresentAsOnlyLastName() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    String lastName = "Dhasmana";
    Contact contactMock =
        new Contact(1l, "Rachit", lastName, "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);
    List<Contact> contactsListMock = new ArrayList<>();
    contactsListMock.add(contactMock);

    Mockito.when(
            contactRepository.findByAddressBookIdAndFirstNameIgnoreCase(addressBookId, lastName))
        .thenReturn(new ArrayList<>());
    Mockito.when(
            contactRepository.findByAddressBookIdAndLastNameIgnoreCase(addressBookId, lastName))
        .thenReturn(contactsListMock);

    List<Contact> contactsList =
        contactService.getContactsBySearchFieldInOneAddressBook(addressBookId, lastName);
    assertThat(contactsList).isNotEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookWhenPresentAsBothFirstNameLastName() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");

    String name = "Rachit";
    Contact contactMock =
        new Contact(1l, name, "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Contact contactMock2 =
        new Contact(2l, "Dhasmana", name, "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    List<Contact> contactsListMock = new ArrayList<>();
    contactsListMock.add(contactMock);

    List<Contact> contactsListMock2 = new ArrayList<>();
    contactsListMock2.add(contactMock2);

    Mockito.when(contactRepository.findByAddressBookIdAndFirstNameIgnoreCase(addressBookId, name))
        .thenReturn(contactsListMock);
    Mockito.when(contactRepository.findByAddressBookIdAndLastNameIgnoreCase(addressBookId, name))
        .thenReturn(contactsListMock2);

    List<Contact> contactsList =
        contactService.getContactsBySearchFieldInOneAddressBook(addressBookId, name);
    assertThat(contactsList.size()).isGreaterThan(1);
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookWhenAbsent() {
    String addressBookId = "official";
    String firstName = "Rachit";

    Mockito.when(
            contactRepository.findByAddressBookIdAndFirstNameIgnoreCase(addressBookId, firstName))
        .thenReturn(new ArrayList<>());
    Mockito.when(
            contactRepository.findByAddressBookIdAndLastNameIgnoreCase(addressBookId, firstName))
        .thenReturn(new ArrayList<>());

    List<Contact> contactsList =
        contactService.getContactsBySearchFieldInOneAddressBook(addressBookId, firstName);
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookWhenExceptionThrown() {
    String addressBookId = "official";
    String firstName = "Rachit";

    Mockito.when(
                    contactRepository.findByAddressBookIdAndFirstNameIgnoreCase(addressBookId, firstName))
            .thenThrow(new IllegalArgumentException());

    List<Contact> contactsList =
            contactService.getContactsBySearchFieldInOneAddressBook(addressBookId, firstName);
    assertThat(contactsList).isEmpty();
  }

  @Test
  void testDeleteContactByIdInAddressBookWhenPresent() {
    String addressBookId = "official";
    Mockito.when(contactRepository.existsById(1l)).thenReturn(true);
    boolean deletionResult = contactService.deleteContactByIdInAddressBook(addressBookId, 1l);
    assertThat(deletionResult).isTrue();
  }

  @Test
  void testDeleteContactByIdInAddressBookWhenAbsent() {
    String addressBookId = "official";
    Mockito.when(contactRepository.existsById(1l)).thenReturn(false);
    boolean deletionResult = contactService.deleteContactByIdInAddressBook(addressBookId, 1l);
    assertThat(deletionResult).isFalse();
  }

  @Test
  void testDeleteContactByIdInAddressBookWhenExceptionThrown() {
    String addressBookId = "official";
    Mockito.when(contactRepository.existsById(1l)).thenThrow(new IllegalArgumentException());
    boolean deletionResult = contactService.deleteContactByIdInAddressBook(addressBookId, 1l);
    assertThat(deletionResult).isFalse();
  }

  @Test
  void testUpdateContactInAddressBookWhenSuccess() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
        new Contact(
            1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Mockito.when(contactRepository.findById(1l)).thenReturn(Optional.of(contactMock));
    mockCheckIfContactExistsInAddressBookWhenAbsent(addressBookId, contactMock);
    Mockito.when(contactRepository.save(contactMock)).thenReturn(contactMock);
    Mockito.when(addressBookRepository.existsById(addressBookId)).thenReturn(true);

    Contact contact = contactService.updateContactInAddressBook(addressBookId, 1l, contactMock);
    assertThat(contact).isNotNull();
  }

  @Test
  void testUpdateContactInAddressBookWhenDuplicate() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
        new Contact(
            1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Mockito.when(contactRepository.findById(1l)).thenReturn(Optional.of(contactMock));
    mockCheckIfContactExistsInAddressBookWhenPresent(addressBookId, contactMock);

    Contact contact = contactService.updateContactInAddressBook(addressBookId, 1l, contactMock);
    assertThat(contact).isNull();
  }

  @Test
  void testUpdateContactInAddressBookWhenAbsent() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
        new Contact(
            1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Mockito.when(contactRepository.findById(1l)).thenReturn(Optional.empty());

    Contact contact = contactService.updateContactInAddressBook(addressBookId, 1l, contactMock);
    assertThat(contact).isNull();
  }

  @Test
  void testUpdateContactInAddressBookWhenNoAddressBook() {
    String addressBookId = "not-present";
    AddressBook addressBookMock =
            new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
            new Contact(
                    1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Mockito.when(contactRepository.findById(1l)).thenReturn(Optional.of(contactMock));
    mockCheckIfContactExistsInAddressBookWhenAbsent(addressBookId, contactMock);
    Mockito.when(addressBookRepository.existsById(addressBookId)).thenReturn(false);

    Contact contact = contactService.updateContactInAddressBook(addressBookId, 1l, contactMock);
    assertThat(contact).isNull();
  }

  @Test
  void testUpdateContactInAddressBookWhenExceptionThrown() {
    String addressBookId = "official";
    AddressBook addressBookMock =
            new AddressBook(addressBookId, "off_name", "my official address book");
    Contact contactMock =
            new Contact(
                    1l, "Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX", addressBookMock);

    Mockito.when(contactRepository.findById(1l)).thenThrow(new IllegalArgumentException());

    Contact contact = contactService.updateContactInAddressBook(addressBookId, 1l, contactMock);
    assertThat(contact).isNull();
  }
}

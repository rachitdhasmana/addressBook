package com.reeceTech.addressBook.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.entities.Contact;
import com.reeceTech.addressBook.services.AddressBookService;
import com.reeceTech.addressBook.services.ContactService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class AddressBookRestControllerUnitTest {
  @Mock private ContactService contactService;

  @Mock private AddressBookService addressBookService;

  private AddressBookRestController addressBookRestController;

  @BeforeEach
  void setUp() {
    addressBookRestController = new AddressBookRestController(contactService, addressBookService);
  }

  @Test
  void testAddNewAddressBookWhenSuccess() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");

    Mockito.when(addressBookService.addNewAddressBook(addressBookMock)).thenReturn(addressBookMock);

    ResponseEntity response = addressBookRestController.addNewAddressBook(addressBookMock);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((AddressBook) response.getBody()).isNotNull();
  }

  @Test
  void testAddNewAddressBookWhenFailure() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");

    Mockito.when(addressBookService.addNewAddressBook(addressBookMock)).thenReturn(null);

    ResponseEntity response = addressBookRestController.addNewAddressBook(addressBookMock);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testDeleteAddressBookByIdWhenSuccess() {
    String addressBookId = "official";
    Mockito.when(addressBookService.deleteAddressBookById(addressBookId)).thenReturn(true);

    ResponseEntity response = addressBookRestController.deleteAddressBookById(addressBookId);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testDeleteAddressBookByIdWhenFailure() {
    String addressBookId = "official";
    Mockito.when(addressBookService.deleteAddressBookById(addressBookId)).thenReturn(false);

    ResponseEntity response = addressBookRestController.deleteAddressBookById(addressBookId);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetAllAddressBooksWhenData() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");
    List<AddressBook> addressBooksListMock = new ArrayList<>();
    addressBooksListMock.add(addressBookMock);

    Mockito.when(addressBookService.getAllAddressBooks()).thenReturn(addressBooksListMock);

    ResponseEntity response = addressBookRestController.getAllAddressBooks();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((List<AddressBook>) response.getBody()).isNotEmpty();
  }

  @Test
  void testGetAllAddressBooksWhenNoData() {
    Mockito.when(addressBookService.getAllAddressBooks()).thenReturn(new ArrayList<AddressBook>());

    ResponseEntity response = addressBookRestController.getAllAddressBooks();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat((List<AddressBook>) response.getBody()).isEmpty();
  }

  @Test
  void testGetAddressBookByIdWhenPresent() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");

    Mockito.when(addressBookService.getAddressBookById(addressBookId)).thenReturn(addressBookMock);

    ResponseEntity response = addressBookRestController.getAddressBookById(addressBookId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((AddressBook) response.getBody()).isNotNull();
  }

  @Test
  void testGetAddressBookByIdWhenAbsent() {
    String addressBookId = "official";

    Mockito.when(addressBookService.getAddressBookById(addressBookId)).thenReturn(null);

    ResponseEntity response = addressBookRestController.getAddressBookById(addressBookId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testUpdateAddressBookWhenPresent() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");

    Mockito.when(addressBookService.updateAddressBook(addressBookId, addressBookMock))
        .thenReturn(addressBookMock);

    ResponseEntity response =
        addressBookRestController.updateAddressBook(addressBookId, addressBookMock);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((AddressBook) response.getBody()).isNotNull();
  }

  @Test
  void testUpdateAddressBookWhenAbsent() {
    String addressBookId = "official";
    AddressBook addressBookMock =
        new AddressBook(addressBookId, "off_name", "my official address book");

    Mockito.when(addressBookService.updateAddressBook(addressBookId, addressBookMock))
        .thenReturn(null);

    ResponseEntity response =
        addressBookRestController.updateAddressBook(addressBookId, addressBookMock);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testAddContactInAddressBookWhenSuccess() {
    String addressBookId = "official";
    Contact contactMock = new Contact("Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");

    Mockito.when(contactService.addContactInAddressBook(addressBookId, contactMock))
        .thenReturn(contactMock);

    ResponseEntity response =
        addressBookRestController.addContactInAddressBook(addressBookId, contactMock);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((Contact) response.getBody()).isNotNull();
  }

  @Test
  void testAddContactInAddressBookWhenFailure() {
    String addressBookId = "official";
    Contact contactMock = new Contact("Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");

    Mockito.when(contactService.addContactInAddressBook(addressBookId, contactMock))
        .thenReturn(null);

    ResponseEntity response =
        addressBookRestController.addContactInAddressBook(addressBookId, contactMock);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testDeleteContactByIdInAddressBookWhenPresent() {
    String addressBookId = "official";
    Mockito.when(contactService.deleteContactByIdInAddressBook(addressBookId, 1l)).thenReturn(true);
    ResponseEntity response = addressBookRestController.deleteContactByIdInAddressBook(addressBookId, 1l);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testDeleteContactByIdInAddressBookWhenAbsent() {
    String addressBookId = "official";
    Mockito.when(contactService.deleteContactByIdInAddressBook(addressBookId, 1l)).thenReturn(false);
    ResponseEntity response = addressBookRestController.deleteContactByIdInAddressBook(addressBookId, 1l);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetContactByIdWhenPresent() {
    String addressBookId = "official";
    Contact contactMock = new Contact("Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");
    Mockito.when(contactService.getContactByIdInAddressBook(addressBookId, 1l)).thenReturn(contactMock);

    ResponseEntity response = addressBookRestController.getContactByIdInAddressBook(addressBookId, 1l);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((Contact) response.getBody()).isNotNull();
  }

  @Test
  void testGetContactByIdWhenAbsent() {
    String addressBookId = "official";
    Mockito.when(contactService.getContactByIdInAddressBook(addressBookId, 1l)).thenReturn(null);

    ResponseEntity response = addressBookRestController.getContactByIdInAddressBook(addressBookId, 1l);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetAllContactsInAddressBookWhenData() {
    String addressBookId = "official";
    Contact contactMock = new Contact("Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");
    List<Contact> contactListMock = new ArrayList<>();
    contactListMock.add(contactMock);

    Mockito.when(contactService.getAllContactsInAddressBook(addressBookId))
        .thenReturn(contactListMock);

    ResponseEntity response = addressBookRestController.getAllContactsInAddressBook(addressBookId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((List<Contact>) response.getBody()).isNotEmpty();
  }

  @Test
  void testGetAllContactsInAddressBookWhenNoData() {
    String addressBookId = "official";

    Mockito.when(contactService.getAllContactsInAddressBook(addressBookId))
        .thenReturn(new ArrayList<Contact>());

    ResponseEntity response = addressBookRestController.getAllContactsInAddressBook(addressBookId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat((List<Contact>) response.getBody()).isEmpty();
  }

  @Test
  void testGetUniqueContactsInAllAddressBooksWhenData() {
    Contact contactMock = new Contact("Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");
    List<Contact> contactListMock = new ArrayList<>();
    contactListMock.add(contactMock);

    Mockito.when(contactService.getUniqueContactsInAllAddressBooks()).thenReturn(contactListMock);

    ResponseEntity response = addressBookRestController.getUniqueContactsInAllAddressBooks();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((List<Contact>) response.getBody()).isNotEmpty();
  }

  @Test
  void testGetUniqueContactsInAllAddressBooksWhenNoData() {

    Mockito.when(contactService.getUniqueContactsInAllAddressBooks())
        .thenReturn(new ArrayList<Contact>());

    ResponseEntity response = addressBookRestController.getUniqueContactsInAllAddressBooks();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat((List<Contact>) response.getBody()).isEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookWhenData() {
    String addressBookId = "official";
    String name = "Rachit";
    Contact contactMock = new Contact(name, "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");
    List<Contact> contactListMock = new ArrayList<>();
    contactListMock.add(contactMock);

    Mockito.when(contactService.getContactsBySearchFieldInOneAddressBook(addressBookId, name))
        .thenReturn(contactListMock);

    ResponseEntity response =
        addressBookRestController.getContactsBySearchFieldInOneAddressBook(addressBookId, name);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((List<Contact>) response.getBody()).isNotEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookWhenNoData() {
    String addressBookId = "official";
    String name = "Rachit";

    Mockito.when(contactService.getContactsBySearchFieldInOneAddressBook(addressBookId, name))
        .thenReturn(new ArrayList<Contact>());

    ResponseEntity response =
        addressBookRestController.getContactsBySearchFieldInOneAddressBook(addressBookId, name);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat((List<Contact>) response.getBody()).isEmpty();
  }

  @Test
  void testGetContactsBySearchFieldInAllAddressBooksWhenData() {
    String name = "Rachit";
    Contact contactMock = new Contact(name, "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");
    List<Contact> contactListMock = new ArrayList<>();
    contactListMock.add(contactMock);

    Mockito.when(contactService.getContactsBySearchFieldInAllAddressBooks(name))
        .thenReturn(contactListMock);

    ResponseEntity response =
        addressBookRestController.getContactsBySearchFieldInAllAddressBooks(name);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((List<Contact>) response.getBody()).isNotEmpty();
  }

  @Test
  void testContactsBySearchFieldInAllAddressBooksWhenNoData() {
    String name = "Rachit";

    Mockito.when(contactService.getContactsBySearchFieldInAllAddressBooks(name))
        .thenReturn(new ArrayList<Contact>());

    ResponseEntity response =
        addressBookRestController.getContactsBySearchFieldInAllAddressBooks(name);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat((List<Contact>) response.getBody()).isEmpty();
  }

  @Test
  void testUpdateContactInAddressBookWhenSuccess() {
    String addressBookId = "official";
    Long contactId = 1l;
    Contact contactMock = new Contact("Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");

    Mockito.when(contactService.updateContactInAddressBook(addressBookId, contactId, contactMock))
        .thenReturn(contactMock);

    ResponseEntity response =
        addressBookRestController.updateContactInAddressBook(addressBookId, contactId, contactMock);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((Contact) response.getBody()).isNotNull();
  }

  @Test
  void testUpdateContactInAddressBookWhenFailure() {
    String addressBookId = "official";
    Long contactId = 1l;
    Contact contactMock = new Contact("Rachit", "Dhasmana", "+61 4XX XXX XXX", "+61 4XX XXX XXX");

    Mockito.when(contactService.updateContactInAddressBook(addressBookId, contactId, contactMock))
        .thenReturn(null);

    ResponseEntity response =
        addressBookRestController.updateContactInAddressBook(addressBookId, contactId, contactMock);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}

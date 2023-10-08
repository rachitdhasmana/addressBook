package com.reeceTech.addressBook.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.entities.Contact;
import com.reeceTech.addressBook.services.AddressBookService;
import com.reeceTech.addressBook.services.ContactService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressBookRestControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private AddressBookService addressBookService;

  @Autowired private ContactService contactService;

  /* INTEGRATION CASES FOR ADDRESSBOOK API(s) */
  @Test
  void testAddNewAddressBookAPISuccess() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-new", "off_name_new", "my new official address book");

    // Execute the POST request
    mockMvc
        .perform(
            post("/v1/address-books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addressBook)))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(addressBook.getId())))
        .andExpect(jsonPath("$.name", is(addressBook.getName())))
        .andExpect(jsonPath("$.description", is(addressBook.getDescription())));

    // Clean up
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testAddNewAddressBookAPIDuplicateFailure() throws Exception {
    AddressBook addressBook = new AddressBook("official", "off_name", "my official address book");
    addressBookService.addNewAddressBook(addressBook);

    // Execute the POST request
    mockMvc
        .perform(
            post("/v1/address-books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addressBook)))

        // Validate the response code and content type
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Unable to save AddressBook, Bad Request"));

    // Clean up
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  public void testGetAllAddressBooksAPIWhenData() throws Exception {
    AddressBook addressBook = new AddressBook("official", "off_name", "my official address book");
    addressBookService.addNewAddressBook(addressBook);

    // Execute the GET request
    mockMvc
        .perform(get("/v1/address-books"))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(addressBook.getId())))
        .andExpect(jsonPath("$[0].name", is(addressBook.getName())))
        .andExpect(jsonPath("$[0].description", is(addressBook.getDescription())));

    // Clean up
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  public void testGetAllAddressBooksAPIWhenNoData() throws Exception {
    // Execute the GET request
    mockMvc
        .perform(get("/v1/address-books"))
        // Validate the response code and content type
        .andExpect(status().isNoContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void testGetAddressBookByIdAPIWhenData() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-getid", "off_name", "my official address book");
    addressBookService.addNewAddressBook(addressBook);

    // Execute the GET request
    mockMvc
        .perform(
            get("/v1/address-books/addressBookId".replace("addressBookId", addressBook.getId())))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(addressBook.getId())))
        .andExpect(jsonPath("$.name", is(addressBook.getName())))
        .andExpect(jsonPath("$.description", is(addressBook.getDescription())));

    // Clean up
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  public void testGetAddressBookByIdAPIWhenNoData() throws Exception {
    // Execute the GET request
    mockMvc
        .perform(
            get("/v1/address-books/addressBookId".replace("addressBookId", "random-address-book")))
        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(content().string("Unable to find the Address Book."));
  }

  @Test
  public void testDeleteAddressBookByIdAPIWhenData() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-del", "off_name", "my official address book");
    addressBookService.addNewAddressBook(addressBook);

    // Execute the DELETE request
    mockMvc
        .perform(
            delete("/v1/address-books/addressBookId".replace("addressBookId", addressBook.getId())))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().string("AddressBook has been successfully deleted."));
  }

  @Test
  public void testDeleteAddressBookByIdAPIWhenNoData() throws Exception {
    // Execute the DELETE request
    mockMvc
        .perform(
            delete(
                "/v1/address-books/addressBookId".replace("addressBookId", "random-address-book")))
        // Validate the response code and content type
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    "Unable to delete AddressBook, resource doesn't exist or "
                        + "deletion violates foreign key constraints"));
  }

  @Test
  void testUpdateAddressBookAPISuccess() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-update", "off_name", "my official address book");
    addressBookService.addNewAddressBook(addressBook);

    AddressBook updatedAddressBook =
        new AddressBook(
            "official-update", "off_name_new_name", "my new desc for official address book");

    // Execute the PUT request
    mockMvc
        .perform(
            put("/v1/address-books/addressBookId".replace("addressBookId", addressBook.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedAddressBook)))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(updatedAddressBook.getId())))
        .andExpect(jsonPath("$.name", is(updatedAddressBook.getName())))
        .andExpect(jsonPath("$.description", is(updatedAddressBook.getDescription())));

    // Clean up
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testUpdateAddressBookAPIFailure() throws Exception {
    AddressBook updatedAddressBook =
        new AddressBook(
            "official-no-new", "off_name_new_name", "my new desc for official address book");

    // Execute the PUT request
    mockMvc
        .perform(
            put("/v1/address-books/addressBookId"
                    .replace("addressBookId", updatedAddressBook.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedAddressBook)))

        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(content().string("Unable to update addressBook, addressBook does not exist."));
  }

  /* INTEGRATION CASES FOR CONTACTS API(s) */

  @Test
  void testAddContactInAddressBookAPISuccess() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-1", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(1l);

    // Execute the POST request
    mockMvc
        .perform(
            post("/v1/address-books/addressBookId/contacts"
                    .replace("addressBookId", addressBook.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contact)))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.firstName", is(contact.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(contact.getLastName())))
        .andExpect(jsonPath("$.mobileNumber", is(contact.getMobileNumber())))
        .andExpect(jsonPath("$.landLine", is(contact.getLandLine())));

    // Clean up
    List<Contact> contactList = contactService.getAllContactsInAddressBook(addressBook.getId());
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contactList.get(0).getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testAddContactInAddressBookAPIFailureDuplicateContact() throws Exception {
    AddressBook addressBook = new AddressBook("official1", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(2l);
    contact = contactService.addContactInAddressBook(addressBook.getId(), contact);

    // Execute the POST request
    mockMvc
        .perform(
            post("/v1/address-books/addressBookId/contacts"
                    .replace("addressBookId", addressBook.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contact)))

        // Validate the response code and content type
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    "Unable to add new contact, violating either uniqueness (duplicate) or "
                        + "foreign key (missing addressBook) constraint."));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact.getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testAddContactInAddressBookAPIFailureAddressBookNotPresent() throws Exception {
    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(3l);

    // Execute the POST request
    mockMvc
        .perform(
            post("/v1/address-books/addressBookId/contacts"
                    .replace("addressBookId", "random-address-book"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contact)))

        // Validate the response code and content type
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    "Unable to add new contact, violating either uniqueness (duplicate) or "
                        + "foreign key (missing addressBook) constraint."));
  }

  @Test
  void testDeleteContactByIdInAddressBookAPISuccess() throws Exception {
    AddressBook addressBook = new AddressBook("official2", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(4l);
    contact = contactService.addContactInAddressBook(addressBook.getId(), contact);

    // Execute the DELETE request
    mockMvc
        .perform(
            delete(
                "/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contact.getId()))))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().string("Contact has been successfully deleted."));

    // Clean up
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testDeleteContactByIdInAddressBookAPIFailure() throws Exception {
    // Execute the DELETE request
    mockMvc
        .perform(
            delete(
                "/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", "random-address-book")
                    .replace("contactId", "1")))

        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(content().string("Unable to delete Contact, Not Found."));
  }

  @Test
  void testGetContactByIdInAddressBookAPISuccess() throws Exception {
    AddressBook addressBook = new AddressBook("official3", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(5l);
    contact = contactService.addContactInAddressBook(addressBook.getId(), contact);

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contact.getId()))))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(contact.getId().intValue())))
        .andExpect(jsonPath("$.firstName", is(contact.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(contact.getLastName())))
        .andExpect(jsonPath("$.mobileNumber", is(contact.getMobileNumber())))
        .andExpect(jsonPath("$.landLine", is(contact.getLandLine())));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact.getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testGetContactByIdInAddressBookAPIFailure() throws Exception {
    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", "random")
                    .replace("contactId", "123")))

        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(content().string("Unable to find a contact in AddressBook"));
  }

  @Test
  void testUpdateContactInAddressBookAPISuccess() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-00", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(6l);
    Contact updatedContact = new Contact("rachit new", "dhasmana new", "321456789", "987654123");
    updatedContact.setId(7l);
    contact = contactService.addContactInAddressBook(addressBook.getId(), contact);

    // Execute the PUT request
    mockMvc
        .perform(
            put("/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contact.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedContact)))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(contact.getId().intValue())))
        .andExpect(jsonPath("$.firstName", is(updatedContact.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(updatedContact.getLastName())))
        .andExpect(jsonPath("$.mobileNumber", is(updatedContact.getMobileNumber())))
        .andExpect(jsonPath("$.landLine", is(updatedContact.getLandLine())));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact.getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testUpdateContactInAddressBookAPIFailureAddressBookNotExist() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-update", "off_name", "my official1 address book");

    Contact updatedContact = new Contact("rachit new", "dhasmana new", "321456789", "987654123");
    updatedContact.setId(8l);

    // Execute the PUT request
    mockMvc
        .perform(
            put("/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", "123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedContact)))

        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .string(
                    "Unable to update contact, contact / addressBook does not "
                        + "exist or violates uniqueness property."));
  }

  @Test
  void testUpdateContactInAddressBookAPIFailureDuplicateContact() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-1", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(9l);
    Contact updatedContact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    updatedContact.setId(10l);
    contact = contactService.addContactInAddressBook(addressBook.getId(), contact);

    // Execute the PUT request
    mockMvc
        .perform(
            put("/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contact.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedContact)))

        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .string(
                    "Unable to update contact, contact / addressBook does not "
                        + "exist or violates uniqueness property."));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact.getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testGetAllContactsInAddressBookAPISuccess() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-4-new", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact1 = new Contact("rachit1", "dhasmana1", "123456789", "987654321");
    contact1.setId(11l);
    Contact contact2 = new Contact("rachit2", "dhasmana2", "12345678", "98765432");
    contact2.setId(12l);

    contact1 = contactService.addContactInAddressBook(addressBook.getId(), contact1);
    contact2 = contactService.addContactInAddressBook(addressBook.getId(), contact2);

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/addressBookId/contacts"
                    .replace("addressBookId", addressBook.getId())))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)));

    // Clean up

    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact2.getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testGetAllContactsInAddressBookAPIWhenNoData() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-66", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/addressBookId/contacts"
                    .replace("addressBookId", addressBook.getId())))

        // Validate the response code and content type
        .andExpect(status().isNoContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));

    // Clean up
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testGetUniqueContactsInAllAddressBooksAPISuccess() throws Exception {
    AddressBook addressBook1 =
        new AddressBook("official-5", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook1);

    AddressBook addressBook2 =
        new AddressBook("official-6", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook2);

    Contact contact1 = new Contact("rachit1", "dhasmana1", "123456789", "987654321");
    contact1.setId(13l);
    Contact contact2 = new Contact("rachit2", "dhasmana2", "123456789", "987654321");
    contact2.setId(14l);

    Contact add1Contact1 = contactService.addContactInAddressBook(addressBook1.getId(), contact1);
    Contact add1Contact2 = contactService.addContactInAddressBook(addressBook1.getId(), contact2);
    Contact add2Contact1 = contactService.addContactInAddressBook(addressBook2.getId(), contact1);
    Contact add2Contact2 = contactService.addContactInAddressBook(addressBook2.getId(), contact2);

    // Execute the GET request
    mockMvc
        .perform(get("/v1/address-books/contacts"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook1.getId(), add1Contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook1.getId(), add1Contact2.getId());
    addressBookService.deleteAddressBookById(addressBook1.getId());

    contactService.deleteContactByIdInAddressBook(addressBook2.getId(), add2Contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook2.getId(), add2Contact2.getId());
    addressBookService.deleteAddressBookById(addressBook2.getId());
  }

  @Test
  void testGetUniqueContactsInAllAddressBooksAPIWhenNoData() throws Exception {
    // Execute the GET request
    mockMvc
        .perform(get("/v1/address-books/contacts"))

        // Validate the response code and content type
        .andExpect(status().isNoContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookAPISuccess() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-8-new", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact1 = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact1.setId(15l);
    Contact contact2 = new Contact("dhasmana", "rachit", "12345678", "98765432");
    contact2.setId(16l);
    Contact contact3 = new Contact("some", "random", "12345678", "98765432");
    contact3.setId(17l);

    contact1 = contactService.addContactInAddressBook(addressBook.getId(), contact1);
    contact2 = contactService.addContactInAddressBook(addressBook.getId(), contact2);
    contact3 = contactService.addContactInAddressBook(addressBook.getId(), contact3);

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/addressBookId/contacts/search?name=searchField"
                    .replace("addressBookId", addressBook.getId())
                    .replace("searchField", "rachit")))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)));

    // Clean up

    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact2.getId());
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact3.getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testGetContactsBySearchFieldInOneAddressBookAPINotFound() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-9-new", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact1 = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact1.setId(15l);
    Contact contact2 = new Contact("dhasmana", "rachit", "12345678", "98765432");
    contact2.setId(16l);
    Contact contact3 = new Contact("some", "random", "12345678", "98765432");
    contact3.setId(17l);

    contact1 = contactService.addContactInAddressBook(addressBook.getId(), contact1);
    contact2 = contactService.addContactInAddressBook(addressBook.getId(), contact2);
    contact3 = contactService.addContactInAddressBook(addressBook.getId(), contact3);

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/addressBookId/contacts/search?name=searchField"
                    .replace("addressBookId", addressBook.getId())
                    .replace("searchField", "rachit-d")))

        // Validate the response code and content type
        .andExpect(status().isNoContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));

    // Clean up

    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact2.getId());
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contact3.getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testGetContactsBySearchFieldInAllAddressBookAPISuccess() throws Exception {
    AddressBook addressBook1 =
        new AddressBook("official-10-new", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook1);

    AddressBook addressBook2 =
        new AddressBook("official-11-new", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook2);

    Contact contact1 = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact1.setId(18l);
    Contact contact2 = new Contact("dhasmana", "rachit", "12345678", "98765432");
    contact2.setId(19l);
    Contact contact3 = new Contact("some1", "random1", "12345678", "98765432");
    contact3.setId(20l);
    Contact contact4 = new Contact("some2", "random2", "12345678", "98765432");
    contact3.setId(21l);

    Contact add1contact1 = contactService.addContactInAddressBook(addressBook1.getId(), contact1);
    Contact add1contact2 = contactService.addContactInAddressBook(addressBook1.getId(), contact3);

    Contact add2contact1 = contactService.addContactInAddressBook(addressBook2.getId(), contact2);
    Contact add2contact2 = contactService.addContactInAddressBook(addressBook2.getId(), contact4);

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/contacts/search?name=searchField"
                    .replace("searchField", "rachit")))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)));

    // Clean up

    contactService.deleteContactByIdInAddressBook(addressBook1.getId(), add1contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook1.getId(), add1contact2.getId());
    contactService.deleteContactByIdInAddressBook(addressBook2.getId(), add2contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook2.getId(), add2contact2.getId());
    addressBookService.deleteAddressBookById(addressBook1.getId());
    addressBookService.deleteAddressBookById(addressBook2.getId());
  }

  @Test
  void testGetContactsBySearchFieldInAllAddressBookAPINotFound() throws Exception {
    AddressBook addressBook1 =
        new AddressBook("official-12-new", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook1);

    AddressBook addressBook2 =
        new AddressBook("official-13-new", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook2);

    Contact contact1 = new Contact("some1", "random1", "12345678", "98765432");
    contact1.setId(22l);
    Contact contact2 = new Contact("some2", "random2", "12345678", "98765432");
    contact2.setId(23l);

    Contact add1contact1 = contactService.addContactInAddressBook(addressBook1.getId(), contact1);
    Contact add2contact1 = contactService.addContactInAddressBook(addressBook2.getId(), contact2);

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/contacts/search?name=searchField"
                    .replace("searchField", "rachit")))

        // Validate the response code and content type
        .andExpect(status().isNoContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));

    // Clean up

    contactService.deleteContactByIdInAddressBook(addressBook1.getId(), add1contact1.getId());
    contactService.deleteContactByIdInAddressBook(addressBook2.getId(), add2contact1.getId());
    addressBookService.deleteAddressBookById(addressBook1.getId());
    addressBookService.deleteAddressBookById(addressBook2.getId());
  }

  static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

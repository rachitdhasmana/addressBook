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
    AddressBook addressBook = new AddressBook("official1", "off_name", "my official1 address book");
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
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.firstName", is(contact.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(contact.getLastName())))
        .andExpect(jsonPath("$.mobileNumber", is(contact.getMobileNumber())))
        .andExpect(jsonPath("$.landLine", is(contact.getLandLine())));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), 1l);
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testAddContactInAddressBookAPIFailureDuplicateContact() throws Exception {
    AddressBook addressBook = new AddressBook("official1", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(1l);
    contactService.addContactInAddressBook(addressBook.getId(), contact);

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
  }

  @Test
  void testAddContactInAddressBookAPIFailureAddressBookNotPresent() throws Exception {
    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(1l);

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
    contact.setId(2l);
    contactService.addContactInAddressBook(addressBook.getId(), contact);
    List<Contact> contactList = contactService.getAllContactsInAddressBook(addressBook.getId());

    // Execute the DELETE request
    mockMvc
        .perform(
            delete(
                "/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contactList.get(0).getId()))))

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
    contact.setId(2l);
    contactService.addContactInAddressBook(addressBook.getId(), contact);
    List<Contact> contactList = contactService.getAllContactsInAddressBook(addressBook.getId());

    // Execute the GET request
    mockMvc
        .perform(
            get(
                "/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contactList.get(0).getId()))))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(contactList.get(0).getId().intValue())))
        .andExpect(jsonPath("$.firstName", is(contact.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(contact.getLastName())))
        .andExpect(jsonPath("$.mobileNumber", is(contact.getMobileNumber())))
        .andExpect(jsonPath("$.landLine", is(contact.getLandLine())));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contactList.get(0).getId());
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
    AddressBook addressBook = new AddressBook("official1", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(1l);
    Contact updatedContact = new Contact("rachit new", "dhasmana new", "321456789", "987654123");
    updatedContact.setId(2l);
    contactService.addContactInAddressBook(addressBook.getId(), contact);
    List<Contact> contactList = contactService.getAllContactsInAddressBook(addressBook.getId());

    // Execute the PUT request
    mockMvc
        .perform(
            put("/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contactList.get(0).getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedContact)))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(contactList.get(0).getId().intValue())))
        .andExpect(jsonPath("$.firstName", is(updatedContact.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(updatedContact.getLastName())))
        .andExpect(jsonPath("$.mobileNumber", is(updatedContact.getMobileNumber())))
        .andExpect(jsonPath("$.landLine", is(updatedContact.getLandLine())));

    // Clean up
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contactList.get(0).getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  @Test
  void testUpdateContactInAddressBookAPIFailureAddressBookNotExist() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-update", "off_name", "my official1 address book");

    Contact updatedContact = new Contact("rachit new", "dhasmana new", "321456789", "987654123");
    updatedContact.setId(2l);

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
    AddressBook addressBook = new AddressBook("official1", "off_name", "my official1 address book");
    addressBookService.addNewAddressBook(addressBook);

    Contact contact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    contact.setId(1l);
    Contact updatedContact = new Contact("rachit", "dhasmana", "123456789", "987654321");
    updatedContact.setId(2l);
    contactService.addContactInAddressBook(addressBook.getId(), contact);
    List<Contact> contactList = contactService.getAllContactsInAddressBook(addressBook.getId());

    // Execute the PUT request
    mockMvc
        .perform(
            put("/v1/address-books/addressBookId/contacts/contactId"
                    .replace("addressBookId", addressBook.getId())
                    .replace("contactId", String.valueOf(contactList.get(0).getId())))
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
    contactService.deleteContactByIdInAddressBook(addressBook.getId(), contactList.get(0).getId());
    addressBookService.deleteAddressBookById(addressBook.getId());
  }

  static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

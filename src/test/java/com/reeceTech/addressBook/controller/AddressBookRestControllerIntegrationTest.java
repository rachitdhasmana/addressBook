package com.reeceTech.addressBook.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.services.AddressBookService;
import com.reeceTech.addressBook.services.ContactService;
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
        .andExpect(jsonPath("$.id", is("official-new")))
        .andExpect(jsonPath("$.name", is("off_name_new")))
        .andExpect(jsonPath("$.description", is("my new official address book")));

    // Clean up
    addressBookService.deleteAddressBookById("official-new");
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
        .andExpect(status().isBadRequest());

    // Clean up
    addressBookService.deleteAddressBookById("official");
  }

  @Test
  public void testGetAllAddressBooksAPIWhenData() throws Exception {
    AddressBook addressBook = new AddressBook("official", "off_name", "my official address book");
    addressBookService.addNewAddressBook(addressBook);

    mockMvc
        .perform(get("/v1/address-books"))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is("official")))
        .andExpect(jsonPath("$[0].name", is("off_name")))
        .andExpect(jsonPath("$[0].description", is("my official address book")));

    // Clean up
    addressBookService.deleteAddressBookById("official");
  }

  @Test
  public void testGetAllAddressBooksAPIWhenNoData() throws Exception {
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

    mockMvc
        .perform(get("/v1/address-books/official-getid"))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is("official-getid")))
        .andExpect(jsonPath("$.name", is("off_name")))
        .andExpect(jsonPath("$.description", is("my official address book")));

    // Clean up
    addressBookService.deleteAddressBookById("official-getid");
  }

  @Test
  public void testGetAddressBookByIdAPIWhenNoData() throws Exception {
    mockMvc
        .perform(get("/v1/address-books/official-no-getid"))
        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(content().string("Unable to find the Address Book."));
  }

  @Test
  public void testDeleteAddressBookByIdAPIWhenData() throws Exception {
    AddressBook addressBook =
        new AddressBook("official-del", "off_name", "my official address book");
    addressBookService.addNewAddressBook(addressBook);

    mockMvc
        .perform(delete("/v1/address-books/official-del"))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().string("AddressBook has been successfully deleted."));
  }

  @Test
  public void testDeleteAddressBookByIdAPIWhenNoData() throws Exception {
    mockMvc
        .perform(delete("/v1/address-books/official-del"))
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

    // Execute the POST request
    mockMvc
        .perform(
            put("/v1/address-books/official-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedAddressBook)))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is("official-update")))
        .andExpect(jsonPath("$.name", is("off_name_new_name")))
        .andExpect(jsonPath("$.description", is("my new desc for official address book")));

    // Clean up
    addressBookService.deleteAddressBookById("official-update");
  }

  @Test
  void testUpdateAddressBookAPIFailure() throws Exception {
    AddressBook updatedAddressBook =
        new AddressBook(
            "official-no-new", "off_name_new_name", "my new desc for official address book");

    // Execute the POST request
    mockMvc
        .perform(
            put("/v1/address-books/official-no-new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedAddressBook)))

        // Validate the response code and content type
        .andExpect(status().isNotFound())
        .andExpect(content().string("Unable to update addressBook, addressBook does not exist."));
  }

  static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

package com.reeceTech.addressBook.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

  static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

package com.reeceTech.addressBook.controller;

import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.entities.Contact;
import com.reeceTech.addressBook.services.AddressBookService;
import com.reeceTech.addressBook.services.ContactService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/address-books")
@OpenAPIDefinition
public class AddressBookRestController {

  @Autowired private ContactService contactServiceObj;

  @Autowired private AddressBookService addressBookServiceObj;

  @PostMapping(value = "", headers = "Accept=application/json")
  @Tag(
      name = "AddressBook API(s)",
      description = "List of API(s) to perform CRUD operation on AddressBook entity.")
  @Operation(
      summary = "Add New Address Book",
      description = "This API allows to add a new Address Book to the existing collection.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "400", description = "Unsuccessful, Bad Request")
      })
  public ResponseEntity addNewAddressBook(@NonNull @RequestBody final AddressBook addressBook) {
    AddressBook savedAddressBook = addressBookServiceObj.addNewAddressBook(addressBook);
    if (savedAddressBook != null) {
      return new ResponseEntity<>(savedAddressBook, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(
          "Unable to save AddressBook, Bad Request", HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value = "/{addressBookId}")
  @Tag(
      name = "AddressBook API(s)",
      description = "List of API(s) to perform CRUD operation on AddressBook entity.")
  @Operation(
      summary = "Delete An Existing Address Book Using Address Book Id",
      description =
          "This API allows to delete an already existing Address Book, only if it is empty.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "404", description = "Unsuccessful, Record Not Found")
      })
  public ResponseEntity deleteAddressBookById(@PathVariable final String addressBookId) {
    boolean isDeleted = addressBookServiceObj.deleteAddressBookById(addressBookId);
    if (isDeleted) {
      return new ResponseEntity<>("AddressBook has been successfully deleted.", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Unable to delete AddressBook, Not Found.", HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping(value = "")
  @Tag(
      name = "AddressBook API(s)",
      description = "List of API(s) to perform CRUD operation on AddressBook entity.")
  @Operation(
      summary = "Get List Of All Address Books",
      description =
          "This API fetches a list of existing address books from the collection, sorted(ASC) by id.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "204", description = "No Content (empty list) to return")
      })
  public ResponseEntity getAllAddressBooks() {
    List<AddressBook> addressBooks = addressBookServiceObj.getAllAddressBooks();
    if (addressBooks.size() > 0) {
      return new ResponseEntity<>(addressBooks, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(addressBooks, HttpStatus.NO_CONTENT);
    }
  }

  @GetMapping(value = "/{addressBookId}")
  @Tag(
      name = "AddressBook API(s)",
      description = "List of API(s) to perform CRUD operation on AddressBook entity.")
  @Operation(
      summary = "Get An Address Book Using Address Book Id",
      description = "This API fetches an existing address book from the collection by id.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "404", description = "Unsuccessful, Record Not Found")
      })
  public ResponseEntity getAddressBookById(@PathVariable final String addressBookId) {
    AddressBook addressBook = addressBookServiceObj.getAddressBookById(addressBookId);
    if (addressBook != null) {
      return new ResponseEntity<>(addressBook, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Unable to find the Address Book.", HttpStatus.NOT_FOUND);
    }
  }

  @PutMapping(value = "/{addressBookId}", headers = "Accept=application/json")
  @Tag(
      name = "AddressBook API(s)",
      description = "List of API(s) to perform CRUD operation on AddressBook entity.")
  @Operation(
      summary = "Update An Address Book Using Address Book Id",
      description = "This API allows to update any details for an existing Address Book entity.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "404", description = "Unsuccessful, Record Not Found")
      })
  public ResponseEntity updateAddressBook(
      @PathVariable final String addressBookId,
      @NonNull @RequestBody final AddressBook addressBook) {
    AddressBook updatedAddressBook =
        addressBookServiceObj.updateAddressBook(addressBookId, addressBook);
    if (updatedAddressBook != null) {
      return new ResponseEntity<>(updatedAddressBook, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(
          "Unable to update addressBook, addressBook does not exist.", HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping(value = "/{addressBookId}/contacts", headers = "Accept=application/json")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Add A Contact To An Address Book",
      description = "This API allows to add a new contact to an existing Address Book.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(
            responseCode = "400",
            description = "Unsuccessful, violates uniqueness or foreign key constraint.")
      })
  public ResponseEntity addContactInAddressBook(
      @PathVariable @NonNull final String addressBookId,
      @NonNull @RequestBody final Contact contact) {
    Contact addedContact = contactServiceObj.addContactInAddressBook(addressBookId, contact);
    if (addedContact != null) {
      return new ResponseEntity<>(addedContact, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(
          "Unable to add new contact, violating either uniqueness (duplicate) or "
              + "foreign key (missing addressBook) constraint.",
          HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value = "/{addressBookId}/contacts/{contactId}")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Delete A Contact From An Address Book Using Contact Id",
      description =
          "This API allows to delete a contact (if exists) from an existing Address Book.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "404", description = "Unsuccessful, Record Not Found")
      })
  public ResponseEntity deleteContactByIdInAddressBook(
      @PathVariable final String addressBookId, @PathVariable final Long contactId) {
    boolean isDeleted = contactServiceObj.deleteContactByIdInAddressBook(addressBookId, contactId);
    if (isDeleted) {
      return new ResponseEntity<>("Contact has been successfully deleted.", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Unable to delete Contact, Not Found.", HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping(value = "/{addressBookId}/contacts/{contactId}")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Get A Contact From An Address Book Using Contact Id",
      description = "This API fetches specific contact (if exists) from an existing Address Book.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "404", description = "Unsuccessful, Record Not Found")
      })
  public ResponseEntity getContactByIdInAddressBook(
      @PathVariable final String addressBookId, @PathVariable final Long contactId) {
    Contact contact = contactServiceObj.getContactByIdInAddressBook(addressBookId, contactId);
    if (contact != null) {
      return new ResponseEntity<>(contact, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Unable to find a contact in AddressBook", HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping(value = "/{addressBookId}/contacts")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Get A List Of All Contacts From An Address Book",
      description = "This API fetches list of all contacts from an existing Address Book.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "204", description = "No Content (empty list) to return")
      })
  public ResponseEntity getAllContactsInAddressBook(@PathVariable final String addressBookId) {
    List<Contact> contacts = this.contactServiceObj.getAllContactsInAddressBook(addressBookId);
    if (contacts.size() > 0) {
      return new ResponseEntity<>(contacts, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(contacts, HttpStatus.NO_CONTENT);
    }
  }

  @GetMapping(value = "/contacts")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Get All unique Contacts Across All Address Books",
      description = "This API fetches only unique contacts from all existing Address Books.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "204", description = "No Content (empty list) to return")
      })
  public ResponseEntity getUniqueContactsInAllAddressBooks() {
    List<Contact> contacts = contactServiceObj.getUniqueContactsInAllAddressBooks();
    if (contacts.size() > 0) {
      return new ResponseEntity<>(contacts, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(contacts, HttpStatus.NO_CONTENT);
    }
  }

  @GetMapping(value = "/{addressBookId}/contacts/search")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Search Contacts By Either First or Last Name In An Address Book",
      description =
          "This API fetches all contacts with first name or last name matching "
              + "to the search field from an Address Book.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "204", description = "No Content (empty list) to return")
      })
  public ResponseEntity getContactsBySearchFieldInOneAddressBook(
      @PathVariable final String addressBookId, @RequestParam(value = "name") final String name) {
    List<Contact> contacts =
        contactServiceObj.getContactsBySearchFieldInOneAddressBook(addressBookId, name);
    if (contacts.size() > 0) {
      return new ResponseEntity<>(contacts, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(contacts, HttpStatus.NO_CONTENT);
    }
  }

  @GetMapping(value = "/contacts/search")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Search Contacts By Either First or Last Name In All Address Book",
      description =
          "This API fetches all contacts with first name or last name matching "
              + "to the search field from all Address Books.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "204", description = "No Content (empty list) to return")
      })
  public ResponseEntity getContactsBySearchFieldInAllAddressBooks(
      @RequestParam("name") final String name) {
    List<Contact> contacts = contactServiceObj.getContactsBySearchFieldInAllAddressBooks(name);
    if (contacts.size() > 0) {
      return new ResponseEntity<>(contacts, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(contacts, HttpStatus.NO_CONTENT);
    }
  }

  @PutMapping(value = "/{addressBookId}/contacts/{contactId}", headers = "Accept=application/json")
  @Tag(
      name = "Contacts API(s)",
      description = "List of API(s) to perform CRUD operation on Contact entity.")
  @Operation(
      summary = "Update A Contact In An Address Book Using Contact Id",
      description =
          "This API allows to update any field in a Contact (if exists) in an Address Book.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "404", description = "Unsuccessful, Record Not Found")
      })
  public ResponseEntity updateContactInAddressBook(
      @PathVariable final String addressBookId,
      @PathVariable final Long contactId,
      @NonNull @RequestBody final Contact contact) {
    Contact updatedContact =
        contactServiceObj.updateContactInAddressBook(addressBookId, contactId, contact);
    if (updatedContact != null) {
      return new ResponseEntity<>(updatedContact, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(
          "Unable to update contact, contact / addressBook does not exist or violates uniqueness property.",
          HttpStatus.NOT_FOUND);
    }
  }
}

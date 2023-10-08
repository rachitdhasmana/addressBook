package com.reeceTech.addressBook.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.reeceTech.addressBook.dao.AddressBookRepository;
import com.reeceTech.addressBook.entities.AddressBook;
import com.reeceTech.addressBook.services.impl.AddressBookServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class AddressBookServiceTest {

  @Mock private AddressBookRepository addressBookRepository;

  private AddressBookService addressBookService;

  @BeforeEach
  void setUp() {
    addressBookService = new AddressBookServiceImpl(addressBookRepository);
  }

  @Test
  void testGetAllAddressBooksWhenData() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    List<AddressBook> addressBooksMock = new ArrayList<>();
    addressBooksMock.add(addressBookMock);
    Mockito.when(addressBookRepository.findAll(Sort.by(Sort.Direction.ASC, "id")))
        .thenReturn(addressBooksMock);

    List<AddressBook> addresseBooks = addressBookService.getAllAddressBooks();
    assertThat(addresseBooks.size()).isGreaterThan(0);
  }

  @Test
  void testGetAllAddressBooksWhenNoData() {
    Mockito.when(addressBookRepository.findAll(Sort.by(Sort.Direction.ASC, "id")))
        .thenReturn(new ArrayList<>());

    List<AddressBook> addresseBooks = addressBookService.getAllAddressBooks();
    assertThat(addresseBooks.size()).isEqualTo(0);
  }

  @Test
  void testGetAllAddressBooksWhenExceptionThrown() {
    Mockito.when(addressBookRepository.findAll(Sort.by(Sort.Direction.ASC, "id")))
        .thenThrow(new IllegalArgumentException());

    List<AddressBook> addresseBooks = addressBookService.getAllAddressBooks();
    assertThat(addresseBooks.size()).isEqualTo(0);
  }

  @Test
  void testGetAddressBookByIdWhenPresent() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    Mockito.when(addressBookRepository.findById("official"))
        .thenReturn(Optional.of(addressBookMock));

    AddressBook addressBook = addressBookService.getAddressBookById("official");
    assertThat(addressBook).isNotNull();
  }

  @Test
  void testGetAddressBookByIdWhenAbsent() {
    Mockito.when(addressBookRepository.findById("personal")).thenReturn(Optional.empty());

    AddressBook addressBook = addressBookService.getAddressBookById("personal");
    assertThat(addressBook).isNull();
  }

  @Test
  void testGetAddressBookByIdWhenExceptionThrown() {
    Mockito.when(addressBookRepository.findById("personal"))
        .thenThrow(new IllegalArgumentException());

    AddressBook addressBook = addressBookService.getAddressBookById("personal");
    assertThat(addressBook).isNull();
  }

  @Test
  void testDeleteAddressBookByIdWhenPresent() {
    Mockito.when(addressBookRepository.existsById("official")).thenReturn(true);
    boolean deleted = addressBookService.deleteAddressBookById("official");
    assertThat(deleted).isTrue();
  }

  @Test
  void testDeleteAddressBookByIdWhenAbsent() {
    Mockito.when(addressBookRepository.existsById("official")).thenReturn(false);
    boolean deleted = addressBookService.deleteAddressBookById("official");
    assertThat(deleted).isFalse();
  }

  @Test
  void testDeleteAddressBookByIdWhenExceptionThrown() {
    Mockito.when(addressBookRepository.existsById("official"))
        .thenThrow(new IllegalArgumentException());
    boolean deleted = addressBookService.deleteAddressBookById("official");
    assertThat(deleted).isFalse();
  }

  @Test
  void testAddNewAddressBookWhenSuccess() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    Mockito.when(addressBookRepository.save(addressBookMock)).thenReturn(addressBookMock);

    AddressBook addressBook = addressBookService.addNewAddressBook(addressBookMock);
    assertThat(addressBook).isNotNull();
  }

  @Test
  void testAddNewAddressBookWhenFailure() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    Mockito.when(addressBookRepository.save(addressBookMock))
        .thenThrow(new IllegalArgumentException());

    AddressBook addressBook = addressBookService.addNewAddressBook(addressBookMock);
    assertThat(addressBook).isNull();
  }

  @Test
  void testAddNewAddressBookWhenRecordAlreadyExists() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    Mockito.when(addressBookRepository.existsById(addressBookMock.getId())).thenReturn(true);

    AddressBook addressBook = addressBookService.addNewAddressBook(addressBookMock);
    assertThat(addressBook).isNull();
  }

  @Test
  void testUpdateAddressBookWhenSuccess() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    Mockito.when(addressBookRepository.findById("official"))
        .thenReturn(Optional.of(addressBookMock));

    AddressBook addressBook = addressBookService.updateAddressBook("official", addressBookMock);
    assertThat(addressBook).isNotNull();
  }

  @Test
  void testUpdateAddressBookWhenFailure() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    Mockito.when(addressBookRepository.findById("official")).thenReturn(Optional.empty());

    AddressBook addressBook = addressBookService.updateAddressBook("official", addressBookMock);
    assertThat(addressBook).isNull();
  }

  @Test
  void testUpdateAddressBookWhenExceptionThrown() {
    AddressBook addressBookMock =
        new AddressBook("official", "off_name", "my official address book");
    Mockito.when(addressBookRepository.findById("official"))
        .thenThrow(new IllegalArgumentException());

    AddressBook addressBook = addressBookService.updateAddressBook("official", addressBookMock);
    assertThat(addressBook).isNull();
  }
}

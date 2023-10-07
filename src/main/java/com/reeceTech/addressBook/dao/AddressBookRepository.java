package com.reeceTech.addressBook.dao;

import com.reeceTech.addressBook.entities.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressBookRepository extends JpaRepository<AddressBook, String> {}

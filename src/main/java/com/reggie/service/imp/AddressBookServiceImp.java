package com.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.AddressBook;
import com.reggie.mapper.AddressBookMapper;
import com.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImp extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}

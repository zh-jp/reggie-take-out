package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.AddressBook;
import com.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        Long usrId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(usrId != null, AddressBook::getUserId, usrId);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    @PutMapping("/default")
    @Transactional
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        Long usrId = BaseContext.getCurrentId();
        Long id = addressBook.getId();
        log.info("用户id为{}", usrId);
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(usrId != null, AddressBook::getUserId, usrId);
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);
        updateWrapper.clear();
        updateWrapper.setEntityClass(AddressBook.class);
        updateWrapper.eq(id != null, AddressBook::getId, id);
        updateWrapper.set(AddressBook::getIsDefault, 1);
        addressBookService.update(updateWrapper);
        return R.success(addressBook);
    }

    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        Long usrId = BaseContext.getCurrentId();
        addressBook.setUserId(usrId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids) {
        for (Long id : ids) {
            addressBookService.removeById(id);
        }
        return R.success("删除成功！");
    }
}

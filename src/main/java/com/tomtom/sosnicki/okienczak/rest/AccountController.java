package com.tomtom.sosnicki.okienczak.rest;

import com.tomtom.sosnicki.okienczak.rest.dto.AccountDto;
import com.tomtom.sosnicki.okienczak.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/rest")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/account/{username}", method = RequestMethod.POST)
    public AccountDto getAccountWithoutAuthorities(@PathVariable String username) {
        return accountService.getAccountWithoutAuthorities(username);
    }

    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public void registerAccount(@Valid @RequestBody AccountDto accountDto) {
        accountService.addAccountWithDefaultAuthorities(accountDto);
    }

    @RequestMapping(path = "/admin/account/{username}", method = RequestMethod.POST)
    public AccountDto getAccountWithAuthorities(@PathVariable String username) {
        return accountService.getAccountWithAuthorities(username);
    }

    @RequestMapping(path = "/admin/account", method = RequestMethod.POST)
    public void createAccount(@Valid @RequestBody AccountDto accountDto) {
        accountService.addAccountWithSetAuthorities(accountDto);
    }

    @RequestMapping(path = "/admin/account/{username}", method = RequestMethod.PUT)
    public void updateAccount(@PathVariable String username, @Valid @RequestBody AccountDto accountDto) {
        accountService.updateAccountWithAuthorities(username, accountDto);
    }

    @RequestMapping(path = "/admin/account/{username}", method = RequestMethod.DELETE)
    public void deleteAccount(@PathVariable String username) {
        accountService.deleteAccount(username);
    }
}

package com.stepguide.backend.domain.favoriteAccount.controller;

import com.stepguide.backend.domain.favoriteAccount.dto.FavoriteAccountDTO;
import com.stepguide.backend.domain.favoriteAccount.service.FavoriteAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-account")
@RequiredArgsConstructor
public class FavoriteAccountController {
    private final FavoriteAccountService favorite;

    @GetMapping
    public List<FavoriteAccountDTO> getFavoriteAccounts(@RequestParam long userId) {
        return favorite.getFavoriteAccountList(userId);
    }

    @PostMapping
    public void addFavoriteAccount(@RequestBody FavoriteAccountDTO favoriteAccount) {
        favorite.addFavoriteAccount(favoriteAccount);
    }
    @DeleteMapping("/{favoriteId}")
    public void delete(@PathVariable Long favoriteId) {
        favorite.delete(favoriteId);
    }
}

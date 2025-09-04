package com.stepguide.backend.domain.favoriteAccount.service;

import com.stepguide.backend.domain.favoriteAccount.dto.FavoriteAccountDTO;

import java.util.List;

public interface FavoriteAccountService {

    public List<FavoriteAccountDTO> getFavoriteAccountList(long userId);// 즐겨찾기 게좌 조회

    public void addFavoriteAccount(FavoriteAccountDTO favoriteAccount); // 즐겨찾기 계좌 추가

    public boolean delete(Long favoriteId);

}
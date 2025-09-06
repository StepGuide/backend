package com.stepguide.backend.domain.favoriteAccount.mapper;


import com.stepguide.backend.domain.favoriteAccount.entity.FavoriteAccountVO;

import java.util.List;

public interface FavoriteAccountMapper {

    List<FavoriteAccountVO> getFavoriteAccounts(long userId);

    void addFavoriteAccount(FavoriteAccountVO favoriteAccount);

    public int delete(long favoriteId);

}

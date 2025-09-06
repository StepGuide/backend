package com.stepguide.backend.domain.favoriteAccount.service;

import com.stepguide.backend.domain.favoriteAccount.dto.FavoriteAccountDTO;
import com.stepguide.backend.domain.favoriteAccount.entity.FavoriteAccountVO;
import com.stepguide.backend.domain.favoriteAccount.mapper.FavoriteAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class FavoriteAccountServiceImpl implements FavoriteAccountService {

    private final FavoriteAccountMapper mapper;

    @Override
    public List<FavoriteAccountDTO> getFavoriteAccountList(long userId) {
        List<FavoriteAccountVO> voList = mapper.getFavoriteAccounts(userId);
        return voList.stream()
                .map(FavoriteAccountDTO::of)
                .toList();
    }

    @Override
    public void addFavoriteAccount(FavoriteAccountDTO favoriteAccount) {
        FavoriteAccountVO vo = favoriteAccount.toVo();
        mapper.addFavoriteAccount(vo);
        favoriteAccount.setUserId(vo.getUserId());
    }

    @Override
    public boolean delete(Long favoriteId) {
        return mapper.delete(favoriteId)==1;
    }

}


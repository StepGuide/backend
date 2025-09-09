package com.stepguide.backend.domain.user.controller;

import com.stepguide.backend.domain.user.service.CustomUser;
import com.stepguide.backend.domain.user.service.UserService;
import com.stepguide.backend.global.response.BaseResponse;
import com.stepguide.backend.global.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userProfileService;

    @GetMapping
    public BaseResponse<MeResponse> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUser cu)) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        var me = userProfileService.getMe(cu.getUserId());
        return new BaseResponse<>(new MeResponse(
                me.getUserId(), me.getUsername(), me.getPhoneNumber(), me.getGuardianPhone()
        ));
    }

    @PutMapping("/guardian-phone")
    public BaseResponse<Void> saveGuardianPhone(@RequestBody SaveGuardianPhoneRequest req) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUser cu)) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST_INVALID_PARAM);
        }
        userProfileService.saveGuardianPhone(cu.getUserId(), req.getPhone());
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @Getter
    @AllArgsConstructor
    public static class MeResponse {
        private Long userId;
        private String username;
        private String phoneNumber;
        private String guardianPhone;
    }

    @Getter @Setter
    public static class SaveGuardianPhoneRequest {
        private String phone;
    }
}
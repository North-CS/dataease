package io.dataease.sso.controller;

import io.dataease.sso.service.SSOService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sso")
public class SSOController {

    @Autowired
    private SSOService ssoService;

    @PostMapping("/login")
    public Map<String, Object> ssoLogin(@RequestBody Map<String, String> params,
                                        HttpServletRequest request,HttpServletResponse response //
    ) {
        String ticket = params.get("ticket");
        System.out.println(ticket);
        return ssoService.ssoLogin(ticket, request, response);//
    }

    @PostMapping("/verify")
    public Map<String, Object> verifyTicket(@RequestBody Map<String, String> params,
                                            HttpServletRequest request) {
        String ticket = params.get("ticket");
        return ssoService.verifyTicket(ticket, request);
    }

    @PostMapping("/logout")
    public Map<String, Object> ssoLogout(@RequestBody Map<String, String> params,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String ticket = params.get("ticket");
        System.out.println(ticket);
        return ssoService.ssoLogout(ticket, request, response);
    }
}

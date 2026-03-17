package io.dataease.sso.controller;

import io.dataease.sso.service.SSOService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sso")
public class SSOController {

    @Autowired
    private SSOService ssoService;

    @PostMapping("/login")
    public Map<String, Object> ssoLogin(@RequestBody Map<String, String> params,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        String ticket = params.get("ticket");
        return ssoService.ssoLogin(ticket, request, response);
    }

    @PostMapping("/verify")
    public Map<String, Object> verifyTicket(@RequestBody Map<String, String> params,
                                            HttpServletRequest request) {
        String ticket = params.get("ticket");
        return ssoService.verifyTicket(ticket, request);
    }

    @GetMapping("/logout")
    public Map<String, Object> ssoLogout(HttpServletRequest request,
                                         HttpServletResponse response) {
        return ssoService.ssoLogout(request, response);
    }
}

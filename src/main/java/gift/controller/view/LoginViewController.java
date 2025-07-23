package gift.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginViewController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @GetMapping("/login")
    public ModelAndView loginPage() {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("clientId", clientId);
        mav.addObject("redirectUri", redirectUri);
        return mav;
    }
}
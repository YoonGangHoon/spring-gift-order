package gift.controller.view;

import gift.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/members")
public class MemberViewController {

    private final MemberService memberService;
    public MemberViewController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ModelAndView list() {
        Map<String, Object> model = new HashMap<>();
        model.put("members", memberService.findAll());
        return new ModelAndView("member/list", model);
    }

    @GetMapping("/{id}")
    public ModelAndView detail(@PathVariable Long id) {
        Map<String, Object> model = new HashMap<>();
        model.put("member", memberService.find(id));
        return new ModelAndView("member/detail", model);
    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable Long id) {
        memberService.delete(id);
        return new ModelAndView("redirect:/admin/members");
    }

}

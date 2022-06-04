package com.remember.core.controllers.users;

import com.remember.core.assemblers.AlgorithmsAssembler;
import com.remember.core.assemblers.PlatformsAssembler;
import com.remember.core.assemblers.PracticeStatususAssembler;
import com.remember.core.requestDtos.QuestionsRO;
import com.remember.core.searchParams.users.QuestionsParams;
import com.remember.core.services.AlgorithmsService;
import com.remember.core.services.PlatformsService;
import com.remember.core.services.PracticeStatususService;

import com.remember.core.services.users.UsersMeQuestionsService;
import com.remember.core.tools.LinkBuilder;

import com.remember.core.responseDtos.AlgorithmVO;
import com.remember.core.responseDtos.PlatformVO;
import com.remember.core.responseDtos.PracticeStatusVO;
import com.remember.core.responseDtos.question.QuestionVO;
import com.remember.core.responseDtos.question.QuestionsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 참고자료
 * https://bulnabang99.tistory.com/59
 */
@Controller
//@PreAuthorize("@userQuestionsAuthorizer.check(#userId, authentication)")
@RequiredArgsConstructor
@RequestMapping("/users/me")
public class UsersMeQuestionsController {
    private final UsersMeQuestionsService service;
    private final PracticeStatususService practiceStatususService;
    private final PlatformsService platformsService;
    private final AlgorithmsService algorithmsService;

    private final LinkBuilder linkBuilder;
    private final String RESOURCES = "users/me/questions";

    /*
     * SSR views
     */
    @GetMapping("/questions")
    public String findAll(Pageable pageable,
                          HttpServletRequest request,
                          @ModelAttribute QuestionsParams params,
                          Model model) {

        PagedModel<QuestionsVO> questions = service.findAll(pageable, params);
        CollectionModel<PracticeStatusVO> practiceStatusus = practiceStatususService.findAll();

        /*
         * modeling
         */
        model.addAttribute("search_status",
                params.getPracticeStatus() == null ? 0L : params.getPracticeStatus());
        model.addAttribute("search_input", params.getTitle() == null ? "" : params.getTitle());
        model.addAttribute("questions_url", request.getRequestURL());
        model.addAttribute("questions", questions);
        model.addAttribute("practiceStatusus", practiceStatusus.getContent());
        return "users/questions/list";
    }

    @GetMapping("/questions/{id}")
    public String findById(
            Model model, @PathVariable Long id) {
        QuestionVO question = service.findById(id);

        model.addAttribute("question", question);
        return "users/questions/detail";
    }

    @PostMapping(value = "/questions", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String create(@ModelAttribute QuestionsRO ro) {
        service.create(ro);

        return "redirect:questions";
    }

    @PutMapping(value = "/questions/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String update(@PathVariable Long id, @ModelAttribute QuestionsRO ro) {
        service.update(id, ro);

        return "redirect:/users/me/questions/" + id;
    }

    @ResponseBody
    @PatchMapping(
            value = "/questions/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public QuestionVO partial_update(@PathVariable Long id, @RequestBody QuestionsRO ro) {
        return service.partial_update(id, ro);
    }

    @DeleteMapping(value = "/questions/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String delete(@PathVariable Long id) {
        service.delete(id);

        return "redirect:";
    }

    @GetMapping("/questions/forms/create")
    public String createView( Model model) {
        CollectionModel<PlatformVO> platforms = platformsService.findAll();
        CollectionModel<PracticeStatusVO> practiceStatusus = practiceStatususService.findAll();
        CollectionModel<AlgorithmVO> algorithms = algorithmsService.findAll();
        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();

        String create_link = linkBuilder.getListLink(baseUri, RESOURCES).getHref();
        model.addAttribute("create_link", create_link);
        model.addAttribute("platforms", platforms);
        model.addAttribute("practiceStatusus", practiceStatusus);
        model.addAttribute("algorithms", algorithms.getContent());
        return "users/questions/forms/create";
    }

    @GetMapping("/questions/{id}/forms/update")
    public String updateView(@PathVariable Long id, Model model) {
        QuestionVO question = service.findById(id);
        CollectionModel<PlatformVO> platforms = platformsService.findAll();
        CollectionModel<PracticeStatusVO> practiceStatusus = practiceStatususService.findAll();
        CollectionModel<AlgorithmVO> algorithms = algorithmsService.findAll();
        Set<Long> curAlgorithms = question.getAlgorithms().stream().map(a -> a.getId()).collect(Collectors.toSet());

        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();
        model.addAttribute("algorithms", algorithms.getContent());
        model.addAttribute("curAlgorithms", curAlgorithms);
        model.addAttribute("platforms", platforms);
        model.addAttribute("practiceStatusus", practiceStatusus);
        model.addAttribute("question", question);
        return "users/questions/forms/update";
    }

    @GetMapping("/questions/{id}/forms/delete")
    public String deleteView(@PathVariable Long id, Model model) {
        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();
        String delete_link = linkBuilder.getDetailLink(baseUri, RESOURCES, id).getHref();

        model.addAttribute("delete_link", delete_link);
        return "users/questions/forms/delete";
    }
}
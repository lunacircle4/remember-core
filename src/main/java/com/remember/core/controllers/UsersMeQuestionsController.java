package com.remember.core.controllers;

import com.remember.core.exceptions.AuthorizationException;
import com.remember.core.requests.QuestionRequestDto;
import com.remember.core.responses.PracticeStatusResponseDto;
import com.remember.core.searchParams.QuestionParams;
import com.remember.core.services.AlgorithmsService;
import com.remember.core.services.PlatformsService;
import com.remember.core.services.PracticeStatususService;
import com.remember.core.services.UsersMeQuestionsService;

import com.remember.core.responses.AlgorithmResponseDto;
import com.remember.core.responses.PlatformResponseDto;
import com.remember.core.responses.question.QuestionResponseDto;
import com.remember.core.responses.question.QuestionListResponseDto;
import com.remember.core.utils.ServerContext;
import com.remember.core.utils.linkBuilders.LinkBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 참고자료
 * https://bulnabang99.tistory.com/59
 * https://stackoverflow.com/questions/16394296/in-which-layer-should-validation-be-performed
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/users/me/questions")
public class UsersMeQuestionsController {
    private final UsersMeQuestionsService service;

    // for models
    private final PracticeStatususService practiceStatususService;
    private final PlatformsService platformsService;
    private final AlgorithmsService algorithmsService;
    private final ServerContext context;
    private final String ALGORITHMS = "algorithms";

    @GetMapping
    public String findAll(Pageable pageable,
                          HttpServletRequest request,
                          @ModelAttribute QuestionParams params,
                          Model model) {
        PagedModel<QuestionListResponseDto> questions = service.findAll(pageable, params);

        /*
         * modeling
         */
        model.addAttribute("questions", questions);
        model.addAttribute("platforms", platformsService.findAll());
        model.addAttribute("practiceStatusus", practiceStatususService.findAll());

        model.addAttribute("search_status",
                params.getPracticeStatus() == null ? 0L : params.getPracticeStatus());
        model.addAttribute("search_input", params.getTitle() == null ? "" : params.getTitle());

        model.addAttribute("questions_url", request.getRequestURL());
        model.addAttribute("algorithms_url", LinkBuilder
                .getListLink(context.getRoot(), ALGORITHMS).getHref());
        return "users/questions/list";
    }

    @GetMapping("/{id}")
    public String findById(
            Model model, @PathVariable Long id) {
        QuestionResponseDto question = service.findById(id);

        /*
         * modeling
         */
        model.addAttribute("question", question);
        model.addAttribute("practiceStatusus", practiceStatususService.findAll());
        return "users/questions/detail";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String create(@ModelAttribute @Validated QuestionRequestDto ro) {
        service.create(ro);
        return "redirect:questions";
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String update(@PathVariable Long id, @ModelAttribute @Validated QuestionRequestDto ro) {
        service.update(id, ro);
        return "redirect:/users/me/questions/" + id;
    }

    @ResponseBody
    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public QuestionResponseDto partial_update(@PathVariable Long id, @RequestBody QuestionRequestDto ro) {
        return service.partial_update(id, ro);
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String delete(@PathVariable Long id) {
        service.delete(id);

        return "redirect:";
    }

    /*
     * update form
     */
    @GetMapping("/{id}/forms/update")
    public String updateView(@PathVariable Long id, Model model) {
        QuestionResponseDto question = service.findById(id);
        CollectionModel<PlatformResponseDto> platforms = platformsService.findAll();
        List<PracticeStatusResponseDto> practiceStatusus = practiceStatususService.findAll();
        CollectionModel<AlgorithmResponseDto> algorithms = algorithmsService.findAll();

        /*
         * modeling
         */
        Set<Long> curAlgorithms = question.getAlgorithms().stream().map(a -> a.getId()).collect(Collectors.toSet());
        model.addAttribute("curAlgorithms", curAlgorithms);
        model.addAttribute("algorithms", algorithms.getContent());
        model.addAttribute("platforms", platforms);
        model.addAttribute("practiceStatusus", practiceStatusus);
        model.addAttribute("question", question);
        return "users/questions/forms/update";
    }

    /*
     * exception handlers
     */
    @ExceptionHandler
    public ModelAndView entityNotFound(EntityNotFoundException e) {
        return new ModelAndView("redirect:");
    }

    @ExceptionHandler
    public ModelAndView unAuthorized(AuthorizationException e) {
        return new ModelAndView("redirect:");
    }
}
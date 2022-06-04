package com.remember.core.assemblers.user;

import com.remember.core.domains.Question;
import com.remember.core.tools.LinkBuilder;
import com.remember.core.tools.ServerContext;
import com.remember.core.responseDtos.question.QuestionVO;
import com.remember.core.responseDtos.question.QuestionPlatformVO;
import com.remember.core.responseDtos.question.QuestionPracticeStatusVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class UsersMeQuestionAssembler implements RepresentationModelAssembler<Question, QuestionVO> {
    @Autowired
    private ServerContext serverContext;

    @Autowired
    private LinkBuilder builder;

    private final String RESOURCES = "users/me/questions";
    private final String PLATFORM_RESOURCES = "platforms";
    private final String PRACTICE_STATUS_RESOURCES = "practiceStatusus";

    @Override
    public QuestionVO toModel(Question question) {
        QuestionVO vo = new QuestionVO(question);
        vo = addSelfLink(vo);
        vo = practiceStatusAssemble(vo);
        vo = platformAssemble(vo);
        return vo;
    }

    /**
     * helpers
     */
    private QuestionVO addSelfLink(QuestionVO vo) {
        StringBuilder url = new StringBuilder();
        String root = serverContext.getRoot();

        url.append(root); url.append("/");
        url.append(RESOURCES); url.append("/"); url.append(vo.getId());

        vo.add(Link.of(url.toString()).withSelfRel());

        return vo;
    }

    private QuestionVO practiceStatusAssemble(QuestionVO vo) {
        String baseUrl = serverContext.getRoot();
        QuestionPracticeStatusVO ps = vo.getPracticeStatus();
        ps.add(builder.getDetailLink(baseUrl, PRACTICE_STATUS_RESOURCES, ps.getId()).withSelfRel());
        return vo;
    }

    private QuestionVO platformAssemble(QuestionVO vo) {
        String baseUrl = serverContext.getRoot();
        QuestionPlatformVO p = vo.getPlatform();
        p.add(builder.getDetailLink(baseUrl, PLATFORM_RESOURCES, p.getId()).withSelfRel());
        return vo;
    }
}
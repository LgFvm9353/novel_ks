package org.example.novel_backened.vote;

import org.example.novel_backened.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/novels/{novelId}/vote")
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ApiResponse<Void> vote(@PathVariable("novelId") String novelId,
                                  @RequestAttribute("userId") String userId) {
        return voteService.vote(novelId, userId);
    }

    @GetMapping("/status")
    public ApiResponse<Boolean> checkVoteStatus(@PathVariable("novelId") String novelId,
                                                @RequestAttribute("userId") String userId) {
        return voteService.checkVoteStatus(novelId, userId);
    }
}


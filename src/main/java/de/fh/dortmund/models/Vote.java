package de.fh.dortmund.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.javafaker.Faker;
import de.fh.dortmund.models.enums.VoteType;
import lombok.Data;

import java.util.Random;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote {
    private String userId;

    private String postId;

    private VoteType voteType;


    public static Vote createPostVoteInstance(Faker faker, Random random, String userId, String questionId) {
        VoteType[] voteOptions = {VoteType.DOWNVOTE, VoteType.UPVOTE};

        int randomVoteOption = random.nextInt(voteOptions.length);
        VoteType voteOption = voteOptions[randomVoteOption];

        Vote vote = new Vote();
        vote.setUserId(userId);
        vote.setVoteType(voteOption);
        vote.setPostId(questionId);
        return vote;
    }

    public boolean isUpvote() {
        return this.voteType == VoteType.UPVOTE;
    }
    public boolean isDownVote() {
        return this.voteType == VoteType.DOWNVOTE;
    }
}
package com.wellcome.main.entity.user

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Rating {

    @Column(name = "like_count")
    var likeCount: Long = 0

    @Column(name = "comment_count")
    var commentCount: Long = 0

    @Column(name = "get_in_count")
    var getInCount: Long = 0

    @Column(name = "post_count")
    var postCount: Long = 0

    @Column(name = "confirm_get_in_count")
    var confirmedGetInCount: Long = 0

    @Column(name = "shared_application_count")
    var sharedApplicationCount: Long = 0
}

enum class RatingCoefficient(val coefficient: Int) {
    LIKE(1),
    COMMENT(3),
    GET_IN(7),
    POST(15),
    CONFIRMED_GET_IN(30),
    SHARED_APPLICATION(10)
}

fun calculateRating(rating: Rating): Long {
    var totalRating: Long = 0
    totalRating += rating.likeCount * RatingCoefficient.LIKE.coefficient.toLong()
    totalRating += rating.commentCount * RatingCoefficient.COMMENT.coefficient.toLong()
    totalRating += rating.getInCount * RatingCoefficient.GET_IN.coefficient.toLong()
    totalRating += rating.postCount * RatingCoefficient.POST.coefficient.toLong()
    totalRating += rating.confirmedGetInCount * RatingCoefficient.CONFIRMED_GET_IN.coefficient.toLong()
    totalRating += rating.sharedApplicationCount * RatingCoefficient.SHARED_APPLICATION.coefficient.toLong()
    return totalRating
}
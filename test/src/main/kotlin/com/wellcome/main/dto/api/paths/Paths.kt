package com.wellcome.main.dto.api.paths

object CommonPaths {
    const val API = "/api/new"
}

object PathsV1 {
    private const val APPLICATION_VERSION = "${CommonPaths.API}/v1"

    const val BASE_USER = "$APPLICATION_VERSION/user"
    const val BASE_INSTITUTION = "$APPLICATION_VERSION/institution"
    const val BASE_SEARCH = "$APPLICATION_VERSION/search"
    const val BASE_OFFER = "$APPLICATION_VERSION/offer"
    const val BASE_EVENT = "$APPLICATION_VERSION/event"
    const val BASE_REVIEW = "$APPLICATION_VERSION/review"
    const val BASE_STORY = "$APPLICATION_VERSION/story"
    const val BASE_USER_STORY = "$APPLICATION_VERSION/userStory"
    const val BASE_FEED = "$APPLICATION_VERSION/feed"
    const val BASE_BIRTHDAY_CAMPAIGN_USER = "$APPLICATION_VERSION/birthdayCampaignUser"

    object User {
        const val SAVE = "/init"
        const val GET_PROFILE = "/getProfile"
        const val CHECK_SESSION = "/checkSession"
        const val SAVE_INSTITUTION = "/saveInstitution"
        const val REMOVE_INSTITUTION = "/removeInstitution"
        const val EDIT_DATE_OF_BIRTH = "/editDateOfBirth"
        const val EDIT_NAME = "/editName"
        const val EDIT_PUSH_NOTIFICATION = "editPushNotification"
        const val EDIT_AVATAR = "/editAvatar"
    }

    object BirthdayCampaignUser {
        const val EXPIRE = "/expired"
    }

    object Institution {
        const val GET_PROFILE = "/getProfile"
        const val GET_CLOSEST = "/getClosest"
        const val GET_FOR_MAP = "/getForMap"
    }

    object Offers {
        const val GET = "/get"
        const val GET_FULL_BLOCK = "/getFullBlock"
    }

    object Search {
        const val GET_SEARCH_ATTRIBUTES = "/getSearchAttributes"
        const val SEARCH = "/search"
        const val SEARCH_BY_SIMILAR_NAME = "/searchBySimilarName"
        const val FEED_STEP_ONE = "/feedStepOne"
        const val FEED_STEP_TWO = "/feedStepTwo"
        const val FEED_STEP_THREE = "/feedStepThree"
    }

    object Event {
        const val GET = "/get"
    }

    object Review {
        const val SAVE = "/save"
        const val GET = "/get"
    }

    object UserStory {
        const val ADD = "/add"
    }

    object Story {
        const val GET_ALL = "/getAll"
        const val LIKE = "/like"
        const val DISLIKE = "/dislike"
        const val NOT_INTERESTING = "/notInteresting"
    }

}

object PathsV2 {
    private const val APPLICATION_VERSION = "${CommonPaths.API}/v2"

    const val BASE_INSTITUTION = "$APPLICATION_VERSION/institution"
    const val BASE_USER = "$APPLICATION_VERSION/user"
    const val BASE_SEARCH = "$APPLICATION_VERSION/search"
    const val BASE_OFFER = "$APPLICATION_VERSION/offer"
    const val BASE_REVIEW = "$APPLICATION_VERSION/review"

    object Institution {
        const val GET_PROFILE = "/getProfile"
    }

    object User {
        const val GET_PROFILE = "/getProfile"
    }

    object Search {
        const val GET_SEARCH_ATTRIBUTES = "/getSearchAttributes"
        const val SEARCH = "/search"
    }

    object Offer {
        const val GET_FULL_BLOCK = "/getFullBlock"
    }

    object Review {
        const val SAVE = "/save"
        const val GET = "/get"
    }

}

object PathsV3 {

    private const val APPLICATION_VERSION = "${CommonPaths.API}/v3"

    const val BASE_SEARCH = "$APPLICATION_VERSION/search"
    const val BASE_INSTITUTION = "$APPLICATION_VERSION/institution"
    const val BASE_USER = "$APPLICATION_VERSION/user"

    object Search {
        const val GET_SEARCH_ATTRIBUTES = "/getSearchAttributes"
        const val SEARCH = "/search"
    }

    object Institution {
        const val GET_PROFILE = "/getProfile"
    }

    object User {
        const val GET_PROFILE = "/getProfile"
    }

}

object PathsV4 {

    private const val APPLICATION_VERSION = "${CommonPaths.API}/v4"

    const val BASE_USER = "$APPLICATION_VERSION/user"

    object User {
        const val GET_PROFILE = "/getProfile"
    }

}
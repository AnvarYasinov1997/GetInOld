package com.wellcome.main.util.variables

object CommonModerationPaths {
    const val MODERATION = "/moderation"
}

object ModerationPathsV1 {
    private const val MODERATION = "${CommonModerationPaths.MODERATION}/v1"

    const val BASE_INSTITUTION_PROFILE = "$MODERATION/institutionProfile"
    const val BASE_INSTITUTION_EDIT_REQUEST = "$MODERATION/institutionEditRequest"
    const val BASE_BIRTHDAY_CAMPAIGNS = "$MODERATION/birthdayCampaigns"

    object InstitutionProfile {
        const val GET_MODERATION_INSTITUTIONS = "/getModerationInstitutions"
        const val GET_PROFILE_FOR_EDIT = "/getProfileForEdit"
        const val GET_ARCHIVE = "/getArchive"
        const val GET_OFFER_ANALYTIC = "/getOfferAnalytic"
        const val GET_EVENT_ANALYTIC = "/getEventAnalytic"
        const val GET_OLD_OFFER_ANALYTIC = "/getOldOfferAnalytic"
        const val GET_OLD_EVENT_ANALYTIC = "/getOldEventAnalytic"
        const val GET_DASHBOARD = "/getDashboard"
        const val GET_MARKETING_CONTENT = "/getMarketingContent"
        const val GET_INSTRUCTION_CONTENT = "/getInstructionContent"
    }

    object InstitutionEditRequest {
        const val EDIT = "/edit"
    }

    object BirthdayCampaigns {
        const val ADD = "/add"
        const val GET_ALL = "/getAll"
    }

}

object WebPaths {
    private const val WEB = "/web"

    const val BASE_SEARCH = "$WEB/search"
    const val BASE_EVENT = "$WEB/event"
    const val BASE_OFFER = "$WEB/offer"
    const val BASE_INSTITUTION = "$WEB/institution"
    const val BASE_FEED = "$WEB/feed"

    object Search {
        const val GET_DYNAMIC_SEARCH = "/getDynamicSearch"
    }

    object Feed {
        const val FEED_STEP_ONE = "/feedStepOne"
        const val FEED_STEP_TWO = "/feedStepTwo"
        const val FEED_STEP_THREE = "/feedStepThree"
    }

    object Offer {
        const val GET_FULL_BLOCK = "/getFullBlock"
        const val GET_ALL= "/getAll"
    }

    object Institution {
        const val GET_PROFILE = "/getProfile"
    }

    object Event {
        const val GET = "/get"
        const val GET_ALL = "/getAll"
    }

}

object Paths {
    private const val ADMIN = "/admin"

    const val BASE_SYSTEM_MANAGEMENT = "$ADMIN/systemManagement"

    const val BASE_AUTH = "/auth"
    const val BASE_LOCALITY = "$ADMIN/locality"
    const val BASE_USER = "$ADMIN/user"
    const val BASE_INSTITUTION = "$ADMIN/institution"
    const val BASE_INSTITUTION_ATTRIBUTES = "$ADMIN/institutionAttributes"
    const val BASE_CATEGORY = "$ADMIN/category"
    const val BASE_SELECTION_OFFER = "$ADMIN/selectionOffer"
    const val BASE_STORY = "$ADMIN/story"
    const val BASE_SELECTION = "$ADMIN/selection"
    const val BASE_USER_STORY = "$ADMIN/userStory"
    const val BASE_MARKETING="$ADMIN/marketing"
    const val BASE_INSTRUCTION = "$ADMIN/instruction"

    object Instruction {
        const val ADD = "/add"
        const val GET_ALL = "/getAll"
    }

    object Marketing {
        const val ADD = "/add"
        const val GET_ALL = "/getAll"
    }

    object SystemManagement {
        const val CLEAR_ALL_CACHES = "/clearAllCaches"
        const val RESTART_APPLICATION = "/restartApplication"
        const val SWITCH_CACHE = "/switchCache"
    }

    object Auth {
        const val INIT_USER = "/initUser"
        const val TOKEN = "/token"
        const val AUTHORIZE_INSTITUTION = "/authorizeInstitution"
    }

    object User {
        const val CHANGE_PASSWORD = "/updatePassword"
    }

    object Locality {
        const val GET_ALL = "/getAll"
    }

    object InstitutionAttributes {
        const val GET = "/get"
    }

    object SelectionOffer {
        const val SELECT = "/select"
        const val GET_ALL = "/getAll"
        const val REMOVE = "/remove"
    }

    object Selection {
        const val ADD = "/add"
        const val GET_ALL = "/getAll"
        const val REMOVE = "/remove"
    }

    object Institution {
        const val CREATE_FROM_FILE = "/createFromFile"
        const val FILL = "/fill"
        const val ADD = "/add"
        const val EDIT = "/edit"
        const val GET_ONE_BY_LOCALITY = "/getOne"
        const val GET_NAMES = "/getNames"
        const val GET_ALL_NAMES = "/getAllNames"
        const val GET_BY_ID = "/getById"
        const val BLOCK = "/block"
    }

    object Category {
        const val GET_CATEGORY_NAMES = "/getNames"
    }

    object UserStory {
        const val GET_ALL = "/getAll"
        const val APPROVE = "/approve"
        const val REMOVE = "/remove"
    }

    object Story {
        const val GET_ALL = "/getAll"
        const val ADD = "/add"
        const val GET_TYPES = "/getTypes"
    }

}

object Query {
    const val LAT = "lat"
    const val LON = "lon"
    const val INSTITUTION_ID = "institutionId"
    const val LOCALITY_ID = "localityId"
    const val CATEGORY_ID = "categoryId"
    const val DATA_FILE = "dataFile"
    const val INSTITUTION_ACCESS_KEY = "institutionAccessKey"
    const val LOGIN = "login"
    const val SWITCH_STATE = "switchState"
}
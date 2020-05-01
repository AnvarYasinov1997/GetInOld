package com.wellcome.main.util.variables

enum class MobileAnalyticEvent(val value: String, val conversion: Boolean) {

    MAKE_A_CALL(MobileAnalyticEventValues.MakeACall, true),
    CALL_A_TAXI(MobileAnalyticEventValues.CallATaxi, true),
    SHOW_MAP(MobileAnalyticEventValues.ShowMap, true),
    SHARE(MobileAnalyticEventValues.Share, true),
    INSTITUTION_PROFILE_CLICK(MobileAnalyticEventValues.InstitutionProfileClick, false),
    INSTITUTION_SEEN(MobileAnalyticEventValues.InstitutionSeen, false),
    OFFER_EXPAND(MobileAnalyticEventValues.OfferExpand, false),
    OFFER_SEEN(MobileAnalyticEventValues.OfferSeen, false),
    EVENT_EXPAND(MobileAnalyticEventValues.EventExpand, false),
    EVENT_SEEN(MobileAnalyticEventValues.EventSeen, false),
    BUILD_ROAD(MobileAnalyticEventValues.BuildRoute, true),
    BIRTHDAY_CAMPAIGN_SEEN(MobileAnalyticEventValues.BirthdayCampaignSeen, false),
    BIRTHDAY_CAMPAIGN_USER(MobileAnalyticEventValues.BirthdayCampaignUse, true);


    object MobileAnalyticEventValues {
        //actions
        const val MakeACall = "Action_MakeACall"
        const val CallATaxi = "Action_CallATaxi"
        const val ShowMap = "Action_ShowMap"
        const val Share = "Action_Share"
        const val BuildRoute = "Action_BuildRoute"
        const val BirthdayCampaignUse = "Action_BirthdayCampaignUse"

        //also matter
        const val InstitutionProfileClick = "Institution_ProfileClick"
        const val InstitutionSeen = "Institution_Seen"
        const val OfferExpand = "Offer_Expand"
        const val OfferSeen = "Offer_Seen"
        const val EventExpand = "Event_Expand"
        const val EventSeen = "Event_Seen"
        const val BirthdayCampaignSeen = "BirthdayCampaign_Seen"
    }
}

object AnalyticEventValues {
    //actions
    const val MakeACall = "Action_MakeACall"
    const val CallATaxi = "Action_CallATaxi"
    const val ShowMap = "Action_ShowMap"
    const val Share = "Action_Share"
    const val BuildRoute = "Action_BuildRoute"
    const val BirthdayCampaignSeen = "BirthdayCampaign_Seen"

    //also matter
    const val InstitutionProfileClick = "Institution_ProfileClick"
    const val InstitutionSeen = "Institution_Seen"
    const val OfferExpand = "Offer_Expand"
    const val OfferSeen = "Offer_Seen"
    const val EventExpand = "Event_Expand"
    const val EventSeen = "Event_Seen"
    const val BirthdayCampaignUse = "Action_BirthdayCampaignUse"
}

object ParamKey {
    const val INSTITUTION_ID = "institution_id"
    const val OFFER_ID = "offer_id"
    const val EVENT_ID = "event_id"
    const val EVENT_NAME = "event_name"
    const val EVENT_TIMESTAMP = "event_timestamp"
    const val EVENT_DATE= "event_date"
    const val BIRTHDAY_CAMPAIGN_ID = "birthday_campaign_id"
}
